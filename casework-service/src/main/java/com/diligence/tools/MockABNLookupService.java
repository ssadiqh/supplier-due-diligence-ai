package com.diligence.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Mock ABN Lookup Service for development and testing
 * Returns synthetic data without calling real API
 *
 * Enable with: abn.mode=mock or leave unconfigured (default)
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "abn.mode", havingValue = "mock", matchIfMissing = true)
public class MockABNLookupService implements IABNLookupService {

    @Override
    public String getMode() {
        return "mock";
    }

    /**
     * Mock ABN lookup - returns synthetic data based on ABN
     * Does not validate checksum or call real API
     *
     * @param abn 11-digit ABN
     * @return Mock ABN lookup result
     */
    @Override
    public ABNLookupService.ABNLookupResult lookupABN(String abn) {
        if (abn == null || abn.isEmpty()) {
            throw new RuntimeException("ABN is required");
        }

        // Validate format (11 digits) but NOT checksum in mock mode
        if (!abn.matches("\\d{11}")) {
            throw new RuntimeException("ABN must be 11 digits");
        }

        log.info("Mock ABN lookup: {} (mock mode - no API call)", abn);

        // Return synthetic data based on ABN
        String businessName = generateMockBusinessName(abn);
        String status = generateMockStatus(abn);

        return new ABNLookupService.ABNLookupResult(abn, businessName, status, true);
    }

    /**
     * Generate mock business name based on ABN
     * Useful for deterministic testing
     */
    private String generateMockBusinessName(String abn) {
        // Create consistent mock name based on ABN value
        int hash = abn.hashCode() % 10;
        String[] names = {
            "Mock Company A Pty Ltd",
            "Test Business B Pty Ltd",
            "Demo Corp C Pty Ltd",
            "Sample Industries D Pty Ltd",
            "Mock Services E Pty Ltd",
            "Test Solutions F Pty Ltd",
            "Demo Trading G Pty Ltd",
            "Sample Holdings H Pty Ltd",
            "Mock Enterprises I Pty Ltd",
            "Test Operations J Pty Ltd"
        };
        return names[Math.abs(hash)];
    }

    /**
     * Generate mock status based on ABN
     * Most ABNs are "Active", some are "Cancelled"
     */
    private String generateMockStatus(String abn) {
        // ABNs ending in 9 are "Cancelled" in mock mode for testing
        if (abn.endsWith("9")) {
            return "Cancelled";
        }
        return "Active";
    }
}
