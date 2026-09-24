package com.diligence.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
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

    @Value("${abn.lookup.timeout:5000}")
    private int timeout;

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

        try {
            log.info("Looking up ABN: {}", abn);

            // Call ABN Lookup API
            // In Phase 4, we use a mock/test API
            // Phase 4+ will integrate real ABR API: https://api.abr.business.gov.au/v1/
            String url = abnLookupUrl + "organisation/" + abn;

            // For Phase 4 learning: simulate API response
            // Real implementation would call the actual API
            ABNLookupResult result = callABNAPI(abn);

            log.info("ABN lookup successful: {}", abn);
            return result;

        } catch (RestClientException e) {
            log.error("ABN lookup failed for {}: {}", abn, e.getMessage());
            throw new RuntimeException("Failed to lookup ABN: " + e.getMessage());
        }
    }

    /**
     * Mock/simulated ABN API call
     * Phase 4 learning: demonstrates tool calling pattern
     * Real implementation would use RestTemplate to call actual API
     */
    private ABNLookupResult callABNAPI(String abn) {
        // In production, this would be:
        // RestTemplate.getForObject(url, ABNLookupResult.class)

        // For Phase 4 learning, return mock data
        // Phase 5+ will integrate real ABR API
        return new ABNLookupResult(
            abn,
            "Test Company Pty Ltd",
            "Active",
            true
        );
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
