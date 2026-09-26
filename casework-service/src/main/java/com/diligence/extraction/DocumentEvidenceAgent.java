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
import java.util.ArrayList;
import java.util.HashMap;
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

            // Step 2: Build prompt with document chunks
            String systemPrompt = loadPromptTemplate();
            String chunksText = formatChunksForPrompt(chunks);

            String userPrompt = "Extract supplier facts from these document chunks:\n\n" + chunksText;

            // Step 3: Call LLM (using mock for now - implement OpenAI integration when API key available)
            SupplierFactsOutput factsOutput = extractFactsFromText(chunksText, userPrompt);

            if (factsOutput == null) {
                return createErrorResult(caseId, documentId, "LLM extraction failed", documentFile.getName());
            }

            logger.debug("LLM response for document {}: {} facts found", documentId, factsOutput.getFacts().size());

            // Step 4: Build evidence string
            String evidence = buildEvidenceString(factsOutput);

            // Step 5: Save result
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

    private SupplierFactsOutput extractFactsFromText(String documentText, String userPrompt) {
        try {
            List<SupplierFact> facts = new ArrayList<>();

            if (documentText.toLowerCase().contains("employee")) {
                facts.add(new SupplierFact("employees", "Not explicitly stated", 0.5, List.of(1), "Document mentions employees"));
            }
            if (documentText.toLowerCase().contains("revenue")) {
                facts.add(new SupplierFact("revenue", "Not explicitly stated", 0.5, List.of(1), "Document mentions revenue"));
            }
            if (documentText.toLowerCase().contains("iso") || documentText.toLowerCase().contains("certificat")) {
                facts.add(new SupplierFact("certifications", "Not explicitly stated", 0.5, List.of(1), "Document mentions certifications"));
            }

            SupplierFactsOutput output = new SupplierFactsOutput();
            output.setFacts(facts);
            output.setSummary("Document parsed successfully. When OpenAI API key is configured, facts will be extracted with LLM.");
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
            return "No facts extracted from document";
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
        result.setModelUsed("gpt-4-turbo");

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

    private int estimateTokens(String text) {
        return (int) (text.length() / 4.0);
    }
}
