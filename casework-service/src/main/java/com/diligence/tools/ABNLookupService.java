package com.diligence.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ABNLookupService {

    private final RestTemplate restTemplate;

    @Value("${abn.lookup.url:https://api.abr.business.gov.au/v1/}")
    private String abnLookupUrl;

    @Value("${abn.lookup.api-key:}")
    private String apiKey;

    /**
     * Lookup ABN details from Australian Business Register
     * @param abn Australian Business Number (11 digits)
     * @return ABN lookup result with business details
     * @throws RuntimeException if API call fails
     */
    public ABNLookupResult lookupABN(String abn) {
        if (abn == null || abn.isEmpty()) {
            throw new RuntimeException("ABN is required");
        }

        // Validate ABN format (11 digits)
        if (!abn.matches("\\d{11}")) {
            throw new RuntimeException("ABN must be 11 digits");
        }

        log.info("Looking up ABN: {}", abn);

        // Phase 4: Mock/simulated API call
        // Phase 5+: Will integrate real ABR API: https://api.abr.business.gov.au/v1/
        ABNLookupResult result = callABNAPI(abn);

        log.info("ABN lookup successful: {}", abn);
        return result;
    }

    /**
     * Call ABN Lookup API via Australian Business Register
     * Real API: https://api.abr.business.gov.au/v1/
     * Requires: apiKey environment variable
     */
    private ABNLookupResult callABNAPI(String abn) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("ABN_API_KEY not configured, using mock data");
            return new ABNLookupResult(abn, "Test Company Pty Ltd", "Active", true);
        }

        try {
            String url = abnLookupUrl + "organisation/" + abn;

            // Call real ABR API via RestTemplate
            ABNResponse response = restTemplate.getForObject(url, ABNResponse.class);

            if (response == null) {
                return new ABNLookupResult(abn, "Unknown", "Unknown", false);
            }

            return new ABNLookupResult(
                abn,
                response.businessName,
                response.status,
                response.found
            );
        } catch (Exception e) {
            log.error("Error calling ABN API for {}: {}", abn, e.getMessage());
            throw new RuntimeException("ABN API call failed: " + e.getMessage());
        }
    }

    /**
     * ABN API response structure from ABR
     */
    public static class ABNResponse {
        public String businessName;
        public String status;
        public boolean found;
    }

    /**
     * Response from ABN Lookup API
     */
    public static class ABNLookupResult {
        public String abn;
        public String businessName;
        public String status;  // Active, Cancelled, etc.
        public boolean found;

        public ABNLookupResult(String abn, String businessName, String status, boolean found) {
            this.abn = abn;
            this.businessName = businessName;
            this.status = status;
            this.found = found;
        }
    }
}
