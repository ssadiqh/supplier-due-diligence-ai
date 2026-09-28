package com.diligence.rules;

import com.diligence.casework.DueDiligenceCase;
import com.diligence.casework.CaseStatus;
import java.util.UUID;

public class RuleServiceMain {

    public static void main(String[] args) {
        System.out.println("=== Rule Service - Standalone Mode ===\n");

        // Create mock ABN lookup service
        MockABNLookupService abnService = new MockABNLookupService();
        RuleService ruleService = new RuleService(abnService);

        if (args.length == 0) {
            demoMode(ruleService);
        } else {
            testMode(ruleService, args[0]);
        }
    }

    private static void demoMode(RuleService ruleService) {
        System.out.println("--- Demo Mode: Testing Rule Evaluation ---\n");

        // Create test cases
        testCase("Valid ABN", "12345678901", ruleService);
        testCase("Invalid ABN", "00000000000", ruleService);
        testCase("Missing ABN", null, ruleService);
    }

    private static void testMode(RuleService ruleService, String abn) {
        System.out.println("Testing ABN: " + abn + "\n");
        testCase("Test Case", abn, ruleService);
    }

    private static void testCase(String name, String abn, RuleService ruleService) {
        DueDiligenceCase testCase = new DueDiligenceCase();
        testCase.setId(UUID.randomUUID());
        testCase.setBusinessName("Test Company");
        testCase.setSupplierAbn(abn);
        testCase.setStatus(CaseStatus.INTAKE);

        System.out.println("--- " + name + " ---");
        System.out.println("ABN: " + abn);
        System.out.println("Business: " + testCase.getBusinessName() + "\n");

        RuleResult result = ruleService.evaluateABNValidation(testCase);

        System.out.println("Result:");
        System.out.println("  Outcome: " + result.getOutcome());
        System.out.println("  Message: " + result.getExplanation());
        System.out.println("  Evaluated: " + result.getEvaluatedAt());
        System.out.println();
    }
}
