package com.diligence.rules;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.diligence.casework.CaseRepository;
import com.diligence.casework.DueDiligenceCase;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleRepository ruleRepository;
    private final RuleResultRepository ruleResultRepository;
    private final CaseRepository caseRepository;

    public Rule createRule(String name, String description, RuleType ruleType, RuleSeverity severity) {
        Rule rule = new Rule(name, description, ruleType, severity);
        return ruleRepository.save(rule);
    }

    public Rule getRule(UUID ruleId) {
        return ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
    }

    public List<Rule> getAllActiveRules() {
        return ruleRepository.findByIsActiveTrue();
    }

    public List<Rule> getRulesByType(RuleType ruleType) {
        return ruleRepository.findByRuleType(ruleType);
    }

    public RuleResult evaluateRule(UUID caseId, UUID ruleId, String details, String evidence) {
        DueDiligenceCase caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        Rule rule = getRule(ruleId);

        // Evaluate based on rule type (simple deterministic logic)
        Boolean passed = evaluateRuleLogic(rule, caseEntity);

        RuleResult result = new RuleResult(caseEntity, rule, passed, details, evidence);
        return ruleResultRepository.save(result);
    }

    public List<RuleResult> evaluateAllRulesForCase(UUID caseId) {
        DueDiligenceCase caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        List<Rule> activeRules = getAllActiveRules();
        List<RuleResult> results = new java.util.ArrayList<>();

        for (Rule rule : activeRules) {
            Boolean passed = evaluateRuleLogic(rule, caseEntity);
            String details = "Auto-evaluated rule: " + rule.getName();
            String evidence = "Rule evaluation on case " + caseId;

            RuleResult result = new RuleResult(caseEntity, rule, passed, details, evidence);
            results.add(ruleResultRepository.save(result));
        }

        return results;
    }

    public List<RuleResult> getRuleResultsForCase(UUID caseId) {
        return ruleResultRepository.findByCaseEntityId(caseId);
    }

    public List<RuleResult> getFailedRulesForCase(UUID caseId) {
        return ruleResultRepository.findByCaseEntityIdAndPassed(caseId, false);
    }

    public List<RuleResult> getPassedRulesForCase(UUID caseId) {
        return ruleResultRepository.findByCaseEntityIdAndPassed(caseId, true);
    }

    private Boolean evaluateRuleLogic(Rule rule, DueDiligenceCase caseEntity) {
        return switch (rule.getRuleType()) {
            case SANCTION_CHECK -> evaluateSanctionCheck(caseEntity);
            case ABN_VALIDATION -> evaluateAbnValidation(caseEntity);
            case BUSINESS_REGISTRATION -> evaluateBusinessRegistration(caseEntity);
            case FINANCIAL_THRESHOLD -> evaluateFinancialThreshold(caseEntity);
            case INDUSTRY_RESTRICTION -> evaluateIndustryRestriction(caseEntity);
            case COMPLIANCE_HISTORY -> evaluateComplianceHistory(caseEntity);
            case BENEFICIAL_OWNERSHIP -> evaluateBeneficialOwnership(caseEntity);
            case POLITICAL_EXPOSURE -> evaluatePoliticalExposure(caseEntity);
        };
    }

    // Deterministic rule evaluation methods
    private Boolean evaluateSanctionCheck(DueDiligenceCase caseEntity) {
        // Phase 4 will integrate real sanctions API
        // For now: pass if supplierName doesn't contain blocked keywords
        return !caseEntity.getSupplierName().toLowerCase().contains("blocked");
    }

    private Boolean evaluateAbnValidation(DueDiligenceCase caseEntity) {
        // Phase 4 will integrate ABN Lookup API
        // For now: pass if ABN is provided and is 11 digits
        return caseEntity.getSupplierAbn() != null && caseEntity.getSupplierAbn().length() == 11;
    }

    private Boolean evaluateBusinessRegistration(DueDiligenceCase caseEntity) {
        // Pass if supplierLegalName is provided
        return caseEntity.getSupplierLegalName() != null && !caseEntity.getSupplierLegalName().isBlank();
    }

    private Boolean evaluateFinancialThreshold(DueDiligenceCase caseEntity) {
        // Phase 4+ will check actual financials
        // For now: always pass (no financial data in Phase 3)
        return true;
    }

    private Boolean evaluateIndustryRestriction(DueDiligenceCase caseEntity) {
        // Check if industry is in restricted list
        String industry = caseEntity.getSupplierName().toLowerCase();
        List<String> restricted = List.of("weapons", "gambling", "tobacco");
        return !restricted.stream().anyMatch(industry::contains);
    }

    private Boolean evaluateComplianceHistory(DueDiligenceCase caseEntity) {
        // Phase 6+ will check compliance history database
        // For now: always pass
        return true;
    }

    private Boolean evaluateBeneficialOwnership(DueDiligenceCase caseEntity) {
        // Phase 7+ will check beneficial ownership records
        // For now: always pass
        return true;
    }

    private Boolean evaluatePoliticalExposure(DueDiligenceCase caseEntity) {
        // Phase 7+ will check PEP database
        // For now: always pass
        return true;
    }
}
