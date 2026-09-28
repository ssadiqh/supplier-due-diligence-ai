package com.diligence.tools;

public class ABNLookupServiceMain {

    public static void main(String[] args) {
        System.out.println("=== ABN Lookup Service - Standalone Mode ===\n");

        ABNLookupService abnService = new MockABNLookupService();

        if (args.length == 0) {
            demoMode(abnService);
        } else {
            testMode(abnService, args[0], args.length > 1 ? args[1] : null);
        }
    }

    private static void demoMode(ABNLookupService abnService) {
        System.out.println("--- Demo Mode: Testing ABN Lookups ---\n");

        testABN("12345678901", "Test Company Ltd", abnService);
        testABN("50110219460", "Apple Australia Pty Ltd", abnService);
        testABN("INVALID", null, abnService);
    }

    private static void testMode(ABNLookupService abnService, String abn, String expectedName) {
        System.out.println("Testing ABN Lookup\n");
        testABN(abn, expectedName, abnService);
    }

    private static void testABN(String abn, String expectedName, ABNLookupService abnService) {
        System.out.println("--- ABN: " + abn + " ---");
        System.out.println("Expected: " + (expectedName != null ? expectedName : "(mock data)"));
        System.out.println();

        try {
            SupplierInfo info = abnService.verifyABN(abn);

            if (info == null) {
                System.out.println("Result: ❌ NOT FOUND\n");
                return;
            }

            System.out.println("Result:");
            System.out.println("  ABN: " + info.getAbn());
            System.out.println("  Name: " + info.getBusinessName());
            System.out.println("  Valid: " + (info.isValid() ? "✅ Yes" : "❌ No"));
            System.out.println();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + "\n");
        }
    }
}
