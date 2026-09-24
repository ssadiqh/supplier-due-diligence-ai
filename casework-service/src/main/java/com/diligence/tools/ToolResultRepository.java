package com.diligence.tools;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ToolResultRepository extends JpaRepository<ToolResult, UUID> {

    // Find all tool results for a specific case
    List<ToolResult> findByCaseEntityId(UUID caseId);

    // Find all results for a tool type
    List<ToolResult> findByToolName(String toolName);

    // Find failed results for a case
    List<ToolResult> findByCaseEntityIdAndSuccess(UUID caseId, Boolean success);
}
