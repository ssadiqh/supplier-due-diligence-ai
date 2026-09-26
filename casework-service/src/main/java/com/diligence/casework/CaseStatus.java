package com.diligence.casework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum CaseStatus {
    SUBMITTED("Initial submission"),
    DOCUMENT_UPLOADED("Documents received"),
    EVIDENCE_EXTRACTED("Evidence extracted from documents"),
    ENTITY_VERIFIED("Supplier entity verified via ABN"),
    SANCTIONS_SCREENED("Sanctions and PEP checks completed"),
    RULES_EVALUATED("Deterministic rules evaluated"),
    POLICY_ASSESSED("Organizational policies assessed"),
    REVIEW_READY("Ready for human review"),
    HUMAN_DECISION_PENDING("Awaiting human approval/rejection"),
    COMPLETED("Final decision made by human reviewer");

    private final String description;

    CaseStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * State machine transitions - must be defined after all enum constants
     */
    private static final Map<CaseStatus, Set<CaseStatus>> ALLOWED_TRANSITIONS = new HashMap<CaseStatus, Set<CaseStatus>>() {{
        put(SUBMITTED, Set.of(DOCUMENT_UPLOADED));
        put(DOCUMENT_UPLOADED, Set.of(EVIDENCE_EXTRACTED));
        put(EVIDENCE_EXTRACTED, Set.of(ENTITY_VERIFIED));
        put(ENTITY_VERIFIED, Set.of(SANCTIONS_SCREENED));
        put(SANCTIONS_SCREENED, Set.of(RULES_EVALUATED));
        put(RULES_EVALUATED, Set.of(POLICY_ASSESSED));
        put(POLICY_ASSESSED, Set.of(REVIEW_READY));
        put(REVIEW_READY, Set.of(HUMAN_DECISION_PENDING));
        put(HUMAN_DECISION_PENDING, Set.of(COMPLETED));
        put(COMPLETED, new HashSet<>()); // Terminal state
    }};

    /**
     * Check if transition to targetStatus is allowed from this status
     */
    public boolean canTransitionTo(CaseStatus targetStatus) {
        Set<CaseStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(this, new HashSet<>());
        return allowed.contains(targetStatus);
    }

    /**
     * Get human-readable list of allowed transitions
     */
    public String getAllowedTransitionsDescription() {
        Set<CaseStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(this, new HashSet<>());
        if (allowed.isEmpty()) {
            return "Terminal state - no transitions allowed";
        }
        StringBuilder sb = new StringBuilder("→ ");
        allowed.forEach(s -> {
            if (sb.length() > 2) sb.append(", ");
            sb.append(s.name());
        });
        return sb.toString();
    }
}
