package com.diligence.rules;

public enum RuleType {
    SANCTION_CHECK,           // Check against sanctions list
    ABN_VALIDATION,           // Validate ABN exists and matches name
    BUSINESS_REGISTRATION,    // Verify business is registered
    FINANCIAL_THRESHOLD,      // Check revenue/assets threshold
    INDUSTRY_RESTRICTION,     // Verify industry is not restricted
    COMPLIANCE_HISTORY,       // Review past compliance issues
    BENEFICIAL_OWNERSHIP,     // Verify beneficial ownership disclosure
    POLITICAL_EXPOSURE        // Check for politically exposed persons
}
