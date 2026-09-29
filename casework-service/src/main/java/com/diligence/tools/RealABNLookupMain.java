package com.diligence.tools;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.MediaType;
import java.lang.reflect.Field;
import java.util.Arrays;

public class RealABNLookupMain {

    public static void main(String[] args) throws Exception {
        // Separate user ABN input from properties
        String userAbn = "26008672179";
        boolean liveMode = true;

        for (String arg : args) {
            if ("--abn.mode=live".equals(arg)) {
                liveMode = true;
            } else if (arg.matches("\\d{11}")) {
                userAbn = arg;
            }
        }

        // Choose service based on mode
        IABNLookupService abnService;
        if (liveMode) {
            // Instantiate ABNLookupService directly with RestTemplate
            RestTemplate restTemplate = createRestTemplateForABN();
            ABNLookupService liveService = new ABNLookupService(restTemplate);

            // Inject configuration via reflection
            setFieldValue(liveService, "baseUrl", "https://abr.business.gov.au");
            setFieldValue(liveService, "guid", readGuidFromProperties());

            abnService = liveService;
        } else {
            // For demo/test, use mock service (no Spring needed)
            abnService = new MockABNLookupService();
        }

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

    private static RestTemplate createRestTemplateForABN() {
        RestTemplate restTemplate = new RestTemplate();

        // Configure message converters to accept text/javascript as plain text
        // (ABN API returns JavaScript function call: Response({...}), not JSON)
        restTemplate.getMessageConverters().forEach(converter -> {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter)
                    .setSupportedMediaTypes(Arrays.asList(
                        MediaType.TEXT_PLAIN,
                        MediaType.valueOf("text/javascript"),
                        MediaType.valueOf("application/javascript")
                    ));
            }
        });

        return restTemplate;
    }

    private static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private static String readGuidFromProperties() {
        // Read GUID from application.yml via System property or return configured default
        String guid = System.getProperty("abn.lookup.guid");
        if (guid == null || guid.isEmpty()) {
            guid = "99b624c4-7e1b-4c68-ad56-2ce50d6af75c";  // Default from application.yml
        }
        return guid;
    }
}
