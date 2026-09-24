package com.diligence.casework;

import jakarta.persistence.*;
import lombok.*;
import com.diligence.documents.Document;
import com.diligence.rules.RuleResult;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cases")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"createdAt", "updatedAt", "documents", "ruleResults"})
@ToString(exclude = {"createdAt", "updatedAt", "documents", "ruleResults"})
public class DueDiligenceCase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String supplierName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status = CaseStatus.SUBMITTED;

    @Column
    private String supplierAbn;

    @Column
    private String supplierLegalName;

    @Column
    private String requestedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RuleResult> ruleResults = new ArrayList<>();

    public DueDiligenceCase(String supplierName, String requestedBy) {
        this.supplierName = supplierName;
        this.requestedBy = requestedBy;
        this.createdAt = LocalDateTime.now();
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

}
