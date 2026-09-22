package com.supplier.diligence.casework;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaseService {

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

    public DueDiligenceCase updateCaseStatus(UUID id, CaseStatus newStatus) {
        DueDiligenceCase dueDiligenceCase = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        dueDiligenceCase.setStatus(newStatus);
        return caseRepository.save(dueDiligenceCase);
    }

    public DueDiligenceCase updateSupplierInfo(UUID id, String abn, String legalName) {
        DueDiligenceCase dueDiligenceCase = caseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        dueDiligenceCase.setSupplierAbn(abn);
        dueDiligenceCase.setSupplierLegalName(legalName);
        return caseRepository.save(dueDiligenceCase);
    }

}
