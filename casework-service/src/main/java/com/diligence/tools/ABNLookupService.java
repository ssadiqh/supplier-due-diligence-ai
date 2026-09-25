package com.diligence.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class ABNLookupService {

    private final RestTemplate restTemplate;

    @Value("${abn.lookup.base-url:https://abr.business.gov.au}")
    private String baseUrl;

    @Value("${abn.lookup.guid:}")
    private String guid;

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
        ABNLookupResult result = callABNAPI(abn);
        log.info("ABN lookup successful: {}", abn);
        return result;
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
    private ABNLookupResult callABNAPI(String abn) {
        if (guid == null || guid.isEmpty()) {
            log.warn("ABN_LOOKUP_GUID not configured, using mock data");
            return new ABNLookupResult(abn, "Test Company Pty Ltd", "Active", true);
        }

        try {
            // Build ABN Lookup JSON endpoint URL with GUID authentication
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/json/AbnDetails.aspx")
                .queryParam("abn", abn)
                .queryParam("guid", guid)
                .toUriString();

            log.debug("Calling ABN Lookup API: /json/AbnDetails.aspx?abn={}&guid=***", abn);

            // Call official ABN Lookup API
            AbnDetailsResponse response = restTemplate.getForObject(url, AbnDetailsResponse.class);

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
     */
    public static class AbnDetailsResponse {
        @JsonProperty("ABN")
        private String abn;

        @JsonProperty("EntityName")
        private String businessName;

        @JsonProperty("EntityStatus")
        private String businessStatus;

        public String getBusinessName() {
            return businessName != null ? businessName : "Unknown";
        }

        public String getBusinessStatus() {
            return businessStatus != null ? businessStatus : "Unknown";
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
