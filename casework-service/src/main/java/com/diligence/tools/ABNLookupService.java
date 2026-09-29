package com.diligence.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
@ConditionalOnProperty(name = "abn.mode", havingValue = "live", matchIfMissing = false)
@RequiredArgsConstructor
public class ABNLookupService implements IABNLookupService {

    private final RestTemplate restTemplate;

    @Value("${abn.lookup.base-url:https://abr.business.gov.au}")
    private String baseUrl;

    @Value("${abn.lookup.guid:}")
    private String guid;

    @Override
    public String getMode() {
        return "live";
    }

    /**
     * Lookup ABN details from Australian Business Register
     * @param abn Australian Business Number (11 digits)
     * @return ABN lookup result with business details
     * @throws RuntimeException if ABN is invalid or API call fails
     */
    public ABNLookupResult lookupABN(String abn) {
        if (abn == null || abn.isEmpty()) {
            throw new RuntimeException("ABN is required");
        }

        // Validate ABN format (11 digits)
        if (!abn.matches("\\d{11}")) {
            throw new RuntimeException("ABN must be 11 digits");
        }

        // Validate ABN checksum (mod 10)
        if (!validateABNChecksum(abn)) {
            throw new RuntimeException("ABN checksum validation failed - invalid ABN");
        }

        log.info("Looking up ABN: {}", abn);
        ABNLookupResult result = callABNAPI(abn);
        log.info("ABN lookup successful: {}", abn);
        return result;
    }

    /**
     * Validate ABN using mod 10 checksum algorithm
     * Australian Business Number checksum calculation per ISO/IEC 7064, mod 10-13
     * @param abn 11-digit ABN
     * @return true if checksum is valid
     */
    private boolean validateABNChecksum(String abn) {
        // ABN checksum weights (left to right)
        int[] weights = {10, 1, 3, 5, 7, 9, 11, 13, 15, 17, 19};

        try {
            int sum = 0;
            for (int i = 0; i < 11; i++) {
                int digit = Integer.parseInt(abn.charAt(i) + "");
                // Subtract 1 from first digit for checksum calculation
                int value = (i == 0) ? digit - 1 : digit;
                sum += value * weights[i];
            }

            // Valid if sum mod 89 equals 0
            return sum % 89 == 0;
        } catch (NumberFormatException e) {
            log.error("Invalid ABN format for checksum validation: {}", abn);
            return false;
        }
    }

    /**
     * Call ABN Lookup API via Australian Business Register
     * Official API: https://abr.business.gov.au/
     *
     * Endpoints:
     * - JSON: GET /json/AbnDetails.aspx?abn={abn}&guid={guid}
     * - XML: GET /abrxmlsearch/AbrXmlSearch.asmx/SearchByABNv202001?searchString={abn}&authenticationGuid={guid}
     *
     * Authentication: GUID (free, obtained via registration at abr.business.gov.au)
     *
     * API Docs: https://abr.business.gov.au/Documentation/WebServiceRegistration
     */
    /**
     * Parse JavaScript response from ABN API
     * API returns: Response({"ABN":"...", "EntityName":"...", ...})
     * Extract JSON object from within JavaScript function call
     */
    private AbnDetailsResponse parseJavaScriptResponse(String rawResponse) throws Exception {
        // Extract JSON from JavaScript: Response({...})
        int jsonStart = rawResponse.indexOf('{');
        int jsonEnd = rawResponse.lastIndexOf('}') + 1;

        if (jsonStart == -1 || jsonEnd <= jsonStart) {
            log.error("Could not find JSON object in response: {}", rawResponse.substring(0, Math.min(200, rawResponse.length())));
            throw new RuntimeException("ABN API response does not contain valid JSON object");
        }

        String jsonStr = rawResponse.substring(jsonStart, jsonEnd);
        log.debug("Extracted JSON: {}", jsonStr.substring(0, Math.min(200, jsonStr.length())));

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonStr, AbnDetailsResponse.class);
    }

    private ABNLookupResult callABNAPI(String abn) {
        if (guid == null || guid.isEmpty()) {
            // In LIVE mode, fail closed - do not fall back to mock data
            log.error("ABN_LOOKUP_GUID not configured in LIVE mode - ABN lookup cannot proceed");
            throw new RuntimeException("ABN lookup is not configured: ABN_LOOKUP_GUID environment variable is missing. " +
                    "Set abn.lookup.guid or switch to mock mode (abn.mode=mock) for development.");
        }

        try {
            // Build ABN Lookup JSON endpoint URL with GUID authentication
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/json/AbnDetails.aspx")
                .queryParam("abn", abn)
                .queryParam("guid", guid)
                .toUriString();

            log.debug("Calling ABN Lookup API: /json/AbnDetails.aspx?abn={}&guid=***", abn);

            // Call official ABN Lookup API - returns raw response to handle JavaScript wrapper
            String rawResponse = restTemplate.getForObject(url, String.class);
            log.debug("Raw API response (first 500 chars): {}", rawResponse.substring(0, Math.min(500, rawResponse.length())));

            // Parse JavaScript response (API returns JS function call, not JSON)
            AbnDetailsResponse response = parseJavaScriptResponse(rawResponse);

            if (response == null) {
                log.warn("ABN {} - no response from registry", abn);
                return new ABNLookupResult(abn, "Unknown", "Unknown", false);
            }

            // Check if ABN was found
            if (!response.isSuccess()) {
                log.warn("ABN {} not found in registry", abn);
                return new ABNLookupResult(abn, "Unknown", "Unknown", false);
            }

            // Extract business details from response
            String businessName = response.getBusinessName();
            String businessStatus = response.getBusinessStatus();

            return new ABNLookupResult(
                abn,
                businessName,
                businessStatus,
                true  // found
            );
        } catch (Exception e) {
            log.error("Error calling ABN Lookup API for {}: {}", abn, e.getMessage());
            throw new RuntimeException("ABN Lookup API call failed: " + e.getMessage());
        }
    }

    /**
     * ABN Lookup API JSON response
     * Maps to actual ABN Lookup /json/AbnDetails.aspx endpoint response
     * Note: API field names differ from documentation:
     * - Actual: "Abn", "AbnName", "AbnStatus"
     * - Documented: "ABN", "EntityName", "EntityStatus"
     */
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class AbnDetailsResponse {
        @JsonProperty(value = "ABN", defaultValue = "")
        @com.fasterxml.jackson.annotation.JsonAlias({"Abn"})
        private String abn;

        @JsonProperty(value = "EntityName", defaultValue = "Unknown")
        @com.fasterxml.jackson.annotation.JsonAlias({"AbnName", "BusinessName"})
        private Object businessName;

        @JsonProperty(value = "EntityStatus", defaultValue = "Unknown")
        @com.fasterxml.jackson.annotation.JsonAlias({"AbnStatus", "BusinessStatus", "Status"})
        private Object businessStatus;

        public String getBusinessName() {
            if (businessName == null) return "Unknown";
            if (businessName instanceof String) return (String) businessName;
            // Handle array case (API returns [name] for some fields)
            if (businessName instanceof java.util.List) {
                java.util.List<?> list = (java.util.List<?>) businessName;
                return list.isEmpty() ? "Unknown" : list.get(0).toString();
            }
            return businessName.toString();
        }

        public String getBusinessStatus() {
            if (businessStatus == null) return "Unknown";
            if (businessStatus instanceof String) return (String) businessStatus;
            // Handle array case
            if (businessStatus instanceof java.util.List) {
                java.util.List<?> list = (java.util.List<?>) businessStatus;
                return list.isEmpty() ? "Unknown" : list.get(0).toString();
            }
            return businessStatus.toString();
        }

        public boolean isSuccess() {
            return abn != null && !abn.isEmpty();
        }
    }

    /**
     * Response from ABN Lookup service
     */
    @Data
    public static class ABNLookupResult {
        private final String abn;
        private final String businessName;
        private final String status;
        private final boolean found;
    }
}
