package com.diligence.rules;

/**
 * Enum representing possible outcomes of a rule evaluation.
 * Replaces Boolean to provide more expressive status information.
 */
public enum RuleOutcome {
    PASS("Rule passed - supplier meets requirements"),
    FAIL("Rule failed - supplier does not meet requirements"),
    ERROR("Rule execution failed - technical error occurred"),
    NOT_EVALUATED("Rule not yet evaluated or not applicable"),
    NOT_APPLICABLE("Rule not applicable for this supplier"),
    UNAVAILABLE("Required data unavailable - cannot evaluate");

    private final String description;

    RuleOutcome(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if outcome indicates a passing result
     */
    public boolean isPassing() {
        return this == PASS;
    }

    /**
     * Check if outcome indicates a failure
     */
    public boolean isFailing() {
        return this == FAIL;
    }

    /**
     * Check if outcome is a terminal error
     */
    public boolean isError() {
        return this == ERROR;
    }

    /**
     * Check if evaluation is still pending
     */
    public boolean isPending() {
        return this == NOT_EVALUATED || this == UNAVAILABLE;
    }
}
