package com.diligence.tools;

import jakarta.persistence.*;
import lombok.*;
import com.diligence.casework.DueDiligenceCase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tool_results")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"executedAt"})
@ToString(exclude = {"executedAt"})
public class ToolResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    @JsonIgnore
    private DueDiligenceCase caseEntity;

    @Column(nullable = false)
    private String toolName;  // "ABN_LOOKUP"

    @Column(nullable = false)
    private String toolType;  // "SUPPLIER_VERIFICATION"

    @Column(columnDefinition = "TEXT")
    private String input;  // JSON input to tool

    @Column(columnDefinition = "TEXT")
    private String output;  // JSON output from tool

    @Column(nullable = false)
    private Boolean success;  // true = tool succeeded, false = tool failed

    @Column(columnDefinition = "TEXT")
    private String errorMessage;  // If success = false

    @Column(columnDefinition = "TEXT")
    private String evidence;  // Human-readable findings

    @Column(nullable = false, updatable = false)
    private LocalDateTime executedAt = LocalDateTime.now();

    public ToolResult(DueDiligenceCase caseEntity, String toolName, String toolType,
                     String input, String output, Boolean success,
                     String errorMessage, String evidence) {
        this.caseEntity = caseEntity;
        this.toolName = toolName;
        this.toolType = toolType;
        this.input = input;
        this.output = output;
        this.success = success;
        this.errorMessage = errorMessage;
        this.evidence = evidence;
        this.executedAt = LocalDateTime.now();
    }
}
