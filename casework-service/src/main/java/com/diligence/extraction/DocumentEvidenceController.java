package com.diligence.extraction;

import com.diligence.casework.DueDiligenceCase;
import com.diligence.casework.CaseRepository;
import com.diligence.documents.Document;
import com.diligence.documents.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases/{caseId}")
public class DocumentEvidenceController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentEvidenceController.class);

    private final DocumentEvidenceAgent evidenceAgent;
    private final ExtractionResultRepository extractionResultRepository;
    private final CaseRepository caseRepository;
    private final DocumentRepository documentRepository;

    public DocumentEvidenceController(
            DocumentEvidenceAgent evidenceAgent,
            ExtractionResultRepository extractionResultRepository,
            CaseRepository caseRepository,
            DocumentRepository documentRepository) {
        this.evidenceAgent = evidenceAgent;
        this.extractionResultRepository = extractionResultRepository;
        this.caseRepository = caseRepository;
        this.documentRepository = documentRepository;
    }

    @PostMapping("/documents/{documentId}/extract-evidence")
    public ResponseEntity<ExtractionResult> extractEvidence(
            @PathVariable UUID caseId,
            @PathVariable UUID documentId) {

        logger.info("Extracting evidence from document {} for case {}", documentId, caseId);

        // Verify case exists
        DueDiligenceCase caseEntity = caseRepository.findById(caseId)
            .orElseThrow(() -> new IllegalArgumentException("Case not found: " + caseId));

        // Verify document exists and belongs to case
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        if (!document.getCaseEntity().getId().equals(caseId)) {
            throw new IllegalArgumentException("Document does not belong to this case");
        }

        // Get document file path
        File documentFile = new File(document.getFilePath());
        if (!documentFile.exists()) {
            throw new IllegalArgumentException("Document file not found: " + document.getFilePath());
        }

        // Extract evidence
        ExtractionResult result = evidenceAgent.extractSupplierEvidence(caseId, documentId, documentFile);
        result.setCaseEntity(caseEntity);
        result.setDocumentEntity(document);

        // Save result
        ExtractionResult savedResult = extractionResultRepository.save(result);

        return ResponseEntity.ok(savedResult);
    }

    @GetMapping("/extraction-results")
    public ResponseEntity<List<ExtractionResult>> getExtractionResults(
            @PathVariable UUID caseId,
            @RequestParam(required = false) String promptVersion) {

        logger.info("Fetching extraction results for case {}", caseId);

        List<ExtractionResult> results;
        if (promptVersion != null && !promptVersion.isBlank()) {
            results = extractionResultRepository.findByCaseId(caseId).stream()
                .filter(r -> promptVersion.equals(r.getPromptVersion()))
                .toList();
        } else {
            results = extractionResultRepository.findByCaseId(caseId);
        }

        return ResponseEntity.ok(results);
    }

    @GetMapping("/extraction-results/success")
    public ResponseEntity<List<ExtractionResult>> getSuccessfulExtractions(
            @PathVariable UUID caseId) {

        logger.info("Fetching successful extraction results for case {}", caseId);

        List<ExtractionResult> results = extractionResultRepository.findByCaseIdAndSuccess(caseId, Boolean.TRUE);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/documents/{documentId}/extraction-results")
    public ResponseEntity<List<ExtractionResult>> getDocumentExtractionResults(
            @PathVariable UUID caseId,
            @PathVariable UUID documentId) {

        logger.info("Fetching extraction results for document {} in case {}", documentId, caseId);

        List<ExtractionResult> results = extractionResultRepository.findByDocumentId(documentId);

        return ResponseEntity.ok(results);
    }
}
