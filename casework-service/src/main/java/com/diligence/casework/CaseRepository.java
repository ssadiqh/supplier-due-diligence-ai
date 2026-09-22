package com.diligence.casework;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CaseRepository extends JpaRepository<DueDiligenceCase, UUID> {
}
