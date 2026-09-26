package com.diligence.extraction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExtractionResultRepository extends JpaRepository<ExtractionResult, UUID> {

    List<ExtractionResult> findByDocumentId(UUID documentId);

    List<ExtractionResult> findByCaseIdAndSuccess(UUID caseId, Boolean success);

    List<ExtractionResult> findByPromptVersion(String promptVersion);

    List<ExtractionResult> findByModelUsed(String modelUsed);

    List<ExtractionResult> findByCaseId(UUID caseId);
}
