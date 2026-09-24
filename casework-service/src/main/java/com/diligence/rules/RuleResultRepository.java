package com.diligence.rules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RuleResultRepository extends JpaRepository<RuleResult, UUID> {
    List<RuleResult> findByCaseEntityId(UUID caseId);
    List<RuleResult> findByCaseEntityIdAndPassed(UUID caseId, Boolean passed);
}
