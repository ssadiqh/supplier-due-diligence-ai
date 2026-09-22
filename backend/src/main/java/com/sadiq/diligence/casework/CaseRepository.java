package com.sadiq.diligence.casework;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseRepository extends JpaRepository<DueDiligenceCase, UUID> {
    Optional<DueDiligenceCase> findByCaseId(String caseId);
}
