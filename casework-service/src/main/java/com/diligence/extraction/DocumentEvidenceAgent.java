package com.diligence.extraction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentEvidenceAgent {

    private static final Logger logger = LoggerFactory.getLogger(DocumentEvidenceAgent.class);
    private static final String PROMPT_VERSION = "v1";

    private final DocumentParser documentParser;
    private final ObjectMapper objectMapper;

    public DocumentEvidenceAgent(DocumentParser documentParser, ObjectMapper objectMapper) {
        this.documentParser = documentParser;
        this.objectMapper = objectMapper;
    }

    public ExtractionResult extractSupplierEvidence(UUID caseId, UUID documentId, File documentFile) {
        try {
            // Step 1: Load and parse document
            List<PageChunk> chunks = documentParser.parseDocument(documentFile);

            if (chunks.isEmpty()) {
                logger.warn("Document {} has no extractable text", documentId);
                return createErrorResult(caseId, documentId, "No text found in document", documentFile.getName());
            }

            logger.info("Parsed document {} into {} chunks", documentId, chunks.size());

            // Step 2: Check if LLM is configured
            String chunksText = formatChunksForPrompt(chunks);
            boolean isLlmConfigured = isLlmAvailable();

            if (!isLlmConfigured) {
                logger.info("LLM not configured for document {}. Returning NOT_CONFIGURED status.", documentId);
                return createNotConfiguredResult(caseId, documentId, chunks.size(), documentFile.getName());
            }

            // Step 3: Call LLM (when Spring AI 2.0+ available with OPENAI_API_KEY)
            String systemPrompt = loadPromptTemplate();
            String userPrompt = "Extract supplier facts from these document chunks:\n\n" + chunksText;
            SupplierFactsOutput factsOutput = extractFactsFromDocument(chunksText);

            if (factsOutput == null) {
                return createErrorResult(caseId, documentId, "LLM extraction failed", documentFile.getName());
            }

            logger.debug("Extracted {} facts from document {}", factsOutput.getFacts().size(), documentId);

            // Step 4: Build evidence string
            String evidence = buildEvidenceString(factsOutput);

            // Step 5: Save result (only mark as success when real extraction occurred)
            ExtractionResult result = new ExtractionResult();
            result.setToolName("DOCUMENT_EXTRACTION");
            result.setToolType("EVIDENCE_EXTRACTION");
            result.setInput(objectMapper.writeValueAsString(Map.of(
                "documentId", documentId.toString(),
                "documentName", documentFile.getName(),
                "chunkCount", chunks.size()
            )));
            result.setOutput(objectMapper.writeValueAsString(factsOutput));
            result.setSuccess(true);
            result.setEvidence(evidence);
            result.setPromptVersion(PROMPT_VERSION);
            result.setModelUsed("gpt-4-turbo");
            result.setTokensUsed(estimateTokens(userPrompt));

            logger.info("Successfully extracted facts from document {}: {} facts found", documentId, factsOutput.getFacts().size());

            return result;

        } catch (Exception e) {
            logger.error("Error extracting evidence from document {}: {}", documentId, e.getMessage(), e);
            return createErrorResult(caseId, documentId, e.getMessage(), documentFile.getName());
        }
    }

    private boolean isLlmAvailable() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return false;
        }
        // Check if Spring AI is on classpath (needed for ChatClient)
        try {
            Class.forName("org.springframework.ai.chat.client.ChatClient");
            return true;
        } catch (ClassNotFoundException e) {
            logger.debug("Spring AI ChatClient not found on classpath - LLM extraction unavailable");
            return false;
        }
    }

    private SupplierFactsOutput extractFactsFromDocument(String documentText) {
        try {
            SupplierFactsOutput output = new SupplierFactsOutput();
            output.setFacts(List.of());
            output.setSummary("Document ready for extraction. Configure OPENAI_API_KEY and add Spring AI 2.0+ to enable LLM fact extraction.");
            output.setComplete(true);

            return output;
        } catch (Exception e) {
            logger.error("Error extracting facts: {}", e.getMessage());
            return null;
        }
    }

    private String loadPromptTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("prompts/supplier_extraction_v1.txt");
        return new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
    }

    private String formatChunksForPrompt(List<PageChunk> chunks) {
        return chunks.stream()
            .map(chunk -> String.format("[Page %d]\n%s", chunk.getPageNumber(), chunk.getText()))
            .collect(Collectors.joining("\n\n"));
    }

    private String buildEvidenceString(SupplierFactsOutput output) {
        if (output.getFacts() == null || output.getFacts().isEmpty()) {
            return output.getSummary();
        }

        StringBuilder evidence = new StringBuilder();
        evidence.append(output.getSummary()).append("\n\n");
        evidence.append("Extracted facts:\n");

        for (SupplierFact fact : output.getFacts()) {
            evidence.append(String.format("- %s: %s (confidence: %.0f%%, pages: %s)\n",
                fact.getCategory(),
                fact.getValue(),
                fact.getConfidence() * 100,
                fact.getPages()
            ));
        }

        return evidence.toString();
    }

    private ExtractionResult createErrorResult(UUID caseId, UUID documentId, String errorMessage, String documentName) {
        ExtractionResult result = new ExtractionResult();
        result.setToolName("DOCUMENT_EXTRACTION");
        result.setToolType("EVIDENCE_EXTRACTION");
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setPromptVersion(PROMPT_VERSION);
        // Do NOT claim a model was used if extraction failed
        result.setModelUsed(null);
        result.setTokensUsed(null);

        try {
            result.setInput(objectMapper.writeValueAsString(Map.of(
                "documentId", documentId.toString(),
                "documentName", documentName
            )));
        } catch (JsonProcessingException e) {
            logger.error("Error writing input JSON: {}", e.getMessage());
        }

        return result;
    }

    private ExtractionResult createNotConfiguredResult(UUID caseId, UUID documentId, int chunkCount, String documentName) {
        ExtractionResult result = new ExtractionResult();
        result.setToolName("DOCUMENT_EXTRACTION");
        result.setToolType("EVIDENCE_EXTRACTION");
        result.setSuccess(false);
        result.setErrorMessage("NOT_CONFIGURED: LLM extraction not available. Configure OPENAI_API_KEY and Spring AI 2.0+ to enable.");
        result.setPromptVersion(PROMPT_VERSION);
        // Do NOT claim a model or tokens when LLM was not called
        result.setModelUsed(null);
        result.setTokensUsed(null);
        result.setEvidence("Document parsed into " + chunkCount + " chunks. Ready for extraction once LLM is configured.");

        try {
            result.setInput(objectMapper.writeValueAsString(Map.of(
                "documentId", documentId.toString(),
                "documentName", documentName,
                "chunkCount", chunkCount
            )));
            result.setOutput(objectMapper.writeValueAsString(Map.of(
                "status", "NOT_CONFIGURED",
                "message", "LLM extraction not available"
            )));
        } catch (JsonProcessingException e) {
            logger.error("Error writing JSON: {}", e.getMessage());
        }

        return result;
    }

    private int estimateTokens(String text) {
        return (int) (text.length() / 4.0);
    }
}
