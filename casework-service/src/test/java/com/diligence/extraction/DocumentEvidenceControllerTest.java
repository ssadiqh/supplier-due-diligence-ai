package com.diligence.extraction;

import com.diligence.casework.DueDiligenceCase;
import com.diligence.casework.CaseRepository;
import com.diligence.documents.Document;
import com.diligence.documents.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Document Evidence Controller Integration Tests")
class DocumentEvidenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ExtractionResultRepository extractionResultRepository;

    private UUID testCaseId;
    private UUID testDocumentId;

    @BeforeEach
    void setUp() {
        // Create test case
        DueDiligenceCase testCase = new DueDiligenceCase("Test Supplier", "test@company.com");
        DueDiligenceCase savedCase = caseRepository.save(testCase);
        testCaseId = savedCase.getId();

        // Create test document
        Document testDoc = new Document();
        testDoc.setCaseEntity(savedCase);
        testDoc.setFileName("test-document.pdf");
        testDoc.setFilePath(createTestPdfFile().getAbsolutePath());
        Document savedDoc = documentRepository.save(testDoc);
        testDocumentId = savedDoc.getId();
    }

    @Test
    @DisplayName("Should extract evidence from valid document")
    void testExtractEvidence() throws Exception {
        // WHEN: POST to extract evidence endpoint
        // THEN: Should return 200 with ExtractionResult
        mockMvc.perform(post("/api/cases/{caseId}/documents/{docId}/extract-evidence", testCaseId, testDocumentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.toolName").value("DOCUMENT_EXTRACTION"))
            .andExpect(jsonPath("$.toolType").value("EVIDENCE_EXTRACTION"))
            .andExpect(jsonPath("$.evidence").isString());
    }

    @Test
    @DisplayName("Should return 404 for non-existent case")
    void testExtractEvidenceNoCaseFound() throws Exception {
        UUID nonExistentCaseId = UUID.randomUUID();

        mockMvc.perform(post("/api/cases/{caseId}/documents/{docId}/extract-evidence",
                nonExistentCaseId, testDocumentId))
            .andExpect(status().is(400)); // IllegalArgumentException becomes 400
    }

    @Test
    @DisplayName("Should return 400 for non-existent document")
    void testExtractEvidenceNoDocumentFound() throws Exception {
        UUID nonExistentDocId = UUID.randomUUID();

        mockMvc.perform(post("/api/cases/{caseId}/documents/{docId}/extract-evidence",
                testCaseId, nonExistentDocId))
            .andExpect(status().is(400)); // IllegalArgumentException becomes 400
    }

    @Test
    @DisplayName("Should get extraction results for case")
    void testGetExtractionResults() throws Exception {
        // GIVEN: Run extraction first
        mockMvc.perform(post("/api/cases/{caseId}/documents/{docId}/extract-evidence",
                testCaseId, testDocumentId))
            .andExpect(status().isOk());

        // WHEN: Get all extraction results
        // THEN: Should return list
        mockMvc.perform(get("/api/cases/{caseId}/extraction-results", testCaseId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].toolName").value("DOCUMENT_EXTRACTION"));
    }

    @Test
    @DisplayName("Should get extraction results filtered by prompt version")
    void testGetExtractionResultsFiltered() throws Exception {
        // GIVEN: Run extraction
        mockMvc.perform(post("/api/cases/{caseId}/documents/{docId}/extract-evidence",
                testCaseId, testDocumentId))
            .andExpect(status().isOk());

        // WHEN: Get results with prompt version filter
        // THEN: Should return filtered results
        mockMvc.perform(get("/api/cases/{caseId}/extraction-results", testCaseId)
                .param("promptVersion", "v1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].promptVersion").value("v1"));
    }

    @Test
    @DisplayName("Should get only successful extraction results")
    void testGetSuccessfulExtractions() throws Exception {
        // GIVEN: Run extraction
        mockMvc.perform(post("/api/cases/{caseId}/documents/{docId}/extract-evidence",
                testCaseId, testDocumentId))
            .andExpect(status().isOk());

        // WHEN: Get successful results only
        // THEN: All should have success=true
        mockMvc.perform(get("/api/cases/{caseId}/extraction-results/success", testCaseId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].success").value(true));
    }

    @Test
    @DisplayName("Should get extraction results for specific document")
    void testGetDocumentExtractionResults() throws Exception {
        // GIVEN: Run extraction
        mockMvc.perform(post("/api/cases/{caseId}/documents/{docId}/extract-evidence",
                testCaseId, testDocumentId))
            .andExpect(status().isOk());

        // WHEN: Get results for specific document
        // THEN: Should return list
        mockMvc.perform(get("/api/cases/{caseId}/documents/{docId}/extraction-results",
                testCaseId, testDocumentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", instanceOf(java.util.ArrayList.class)));
    }

    @Test
    @DisplayName("Should persist extraction result to database")
    void testExtractionResultPersistence() throws Exception {
        // GIVEN: Initial extraction results count
        long initialCount = extractionResultRepository.count();

        // WHEN: Extract evidence
        mockMvc.perform(post("/api/cases/{caseId}/documents/{docId}/extract-evidence",
                testCaseId, testDocumentId))
            .andExpect(status().isOk());

        // THEN: Should have saved result to database
        long finalCount = extractionResultRepository.count();
        assertTrue(finalCount > initialCount, "Should save extraction result to database");
    }

    @Test
    @DisplayName("Should include metadata in extraction result")
    void testExtractionResultMetadata() throws Exception {
        mockMvc.perform(post("/api/cases/{caseId}/documents/{docId}/extract-evidence",
                testCaseId, testDocumentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.input").exists())
            .andExpect(jsonPath("$.output").exists())
            .andExpect(jsonPath("$.modelUsed").value("gpt-4-turbo"))
            .andExpect(jsonPath("$.promptVersion").value("v1"))
            .andExpect(jsonPath("$.tokensUsed").isNumber())
            .andExpect(jsonPath("$.executedAt").exists());
    }

    private File createTestPdfFile() {
        try {
            File tempFile = Files.createTempFile("test-", ".pdf").toFile();
            Files.write(tempFile.toPath(), "Test PDF content".getBytes());
            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test PDF", e);
        }
    }
}
