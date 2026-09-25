package com.diligence.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
     * Requires: apiKey environment variable (obtained from abr.business.gov.au)
     *
     * API Docs: https://www.asic.gov.au/online-services/access-to-asic-data/asic-download-data/access-the-abr-data-guide/
     */
    private ABNLookupResult callABNAPI(String abn) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("ABN_API_KEY not configured, using mock data");
            return new ABNLookupResult(abn, "Test Company Pty Ltd", "Active", true);
        }

        try {
            // Real ABR API endpoint: GET /v1/organisation/{abn}
            String url = abnLookupUrl + "organisation/" + abn;
            log.debug("Calling ABN API: {}", url);

            // Prepare request with authentication header
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Accept", "application/json");

            HttpEntity<String> request = new HttpEntity<>(headers);

            // Call real ABR API via RestTemplate
            ResponseEntity<AbrOrganisationResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                AbrOrganisationResponse.class
            );

            if (response.getBody() == null) {
                log.warn("ABN {} not found in registry", abn);
                return new ABNLookupResult(abn, "Unknown", "Unknown", false);
            }

            AbrOrganisationResponse body = response.getBody();
            return new ABNLookupResult(
                abn,
                body.businessName,
                body.businessStatus,
                true  // found
            );
        } catch (Exception e) {
            log.error("Error calling ABN API for {}: {}", abn, e.getMessage());
            throw new RuntimeException("ABN API call failed: " + e.getMessage());
        }
    }

    /**
     * ABR API response structure for organisation endpoint
     * Maps to actual ABR JSON response
     */
    public static class AbrOrganisationResponse {
        public String businessName;
        public String businessStatus;  // "Active", "Cancelled", "Suspended", etc.
        public String abn;
        public String acn;
        public String stateOfRegistration;
        public String lastUpdatedDate;
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
