package com.diligence.documents;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases/{caseId}/documents")
@Validated
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<?> uploadDocument(
            @PathVariable UUID caseId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "uploadedBy", defaultValue = "system") String uploadedBy) {
        try {
            Document document = documentService.uploadDocument(caseId, file, uploadedBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to process file: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Document>> getDocuments(@PathVariable UUID caseId) {
        List<Document> documents = documentService.getDocumentsByCase(caseId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<Document> getDocument(
            @PathVariable UUID caseId,
            @PathVariable UUID documentId) {
        Document document = documentService.getDocumentById(documentId);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteDocument(
            @PathVariable UUID caseId,
            @PathVariable UUID documentId) {
        try {
            documentService.deleteDocument(documentId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete file: " + e.getMessage()));
        }
    }

    static class ErrorResponse {
        public String error;

        ErrorResponse(String error) {
            this.error = error;
        }
    }
}
