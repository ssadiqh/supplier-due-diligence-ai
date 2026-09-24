package com.diligence.rules;

import com.diligence.casework.CaseRepository;
import com.diligence.casework.DueDiligenceCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("RuleService Tests")
class RuleServiceTest {

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private RuleResultRepository ruleResultRepository;

    @Mock
    private CaseRepository caseRepository;

    private RuleService ruleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ruleService = new RuleService(ruleRepository, ruleResultRepository, caseRepository);
    }

    @Test
    @DisplayName("Should create a new rule")
    void testCreateRule() {
        Rule mockRule = new Rule("Sanction Check", "Check against sanctions list",
                                 RuleType.SANCTION_CHECK, RuleSeverity.CRITICAL);
        mockRule.setId(UUID.randomUUID());

        when(ruleRepository.save(any())).thenReturn(mockRule);

        Rule created = ruleService.createRule("Sanction Check", "Check against sanctions list",
                                              RuleType.SANCTION_CHECK, RuleSeverity.CRITICAL);

        assertNotNull(created);
        assertEquals("Sanction Check", created.getName());
        assertEquals(RuleType.SANCTION_CHECK, created.getRuleType());
        assertEquals(RuleSeverity.CRITICAL, created.getSeverity());
    }

    @Test
    @DisplayName("Should evaluate ABN validation rule")
    void testEvaluateAbnValidationRule() {
        UUID caseId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();

        DueDiligenceCase caseEntity = new DueDiligenceCase("Test Corp", "analyst@example.com");
        caseEntity.setId(caseId);
        caseEntity.setSupplierAbn("12345678901");  // Valid 11-digit ABN

        Rule rule = new Rule("ABN Validation", "Validate ABN", RuleType.ABN_VALIDATION, RuleSeverity.HIGH);
        rule.setId(ruleId);

        when(caseRepository.findById(caseId)).thenReturn(Optional.of(caseEntity));
        when(ruleRepository.findById(ruleId)).thenReturn(Optional.of(rule));
        when(ruleResultRepository.save(any())).thenAnswer(inv -> {
            RuleResult result = inv.getArgument(0);
            result.setId(UUID.randomUUID());
            return result;
        });

        RuleResult result = ruleService.evaluateRule(caseId, ruleId, "ABN valid", "ABN check passed");

        assertNotNull(result);
        assertTrue(result.getPassed());
        assertEquals(ruleId, result.getRule().getId());
    }

    @Test
    @DisplayName("Should fail ABN validation for invalid ABN")
    void testAbnValidationFails() {
        UUID caseId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();

        DueDiligenceCase caseEntity = new DueDiligenceCase("Test Corp", "analyst@example.com");
        caseEntity.setId(caseId);
        caseEntity.setSupplierAbn("123");  // Invalid ABN (too short)

        Rule rule = new Rule("ABN Validation", "Validate ABN", RuleType.ABN_VALIDATION, RuleSeverity.HIGH);
        rule.setId(ruleId);

        when(caseRepository.findById(caseId)).thenReturn(Optional.of(caseEntity));
        when(ruleRepository.findById(ruleId)).thenReturn(Optional.of(rule));
        when(ruleResultRepository.save(any())).thenAnswer(inv -> {
            RuleResult result = inv.getArgument(0);
            result.setId(UUID.randomUUID());
            return result;
        });

        RuleResult result = ruleService.evaluateRule(caseId, ruleId, "ABN invalid", "ABN check failed");

        assertNotNull(result);
        assertFalse(result.getPassed());
    }

    @Test
    @DisplayName("Should evaluate all active rules for case")
    void testEvaluateAllRulesForCase() {
        UUID caseId = UUID.randomUUID();
        DueDiligenceCase caseEntity = new DueDiligenceCase("Test Corp", "analyst@example.com");
        caseEntity.setId(caseId);
        caseEntity.setSupplierAbn("12345678901");
        caseEntity.setSupplierLegalName("Test Corp Legal");

        Rule rule1 = new Rule("ABN Validation", "Validate ABN", RuleType.ABN_VALIDATION, RuleSeverity.HIGH);
        rule1.setId(UUID.randomUUID());

        Rule rule2 = new Rule("Business Registration", "Verify registration",
                             RuleType.BUSINESS_REGISTRATION, RuleSeverity.MEDIUM);
        rule2.setId(UUID.randomUUID());

        when(caseRepository.findById(caseId)).thenReturn(Optional.of(caseEntity));
        when(ruleRepository.findByIsActiveTrue()).thenReturn(List.of(rule1, rule2));
        when(ruleResultRepository.save(any())).thenAnswer(inv -> {
            RuleResult result = inv.getArgument(0);
            result.setId(UUID.randomUUID());
            return result;
        });

        List<RuleResult> results = ruleService.evaluateAllRulesForCase(caseId);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.get(0).getPassed());  // ABN validation should pass
        assertTrue(results.get(1).getPassed());  // Business registration should pass
    }
}
