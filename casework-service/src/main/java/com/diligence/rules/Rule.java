package com.diligence.rules;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rules")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"createdAt"})
@ToString(exclude = {"createdAt"})
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType ruleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleSeverity severity;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Rule(String name, String description, RuleType ruleType, RuleSeverity severity) {
        this.name = name;
        this.description = description;
        this.ruleType = ruleType;
        this.severity = severity;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
}
