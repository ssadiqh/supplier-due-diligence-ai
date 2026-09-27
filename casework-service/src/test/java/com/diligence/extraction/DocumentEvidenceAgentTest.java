package com.diligence.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Document Extraction Service Unit Tests")
class DocumentEvidenceAgentTest {

    private DocumentExtractionService extractionService;

    @Mock
    private DocumentParser mockDocumentParser;

    private ObjectMapper objectMapper;
    private UUID testCaseId;
    private UUID testDocumentId;
    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        extractionService = new DocumentExtractionService(mockDocumentParser, objectMapper, java.util.Optional.ofNullable(null));
        testCaseId = UUID.randomUUID();
        testDocumentId = UUID.randomUUID();
        testFile = Files.createTempFile("test-", ".pdf").toFile();
    }

    @Test
    @DisplayName("Should handle document with no extractable text")
    void testExtractSupplierEvidence_NoText() {
        // GIVEN: A PDF that returns empty chunks
        when(mockDocumentParser.parseDocument(testFile)).thenReturn(List.of());

        // WHEN: Extract evidence
        ExtractionResult result = extractionService.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Result should be error
        assertNotNull(result, "Result should not be null");
        assertFalse(result.getSuccess(), "Extraction should fail for empty document");
        assertNotNull(result.getErrorMessage(), "Should have error message");
    }

    @Test
    @DisplayName("Should handle parser exceptions gracefully")
    void testExtractSupplierEvidence_ParserException() {
        // GIVEN: Parser throws exception
        when(mockDocumentParser.parseDocument(testFile))
            .thenThrow(new RuntimeException("PDF parsing failed"));

        // WHEN: Extract evidence
        ExtractionResult result = extractionService.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Should return error result, not throw exception
        assertNotNull(result, "Result should not be null");
        assertFalse(result.getSuccess(), "Should be marked as failed");
        assertNotNull(result.getErrorMessage(), "Should have error message");
    }

    @Test
    @DisplayName("Should create valid extraction result structure")
    void testExtractionResultStructure() {
        // GIVEN: A valid document with chunks
        List<PageChunk> mockChunks = List.of(
            new PageChunk(1, "Sample company text", 0, 19)
        );
        when(mockDocumentParser.parseDocument(testFile)).thenReturn(mockChunks);

        // WHEN: Extract evidence (without LLM configured)
        ExtractionResult result = extractionService.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Result should have all required fields
        assertNotNull(result, "Result should not be null");
        assertNotNull(result.getToolName(), "Should have tool name");
        assertNotNull(result.getToolType(), "Should have tool type");
        assertNotNull(result.getInput(), "Should have input");
        assertNotNull(result.getOutput(), "Should have output");
        assertNotNull(result.getSuccess(), "Should have success field");
        assertNotNull(result.getPromptVersion(), "Should have prompt version");
        assertNotNull(result.getErrorMessage(), "Should have error/status message");
        // ModelUsed should be null when LLM not configured
        assertNull(result.getModelUsed(), "Should not claim model when LLM not configured");
        assertNull(result.getTokensUsed(), "Should not estimate tokens when LLM not configured");
        assertNotNull(result.getEvidence(), "Should have evidence");
        assertFalse(result.getSuccess(), "Should be NOT_CONFIGURED, not success");
    }

    @Test
    @DisplayName("Should return NOT_CONFIGURED when LLM unavailable")
    void testTokenEstimation() {
        // GIVEN: A document with known text size
        String textContent = "A".repeat(400); // 400 chars
        List<PageChunk> mockChunks = List.of(
            new PageChunk(1, textContent, 0, 400)
        );
        when(mockDocumentParser.parseDocument(testFile)).thenReturn(mockChunks);

        // WHEN: Extract evidence (without LLM configured)
        ExtractionResult result = extractionService.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Should indicate NOT_CONFIGURED
        assertFalse(result.getSuccess());
        assertTrue(result.getErrorMessage().contains("NOT_CONFIGURED"));
        assertTrue(result.getErrorMessage().contains("ANTHROPIC_API_KEY"), "Should mention ANTHROPIC_API_KEY configuration");
        assertNull(result.getTokensUsed(), "Should not estimate tokens when LLM not configured");
    }

    @Test
    @DisplayName("Should handle multiple pages correctly")
    void testMultiPageExtraction() {
        // GIVEN: A multi-page document
        List<PageChunk> mockChunks = List.of(
            new PageChunk(1, "Page 1 content", 0, 14),
            new PageChunk(2, "Page 2 content", 14, 28),
            new PageChunk(3, "Page 3 content", 28, 42)
        );
        when(mockDocumentParser.parseDocument(testFile)).thenReturn(mockChunks);

        // WHEN: Extract evidence (without LLM configured)
        ExtractionResult result = extractionService.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Should process all pages but return NOT_CONFIGURED
        assertFalse(result.getSuccess(), "Should be NOT_CONFIGURED");
        assertNotNull(result.getOutput(), "Should have output");
        assertTrue(result.getEvidence().length() > 0, "Should have evidence with chunk summary");
        assertTrue(result.getErrorMessage().contains("NOT_CONFIGURED"));
    }

    @Test
    @DisplayName("Should set correct tool metadata (NOT_CONFIGURED status)")
    void testToolMetadata() {
        // GIVEN: Valid document
        List<PageChunk> mockChunks = List.of(new PageChunk(1, "text", 0, 4));
        when(mockDocumentParser.parseDocument(testFile)).thenReturn(mockChunks);

        // WHEN: Extract evidence without LLM configured
        ExtractionResult result = extractionService.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Tool metadata should be correct
        assertEquals("DOCUMENT_EXTRACTION", result.getToolName());
        assertEquals("EVIDENCE_EXTRACTION", result.getToolType());
        assertEquals("v1", result.getPromptVersion());
        // ModelUsed should be null when LLM not configured
        assertNull(result.getModelUsed(), "Should not claim model when NOT_CONFIGURED");
        assertFalse(result.getSuccess());
    }

    @Test
    @DisplayName("Should format evidence string properly (NOT_CONFIGURED)")
    void testEvidenceFormatting() {
        // GIVEN: Document with chunks
        List<PageChunk> mockChunks = List.of(
            new PageChunk(1, "Company has data", 0, 16)
        );
        when(mockDocumentParser.parseDocument(testFile)).thenReturn(mockChunks);

        // WHEN: Extract evidence without LLM configured
        ExtractionResult result = extractionService.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Evidence should contain configuration message
        assertFalse(result.getSuccess());
        assertNotNull(result.getEvidence());
        assertTrue(result.getEvidence().contains("Ready for extraction"));
        assertTrue(result.getEvidence().length() > 0);
    }
}
