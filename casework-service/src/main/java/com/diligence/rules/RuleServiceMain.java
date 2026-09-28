package com.diligence.rules;

public class RuleServiceMain {

    public static void main(String[] args) {
        System.out.println("=== Rule Service - Demo Mode ===\n");
        System.out.println("RuleService requires Spring Boot context and database repositories.");
        System.out.println("To test rule evaluation logic, run the application and use REST API:\n");

        System.out.println("1. Create a case:");
        System.out.println("   POST /api/cases");
        System.out.println("   {\"supplierName\": \"Test Company\", \"supplierAbn\": \"12345678901\"}\n");

        System.out.println("2. Evaluate rules:");
        System.out.println("   POST /api/cases/{caseId}/rules/evaluate-all\n");

        System.out.println("3. View results:");
        System.out.println("   GET /api/cases/{caseId}/rules/results\n");

        System.out.println("Or use the full Spring Boot application:");
        System.out.println("   mvn spring-boot:run");
    }
}
