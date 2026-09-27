package com.diligence.documents;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByCaseEntityId(UUID caseId);

    /**
     * Find document by ID and case ID (enforces case ownership)
     * Used by controllers to verify document belongs to case before retrieval/deletion
     */
    Optional<Document> findByIdAndCaseEntityId(UUID documentId, UUID caseId);
}
