package com.diligence.extraction;

import jakarta.persistence.*;
import lombok.*;
import com.diligence.casework.DueDiligenceCase;
import com.diligence.documents.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "extraction_results")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"executedAt"})
@ToString(exclude = {"executedAt"})
public class ExtractionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    @JsonIgnore
    private DueDiligenceCase caseEntity;

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    @JsonIgnore
    private Document documentEntity;

    @Column(nullable = false)
    private String toolName;  // "DOCUMENT_EXTRACTION"

    @Column(nullable = false)
    private String toolType;  // "EVIDENCE_EXTRACTION"

    @Column(columnDefinition = "TEXT")
    private String input;  // JSON: {documentId, documentName, chunkCount}

    @Column(columnDefinition = "TEXT")
    private String output;  // JSON: {facts[], summary, complete}

    @Column(nullable = false)
    private Boolean success;  // true = extraction succeeded, false = failed

    @Column(columnDefinition = "TEXT")
    private String errorMessage;  // If success = false, why?

    @Column(columnDefinition = "TEXT")
    private String evidence;  // Human-readable findings with confidence

    @Column(length = 20)
    private String promptVersion;  // "v1", "v2", "v3" for A/B testing

    @Column(length = 100)
    private String modelUsed;  // "gpt-4-turbo", "claude-3-opus", etc.

    @Column
    private Integer tokensUsed;  // For cost tracking

    @Column(nullable = false, updatable = false)
    private LocalDateTime executedAt = LocalDateTime.now();

    public ExtractionResult(DueDiligenceCase caseEntity, Document documentEntity,
                           String toolName, String toolType,
                           String input, String output, Boolean success,
                           String errorMessage, String evidence,
                           String promptVersion, String modelUsed, Integer tokensUsed) {
        this.caseEntity = caseEntity;
        this.documentEntity = documentEntity;
        this.toolName = toolName;
        this.toolType = toolType;
        this.input = input;
        this.output = output;
        this.success = success;
        this.errorMessage = errorMessage;
        this.evidence = evidence;
        this.promptVersion = promptVersion;
        this.modelUsed = modelUsed;
        this.tokensUsed = tokensUsed;
        this.executedAt = LocalDateTime.now();
    }
}
