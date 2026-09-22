package com.diligence.documents;

import com.diligence.casework.CaseRepository;
import com.diligence.casework.DueDiligenceCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("DocumentService Tests")
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private CaseRepository caseRepository;

    private DocumentService documentService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        documentService = new DocumentService(documentRepository, caseRepository);
        // Override upload directory to temp directory for testing
        ReflectionTestUtils.setField(documentService, "uploadDir", tempDir.toString());
    }

    @Test
    @DisplayName("Should upload a valid PDF document")
    void testUploadDocument() throws IOException {
        UUID caseId = UUID.randomUUID();
        DueDiligenceCase mockCase = new DueDiligenceCase("Test Corp", "analyst@example.com");
        mockCase.setId(caseId);

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        when(caseRepository.findById(caseId)).thenReturn(Optional.of(mockCase));
        when(documentRepository.save(any())).thenAnswer(inv -> {
            Document doc = inv.getArgument(0);
            doc.setId(UUID.randomUUID());
            return doc;
        });

        Document uploaded = documentService.uploadDocument(caseId, file, "analyst@example.com");

        assertNotNull(uploaded);
        assertEquals("test.pdf", uploaded.getFileName());
        assertEquals("application/pdf", uploaded.getFileType());
        assertEquals("analyst@example.com", uploaded.getUploadedBy());
    }

    @Test
    @DisplayName("Should reject non-PDF files")
    void testRejectNonPdfFile() {
        UUID caseId = UUID.randomUUID();
        DueDiligenceCase mockCase = new DueDiligenceCase("Test Corp", "analyst@example.com");
        mockCase.setId(caseId);

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Text content".getBytes()
        );

        when(caseRepository.findById(caseId)).thenReturn(Optional.of(mockCase));

        assertThrows(RuntimeException.class, () -> {
            documentService.uploadDocument(caseId, file, "analyst@example.com");
        });
    }

    @Test
    @DisplayName("Should reject files exceeding size limit")
    void testRejectOversizedFile() {
        UUID caseId = UUID.randomUUID();
        DueDiligenceCase mockCase = new DueDiligenceCase("Test Corp", "analyst@example.com");
        mockCase.setId(caseId);

        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MultipartFile file = new MockMultipartFile(
                "file",
                "large.pdf",
                "application/pdf",
                largeContent
        );

        when(caseRepository.findById(caseId)).thenReturn(Optional.of(mockCase));

        assertThrows(RuntimeException.class, () -> {
            documentService.uploadDocument(caseId, file, "analyst@example.com");
        });
    }
}
