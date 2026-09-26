package com.diligence.casework;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaseService {

    private static final Logger logger = LoggerFactory.getLogger(CaseService.class);
    private final CaseRepository caseRepository;

    public DueDiligenceCase createCase(String supplierName, String requestedBy) {
        DueDiligenceCase dueDiligenceCase = new DueDiligenceCase(supplierName, requestedBy);
        return caseRepository.save(dueDiligenceCase);
    }

    public Optional<DueDiligenceCase> getCaseById(UUID id) {
        return caseRepository.findById(id);
    }

    public List<DueDiligenceCase> getAllCases() {
        return caseRepository.findAll();
    }

    /**
     * Transition case to new status with validation and authorization
     * @param id Case ID
     * @param newStatus Target status
     * @param authorizedUser User making the change (required for sensitive transitions)
     * @return Updated case
     * @throws IllegalStateException if transition is not allowed
     * @throws IllegalArgumentException if authorization fails
     */
    public DueDiligenceCase updateCaseStatus(UUID id, CaseStatus newStatus, String authorizedUser) {
        DueDiligenceCase dueDiligenceCase = caseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Case not found: " + id));

        // Check if transition requires authorization
        if (isSensitiveTransition(dueDiligenceCase.getStatus(), newStatus)) {
            if (authorizedUser == null || authorizedUser.isBlank()) {
                throw new IllegalArgumentException(
                    "Authorization required for transition from " + dueDiligenceCase.getStatus() +
                    " to " + newStatus);
            }
            logger.info("Authorized transition from {} to {} by {}",
                dueDiligenceCase.getStatus(), newStatus, authorizedUser);
        }

        // Enforce state machine transitions
        dueDiligenceCase.transitionStatus(newStatus);
        DueDiligenceCase saved = caseRepository.save(dueDiligenceCase);
        logger.info("Case {} transitioned to {} status", id, newStatus);
        return saved;
    }

    /**
     * Backward compatible method for status updates without authorization tracking
     */
    public DueDiligenceCase updateCaseStatus(UUID id, CaseStatus newStatus) {
        return updateCaseStatus(id, newStatus, null);
    }

    /**
     * Determine if a transition requires human authorization
     * Sensitive transitions: moving to HUMAN_DECISION_PENDING or COMPLETED
     */
    private boolean isSensitiveTransition(CaseStatus currentStatus, CaseStatus targetStatus) {
        return targetStatus == CaseStatus.HUMAN_DECISION_PENDING ||
               targetStatus == CaseStatus.COMPLETED;
    }

    public DueDiligenceCase updateSupplierInfo(UUID id, String abn, String legalName) {
        DueDiligenceCase dueDiligenceCase = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        dueDiligenceCase.setSupplierAbn(abn);
        dueDiligenceCase.setSupplierLegalName(legalName);
        return caseRepository.save(dueDiligenceCase);
    }

}
