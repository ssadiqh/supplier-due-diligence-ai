package com.diligence.rules;

public enum RuleSeverity {
    CRITICAL,   // Failure blocks case approval
    HIGH,       // Failure requires executive review
    MEDIUM,     // Failure flagged but can proceed with caution
    LOW        // Failure is informational only
}
