package com.diligence.tools;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases")
@Validated
@RequiredArgsConstructor
public class ToolController {

    private final SupplierVerificationService supplierVerificationService;
    private final ToolResultRepository toolResultRepository;

    @PostMapping("/{caseId}/verify-supplier")
    public ResponseEntity<ToolResult> verifySupplier(@PathVariable UUID caseId) {
        // HTTP: POST /api/cases/{caseId}/verify-supplier
        // Calls ABN Lookup tool to verify supplier
        // Returns: Tool execution result with match status and evidence

        ToolResult result = supplierVerificationService.verifySupplier(caseId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{caseId}/tool-results")
    public ResponseEntity<List<ToolResult>> getToolResults(@PathVariable UUID caseId) {
        // HTTP: GET /api/cases/{caseId}/tool-results
        // Returns: All tool execution results for this case

        List<ToolResult> results = toolResultRepository.findByCaseEntityId(caseId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{caseId}/tool-results/successful")
    public ResponseEntity<List<ToolResult>> getSuccessfulToolResults(@PathVariable UUID caseId) {
        // HTTP: GET /api/cases/{caseId}/tool-results/successful
        // Returns: Only successful tool results

        List<ToolResult> results = toolResultRepository.findByCaseEntityIdAndSuccess(caseId, true);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{caseId}/tool-results/failed")
    public ResponseEntity<List<ToolResult>> getFailedToolResults(@PathVariable UUID caseId) {
        // HTTP: GET /api/cases/{caseId}/tool-results/failed
        // Returns: Only failed tool results

        List<ToolResult> results = toolResultRepository.findByCaseEntityIdAndSuccess(caseId, false);
        return ResponseEntity.ok(results);
    }
}
