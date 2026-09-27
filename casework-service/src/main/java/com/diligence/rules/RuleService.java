package com.diligence.rules;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.diligence.casework.CaseRepository;
import com.diligence.casework.DueDiligenceCase;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RuleService {

    private static final Logger logger = LoggerFactory.getLogger(RuleService.class);
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
        RuleOutcome outcome = evaluateRuleLogic(rule, caseEntity);

        RuleResult result = new RuleResult(caseEntity, rule, outcome, details, evidence);
        return ruleResultRepository.save(result);
    }

    public List<RuleResult> evaluateAllRulesForCase(UUID caseId) {
        DueDiligenceCase caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        List<Rule> activeRules = getAllActiveRules();
        List<RuleResult> results = new java.util.ArrayList<>();

        for (Rule rule : activeRules) {
            RuleOutcome outcome = evaluateRuleLogic(rule, caseEntity);
            String details = "Auto-evaluated rule: " + rule.getName();
            String evidence = "Rule evaluation on case " + caseId;

            RuleResult result = new RuleResult(caseEntity, rule, outcome, details, evidence);
            results.add(ruleResultRepository.save(result));
        }

        return results;
    }

    public List<RuleResult> getRuleResultsForCase(UUID caseId) {
        return ruleResultRepository.findByCaseEntityId(caseId);
    }

    public List<RuleResult> getFailedRulesForCase(UUID caseId) {
        return ruleResultRepository.findByCaseEntityIdAndOutcome(caseId, RuleOutcome.FAIL);
    }

    public List<RuleResult> getPassedRulesForCase(UUID caseId) {
        return ruleResultRepository.findByCaseEntityIdAndOutcome(caseId, RuleOutcome.PASS);
    }

    private RuleOutcome evaluateRuleLogic(Rule rule, DueDiligenceCase caseEntity) {
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
    private RuleOutcome evaluateSanctionCheck(DueDiligenceCase caseEntity) {
        // Awaiting real sanctions dataset integration (Phase 6+)
        return RuleOutcome.NOT_EVALUATED;
    }

    private RuleOutcome evaluateAbnValidation(DueDiligenceCase caseEntity) {
        if (caseEntity.getSupplierAbn() == null || caseEntity.getSupplierAbn().isBlank()) {
            return RuleOutcome.NOT_APPLICABLE;
        }
        // Consumes ToolResult from Phase 4 SupplierVerificationService
        return RuleOutcome.NOT_EVALUATED;
    }

    private RuleOutcome evaluateBusinessRegistration(DueDiligenceCase caseEntity) {
        if (caseEntity.getSupplierLegalName() != null && !caseEntity.getSupplierLegalName().isBlank()) {
            return RuleOutcome.PASS;
        }
        return RuleOutcome.NOT_EVALUATED;
    }

    private RuleOutcome evaluateFinancialThreshold(DueDiligenceCase caseEntity) {
        // Awaiting financial data integration (Phase 6+)
        return RuleOutcome.NOT_EVALUATED;
    }

    private RuleOutcome evaluateIndustryRestriction(DueDiligenceCase caseEntity) {
        // Awaiting industry classification service (Phase 6+)
        return RuleOutcome.NOT_EVALUATED;
    }

    private RuleOutcome evaluateComplianceHistory(DueDiligenceCase caseEntity) {
        // Awaiting compliance history database (Phase 6+)
        return RuleOutcome.NOT_EVALUATED;
    }

    private RuleOutcome evaluateBeneficialOwnership(DueDiligenceCase caseEntity) {
        // Awaiting beneficial ownership records (Phase 7+)
        return RuleOutcome.NOT_EVALUATED;
    }

    private RuleOutcome evaluatePoliticalExposure(DueDiligenceCase caseEntity) {
        // Awaiting PEP database integration (Phase 7+)
        return RuleOutcome.NOT_EVALUATED;
    }
}
