package com.diligence.rules;

import jakarta.persistence.*;
import lombok.*;
import com.diligence.casework.DueDiligenceCase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rule_results")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"evaluatedAt"})
@ToString(exclude = {"evaluatedAt"})
public class RuleResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    @JsonIgnore
    private DueDiligenceCase caseEntity;

    @ManyToOne
    @JoinColumn(name = "rule_id", nullable = false)
    private Rule rule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleOutcome outcome = RuleOutcome.NOT_EVALUATED;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String evidence;

    @Column(nullable = false, updatable = false)
    private LocalDateTime evaluatedAt = LocalDateTime.now();

    public RuleResult(DueDiligenceCase caseEntity, Rule rule, RuleOutcome outcome, String reason, String evidence) {
        this.caseEntity = caseEntity;
        this.rule = rule;
        this.outcome = outcome;
        this.reason = reason;
        this.evidence = evidence;
        this.evaluatedAt = LocalDateTime.now();
    }

    /**
     * Check if rule evaluation passed
     */
    public boolean isPassed() {
        return outcome == RuleOutcome.PASS;
    }

    /**
     * Check if rule evaluation failed
     */
    public boolean isFailed() {
        return outcome == RuleOutcome.FAIL;
    }
}
