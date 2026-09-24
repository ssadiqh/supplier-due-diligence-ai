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

    @Column(nullable = false)
    private Boolean passed;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(columnDefinition = "TEXT")
    private String evidence;

    @Column(nullable = false, updatable = false)
    private LocalDateTime evaluatedAt = LocalDateTime.now();

    public RuleResult(DueDiligenceCase caseEntity, Rule rule, Boolean passed, String details, String evidence) {
        this.caseEntity = caseEntity;
        this.rule = rule;
        this.passed = passed;
        this.details = details;
        this.evidence = evidence;
        this.evaluatedAt = LocalDateTime.now();
    }
}
