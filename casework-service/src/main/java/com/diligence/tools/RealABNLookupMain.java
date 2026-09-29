package com.diligence.tools;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.MediaType;
import java.lang.reflect.Field;
import java.util.Arrays;

public class RealABNLookupMain {

    public static void main(String[] args) throws Exception {
        // Separate user ABN input from properties
        String abn = "26008672179";

        // Instantiate ABNLookupService directly with RestTemplate
        RestTemplate restTemplate = createRestTemplateForABN();
        IABNLookupService service = new ABNLookupService(restTemplate);

        // Inject configuration via reflection
        setFieldValue(service, "baseUrl", "https://abr.business.gov.au");
        setFieldValue(service, "guid", readGuidFromProperties());

        System.out.println("=== ABN Lookup Service - Real Mode ===\n");
        System.out.println("Mode: " + service.getMode() + "\n");

        System.out.println("Testing ABN: " + abn + "\n");
        testABN(abn, service);
    }


    private static void testABN(String abn, IABNLookupService abnService) {
    	
        System.out.println("--- ABN: " + abn + " ---");
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
