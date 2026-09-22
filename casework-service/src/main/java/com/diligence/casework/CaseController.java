package com.diligence.casework;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases")
@Validated
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @PostMapping
    public ResponseEntity<DueDiligenceCase> createCase(@Valid @RequestBody CreateCaseRequest request) {
        DueDiligenceCase dueDiligenceCase = caseService.createCase(request.supplierName(), request.requestedBy());
        return ResponseEntity.status(HttpStatus.CREATED).body(dueDiligenceCase);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DueDiligenceCase> getCaseById(@PathVariable UUID id) {
        return caseService.getCaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<DueDiligenceCase>> getAllCases() {
        List<DueDiligenceCase> cases = caseService.getAllCases();
        return ResponseEntity.ok(cases);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DueDiligenceCase> updateStatus(
            @PathVariable UUID id,
            @RequestParam CaseStatus status) {
        DueDiligenceCase dueDiligenceCase = caseService.updateCaseStatus(id, status);
        return ResponseEntity.ok(dueDiligenceCase);
    }

}
