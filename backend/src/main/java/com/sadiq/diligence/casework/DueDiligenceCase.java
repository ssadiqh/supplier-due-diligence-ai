package com.sadiq.diligence.casework;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cases")
public class DueDiligenceCase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String caseId;

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

    public DueDiligenceCase() {
    }

    public DueDiligenceCase(String caseId, String supplierName, String requestedBy) {
        this.caseId = caseId;
        this.supplierName = supplierName;
        this.requestedBy = requestedBy;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getSupplierAbn() {
        return supplierAbn;
    }

    public void setSupplierAbn(String supplierAbn) {
        this.supplierAbn = supplierAbn;
    }

    public String getSupplierLegalName() {
        return supplierLegalName;
    }

    public void setSupplierLegalName(String supplierLegalName) {
        this.supplierLegalName = supplierLegalName;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
