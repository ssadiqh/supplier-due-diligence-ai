package com.diligence.rules;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rules")
@Validated
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    @PostMapping
    public ResponseEntity<Rule> createRule(@Valid @RequestBody CreateRuleRequest request) {
        Rule rule = ruleService.createRule(request.name(), request.description(),
                                           request.ruleType(), request.severity());
        return ResponseEntity.status(HttpStatus.CREATED).body(rule);
    }

    @GetMapping
    public ResponseEntity<List<Rule>> getAllActiveRules() {
        List<Rule> rules = ruleService.getAllActiveRules();
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rule> getRule(@PathVariable UUID id) {
        Rule rule = ruleService.getRule(id);
        return ResponseEntity.ok(rule);
    }

    @GetMapping("/by-type/{ruleType}")
    public ResponseEntity<List<Rule>> getRulesByType(@PathVariable RuleType ruleType) {
        List<Rule> rules = ruleService.getRulesByType(ruleType);
        return ResponseEntity.ok(rules);
    }

    @PostMapping("/api/cases/{caseId}/evaluate-rules")
    public ResponseEntity<List<RuleResult>> evaluateAllRules(@PathVariable UUID caseId) {
        List<RuleResult> results = ruleService.evaluateAllRulesForCase(caseId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/api/cases/{caseId}/rule-results")
    public ResponseEntity<List<RuleResult>> getRuleResults(@PathVariable UUID caseId) {
        List<RuleResult> results = ruleService.getRuleResultsForCase(caseId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/api/cases/{caseId}/rule-results/failed")
    public ResponseEntity<List<RuleResult>> getFailedRules(@PathVariable UUID caseId) {
        List<RuleResult> results = ruleService.getFailedRulesForCase(caseId);
        return ResponseEntity.ok(results);
    }
}
