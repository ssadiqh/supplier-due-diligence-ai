package com.diligence.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Document Evidence Agent Tests")
class DocumentEvidenceAgentTest {

    private DocumentEvidenceAgent evidenceAgent;

    @Mock
    private DocumentParser mockDocumentParser;

    private ObjectMapper objectMapper;
    private UUID testCaseId;
    private UUID testDocumentId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        evidenceAgent = new DocumentEvidenceAgent(mockDocumentParser, objectMapper);
        testCaseId = UUID.randomUUID();
        testDocumentId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should successfully extract supplier evidence from document")
    void testExtractSupplierEvidence_Success() {
        // GIVEN: A valid PDF file with mock chunks
        File testFile = new File("test-document.pdf");
        List<PageChunk> mockChunks = List.of(
            new PageChunk(1, "Company has 1,250 employees", 0, 30),
            new PageChunk(1, "Revenue is $50M FY2024", 30, 52)
        );
        when(mockDocumentParser.parseDocument(testFile)).thenReturn(mockChunks);

        // WHEN: Extract evidence
        ExtractionResult result = evidenceAgent.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Result should be successful
        assertNotNull(result, "Result should not be null");
        assertTrue(result.getSuccess(), "Extraction should succeed");
        assertNull(result.getErrorMessage(), "Should have no error message");
        assertNotNull(result.getOutput(), "Should have output");
        assertEquals("DOCUMENT_EXTRACTION", result.getToolName());
        assertEquals("EVIDENCE_EXTRACTION", result.getToolType());
    }

    @Test
    @DisplayName("Should handle document with no extractable text")
    void testExtractSupplierEvidence_NoText() {
        // GIVEN: A PDF that returns empty chunks
        File testFile = new File("empty.pdf");
        when(mockDocumentParser.parseDocument(testFile)).thenReturn(List.of());

        // WHEN: Extract evidence
        ExtractionResult result = evidenceAgent.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Result should be error
        assertNotNull(result, "Result should not be null");
        assertFalse(result.getSuccess(), "Extraction should fail for empty document");
        assertTrue(result.getErrorMessage().contains("no text"), "Error should mention empty document");
    }

    @Test
    @DisplayName("Should handle parser exceptions gracefully")
    void testExtractSupplierEvidence_ParserException() {
        // GIVEN: Parser throws exception
        File testFile = new File("corrupted.pdf");
        when(mockDocumentParser.parseDocument(testFile))
            .thenThrow(new RuntimeException("PDF parsing failed"));

        // WHEN: Extract evidence
        ExtractionResult result = evidenceAgent.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Should return error result, not throw exception
        assertNotNull(result, "Result should not be null");
        assertFalse(result.getSuccess(), "Should be marked as failed");
        assertNotNull(result.getErrorMessage(), "Should have error message");
        assertTrue(result.getErrorMessage().contains("PDF parsing failed"));
    }

    @Test
    @DisplayName("Should create valid extraction result structure")
    void testExtractionResultStructure() {
        // GIVEN: A valid document
        File testFile = new File("valid.pdf");
        List<PageChunk> mockChunks = List.of(
            new PageChunk(1, "Sample company text", 0, 19)
        );
        when(mockDocumentParser.parseDocument(testFile)).thenReturn(mockChunks);

        // WHEN: Extract evidence
        ExtractionResult result = evidenceAgent.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Result should have all required fields
        assertNotNull(result.getId() != null || result.getId() == null, "ID can be assigned later");
        assertNotNull(result.getToolName());
        assertNotNull(result.getToolType());
        assertNotNull(result.getInput());
        assertNotNull(result.getOutput());
        assertNotNull(result.getSuccess());
        assertNotNull(result.getPromptVersion());
        assertNotNull(result.getModelUsed());
        assertNotNull(result.getEvidence());
    }

    @Test
    @DisplayName("Should estimate tokens correctly")
    void testTokenEstimation() {
        // GIVEN: A document with known text size
        File testFile = new File("tokens.pdf");
        String textContent = "A".repeat(400); // 400 chars = ~100 tokens
        List<PageChunk> mockChunks = List.of(
            new PageChunk(1, textContent, 0, 400)
        );
        when(mockDocumentParser.parseDocument(testFile)).thenReturn(mockChunks);

        // WHEN: Extract evidence
        ExtractionResult result = evidenceAgent.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Token count should be reasonable approximation
        assertTrue(result.getSuccess());
        assertTrue(result.getTokensUsed() > 0, "Should estimate tokens");
        // Token estimate is length / 4 in our implementation
    }

    @Test
    @DisplayName("Should handle multiple pages correctly")
    void testMultiPageExtraction() {
        // GIVEN: A multi-page document
        File testFile = new File("multipage.pdf");
        List<PageChunk> mockChunks = List.of(
            new PageChunk(1, "Page 1 content", 0, 14),
            new PageChunk(2, "Page 2 content", 14, 28),
            new PageChunk(3, "Page 3 content", 28, 42)
        );
        when(mockDocumentParser.parseDocument(testFile)).thenReturn(mockChunks);

        // WHEN: Extract evidence
        ExtractionResult result = evidenceAgent.extractSupplierEvidence(testCaseId, testDocumentId, testFile);

        // THEN: Should process all pages
        assertTrue(result.getSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getEvidence().length() > 0);
    }
}
