package com.diligence.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentEvidenceAgent {

    private static final Logger logger = LoggerFactory.getLogger(DocumentEvidenceAgent.class);
    private static final String PROMPT_VERSION = "v1";

    private final DocumentParser documentParser;
    private final ObjectMapper objectMapper;
    private final Optional<ChatModel> chatModel;

    public DocumentEvidenceAgent(
        DocumentParser documentParser,
        ObjectMapper objectMapper,
        Optional<ChatModel> chatModel
    ) {
        this.documentParser = documentParser;
        this.objectMapper = objectMapper;
        this.chatModel = chatModel;
    }

    public ExtractionResult extractSupplierEvidence(UUID caseId, UUID documentId, File documentFile) {
        try {
            List<PageChunk> chunks = documentParser.parseDocument(documentFile);

            if (chunks.isEmpty()) {
                return errorResult(caseId, documentId, "No text found in document");
            }

            if (chatModel.isEmpty()) {
                return notConfiguredResult(caseId, documentId, chunks.size());
            }

            String chunksText = chunks.stream()
                .map(c -> String.format("[Page %d]\n%s", c.getPageNumber(), c.getText()))
                .collect(Collectors.joining("\n\n"));

            SupplierFactsOutput facts = extractViaLLM(chunksText);
            if (facts == null) {
                return errorResult(caseId, documentId, "LLM extraction failed");
            }

            ExtractionResult result = new ExtractionResult();
            result.setToolName("DOCUMENT_EXTRACTION");
            result.setToolType("EVIDENCE_EXTRACTION");
            result.setSuccess(true);
            result.setModelUsed("claude-3-5-sonnet-20241022");
            result.setTokensUsed((int) (chunksText.length() / 4.0));
            result.setPromptVersion(PROMPT_VERSION);
            result.setEvidence(buildEvidence(facts));
            result.setInput(objectMapper.writeValueAsString(Map.of(
                "documentId", documentId.toString(),
                "documentName", documentFile.getName()
            )));
            result.setOutput(objectMapper.writeValueAsString(facts));

            logger.info("Extracted facts from document {}: {} facts", documentId, facts.getFacts().size());
            return result;

        } catch (Exception e) {
            logger.error("Error extracting evidence from {}: {}", documentId, e.getMessage(), e);
            return errorResult(caseId, documentId, e.getMessage());
        }
    }

    private SupplierFactsOutput extractViaLLM(String documentText) {
        try {
            PromptTemplate template = new PromptTemplate(
                "Extract supplier facts from this document:\n\n{text}"
            );
            Prompt prompt = template.create(Map.of("text", documentText));

            String response = chatModel.get()
                .call(prompt)
                .getResult()
                .getOutput()
                .getText();

            return objectMapper.readValue(response, SupplierFactsOutput.class);

        } catch (Exception e) {
            logger.error("LLM extraction failed: {}", e.getMessage());
            return null;
        }
    }

    private String buildEvidence(SupplierFactsOutput facts) {
        if (facts.getFacts() == null || facts.getFacts().isEmpty()) {
            return facts.getSummary();
        }

        StringBuilder sb = new StringBuilder(facts.getSummary()).append("\n\nExtracted facts:\n");
        for (SupplierFact fact : facts.getFacts()) {
            sb.append(String.format("- %s: %s (confidence: %.0f%%, pages: %s)\n",
                fact.getCategory(),
                fact.getValue(),
                fact.getConfidence() * 100,
                fact.getPages()
            ));
        }
        return sb.toString();
    }

    private ExtractionResult errorResult(UUID caseId, UUID documentId, String error) {
        ExtractionResult result = new ExtractionResult();
        result.setToolName("DOCUMENT_EXTRACTION");
        result.setToolType("EVIDENCE_EXTRACTION");
        result.setSuccess(false);
        result.setErrorMessage(error);
        result.setPromptVersion(PROMPT_VERSION);
        try {
            result.setInput(objectMapper.writeValueAsString(Map.of(
                "documentId", documentId.toString()
            )));
            result.setOutput(objectMapper.writeValueAsString(Map.of(
                "status", "error"
            )));
        } catch (Exception e) {
            logger.error("Error serializing result: {}", e.getMessage());
        }
        return result;
    }

    private ExtractionResult notConfiguredResult(UUID caseId, UUID documentId, int chunkCount) {
        ExtractionResult result = new ExtractionResult();
        result.setToolName("DOCUMENT_EXTRACTION");
        result.setToolType("EVIDENCE_EXTRACTION");
        result.setSuccess(false);
        result.setErrorMessage("NOT_CONFIGURED: LLM extraction not available. Configure ANTHROPIC_API_KEY to enable.");
        result.setPromptVersion(PROMPT_VERSION);
        result.setEvidence("Document parsed into " + chunkCount + " chunks. Ready for extraction once LLM is configured.");
        try {
            result.setInput(objectMapper.writeValueAsString(Map.of(
                "documentId", documentId.toString(),
                "chunkCount", chunkCount
            )));
            result.setOutput(objectMapper.writeValueAsString(Map.of(
                "status", "not_configured"
            )));
        } catch (Exception e) {
            logger.error("Error serializing result: {}", e.getMessage());
        }
        return result;
    }
}
