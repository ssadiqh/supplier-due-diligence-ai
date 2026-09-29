package com.diligence.tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContext;
import com.diligence.SupplierDueDiligenceApplication;

public class RealABNLookupMain {

    public static void main(String[] args) {
        // Separate Spring properties from user ABN input
        // Spring args: --abn.mode=live, user args: ABN number (11 digits)
        java.util.List<String> springArgsList = new java.util.ArrayList<>();
        String userAbn = null;

        for (String arg : args) {
            if (arg.startsWith("--")) {
                springArgsList.add(arg);
            } else if (arg.matches("\\d{11}")) {
                userAbn = arg;
            }
        }

        // Add default Spring args to disable database
        springArgsList.add("--spring.jpa.hibernate.ddl-auto=none");
        springArgsList.add("--spring.flyway.enabled=false");

        // Initialize Spring context (without web server) to get ABNLookupService with RestTemplate and GUID config
        SpringApplication app = new SpringApplication(SupplierDueDiligenceApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        ApplicationContext context = app.run(springArgsList.toArray(new String[0]));

        // Get the ABN lookup service (will be live if abn.mode=live and GUID configured)
        IABNLookupService abnService = context.getBean(IABNLookupService.class);

        System.out.println("=== ABN Lookup Service - Real Mode ===\n");
        System.out.println("Mode: " + abnService.getMode() + "\n");

        if (userAbn == null) {
            demoMode(abnService);
        } else {
            testMode(abnService, userAbn);
        }
    }

    private static void demoMode(IABNLookupService abnService) {
        System.out.println("--- Demo Mode: Testing Real ABN Lookups ---\n");

        testABN("50110219460", "Apple Australia Pty Ltd", abnService);
        testABN("99999999999", "Invalid/Not Found", abnService);
    }

    private static void testMode(IABNLookupService abnService, String abn) {
        System.out.println("Testing ABN: " + abn + "\n");
        testABN(abn, null, abnService);
    }

    private static void testABN(String abn, String description, IABNLookupService abnService) {
        System.out.println("--- ABN: " + abn + " ---");
        if (description != null) {
            System.out.println("Expected: " + description);
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
            System.out.println("Error: " + e.getMessage());
            System.out.println("  Mode: " + abnService.getMode());
            System.out.println();
        }
    }
}
