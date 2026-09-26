package com.diligence.extraction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExtractionResultRepository extends JpaRepository<ExtractionResult, UUID> {

    @Query("SELECT e FROM ExtractionResult e WHERE e.documentEntity.id = :documentId")
    List<ExtractionResult> findByDocumentId(@Param("documentId") UUID documentId);

    @Query("SELECT e FROM ExtractionResult e WHERE e.caseEntity.id = :caseId AND e.success = :success")
    List<ExtractionResult> findByCaseIdAndSuccess(@Param("caseId") UUID caseId, @Param("success") Boolean success);

    List<ExtractionResult> findByPromptVersion(String promptVersion);

    List<ExtractionResult> findByModelUsed(String modelUsed);

    @Query("SELECT e FROM ExtractionResult e WHERE e.caseEntity.id = :caseId")
    List<ExtractionResult> findByCaseId(@Param("caseId") UUID caseId);
}
