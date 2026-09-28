package com.diligence.tools;

public class ABNLookupServiceMain {

    public static void main(String[] args) {
        System.out.println("=== ABN Lookup Service - Standalone Mode ===\n");

        IABNLookupService abnService = new MockABNLookupService();

        if (args.length == 0) {
            demoMode(abnService);
        } else {
            testMode(abnService, args[0]);
        }
    }

    private static void demoMode(IABNLookupService abnService) {
        System.out.println("--- Demo Mode: Testing ABN Lookups ---\n");

        testABN("12345678901", "Test Company", abnService);
        testABN("50110219460", "Apple Australia", abnService);
        testABN("11111111111", "Invalid", abnService);
    }

    private static void testMode(IABNLookupService abnService, String abn) {
        System.out.println("Testing ABN Lookup\n");
        testABN(abn, null, abnService);
    }

    private static void testABN(String abn, String expectedName, IABNLookupService abnService) {
        System.out.println("--- ABN: " + abn + " ---");
        if (expectedName != null) {
            System.out.println("Expected: " + expectedName);
        }
        System.out.println();

        try {
            ABNLookupService.ABNLookupResult result = abnService.lookupABN(abn);

            System.out.println("Result:");
            System.out.println("  ABN: " + result.getAbn());
            System.out.println("  Name: " + result.getBusinessName());
            System.out.println("  Status: " + result.getStatus());
            System.out.println("  Found: " + (result.isFound() ? "✅ Yes" : "❌ No"));
            System.out.println("  Mode: " + abnService.getMode());
            System.out.println();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + "\n");
        }
    }
}
