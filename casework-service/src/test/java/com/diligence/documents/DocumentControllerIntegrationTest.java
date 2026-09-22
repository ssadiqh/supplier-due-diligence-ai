package com.diligence.documents;

import com.diligence.casework.CaseRepository;
import com.diligence.casework.DueDiligenceCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("DocumentController Integration Tests")
class DocumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private DocumentRepository documentRepository;

    private UUID caseId;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAll();
        caseRepository.deleteAll();

        DueDiligenceCase testCase = new DueDiligenceCase("Test Corp", "analyst@example.com");
        DueDiligenceCase savedCase = caseRepository.save(testCase);
        caseId = savedCase.getId();
    }

    @Test
    @DisplayName("Should upload a PDF document")
    void testUploadDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "evidence.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        mockMvc.perform(multipart("/api/cases/{caseId}/documents", caseId)
                .file(file)
                .param("uploadedBy", "analyst@example.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("evidence.pdf"))
                .andExpect(jsonPath("$.fileType").value("application/pdf"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("Should list documents for a case")
    void testGetDocuments() throws Exception {
        // Upload a document first
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        mockMvc.perform(multipart("/api/cases/{caseId}/documents", caseId)
                .file(file)
                .param("uploadedBy", "analyst@example.com"))
                .andExpect(status().isCreated());

        // Retrieve documents
        mockMvc.perform(get("/api/cases/{caseId}/documents", caseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].fileName").value("test.pdf"));
    }

    @Test
    @DisplayName("Should reject non-PDF files")
    void testRejectNonPdfFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "Text content".getBytes()
        );

        mockMvc.perform(multipart("/api/cases/{caseId}/documents", caseId)
                .file(file)
                .param("uploadedBy", "analyst@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Only PDF files are allowed"));
    }
}
