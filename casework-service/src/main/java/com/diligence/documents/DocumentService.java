package com.diligence.documents;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.diligence.casework.CaseRepository;
import com.diligence.casework.DueDiligenceCase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CaseRepository caseRepository;

    @Value("${app.upload.dir:./data/case-documents}")
    private String uploadDir;

    public Document uploadDocument(UUID caseId, MultipartFile file, String uploadedBy) throws IOException {
        // Validate case exists
        DueDiligenceCase caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        // Validate file
        validateFile(file);

        // Create upload directory for this case
        Path casePath = Paths.get(uploadDir, caseId.toString());
        Files.createDirectories(casePath);

        // Save file with UUID-based name to avoid collisions
        String fileName = file.getOriginalFilename();
        String storageName = UUID.randomUUID() + "-" + fileName;
        Path filePath = casePath.resolve(storageName);
        Files.write(filePath, file.getBytes());

        // Create document record
        Document document = new Document(
                caseEntity,
                fileName,
                file.getContentType(),
                file.getSize(),
                filePath.toString(),
                uploadedBy
        );

        return documentRepository.save(document);
    }

    public List<Document> getDocumentsByCase(UUID caseId) {
        return documentRepository.findByCaseEntityId(caseId);
    }

    public Document getDocumentById(UUID documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public void deleteDocument(UUID documentId) throws IOException {
        Document document = getDocumentById(documentId);

        // Delete file from disk
        Path filePath = Paths.get(document.getFilePath());
        Files.deleteIfExists(filePath);

        // Delete database record
        documentRepository.deleteById(documentId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new RuntimeException("File exceeds maximum size of 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }
    }
}
