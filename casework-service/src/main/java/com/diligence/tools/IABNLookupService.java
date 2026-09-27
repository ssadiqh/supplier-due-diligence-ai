package com.diligence.tools;

/**
 * Interface for ABN lookup service
 * Allows switching between mock and live implementations via configuration
 */
public interface IABNLookupService {

    /**
     * Lookup ABN details
     * @param abn Australian Business Number (11 digits, valid checksum)
     * @return ABN lookup result with business details
     * @throws RuntimeException if ABN is invalid or lookup fails
     */
    ABNLookupService.ABNLookupResult lookupABN(String abn);

    /**
     * Get the current mode (mock or live)
     */
    String getMode();
}
