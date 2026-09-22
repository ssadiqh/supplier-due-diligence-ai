package com.sadiq.diligence.casework;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("CaseService Tests")
class CaseServiceTest {

    @Mock
    private CaseRepository caseRepository;

    private CaseService caseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        caseService = new CaseService(caseRepository);
    }

    @Test
    @DisplayName("Should create a new case with SUBMITTED status")
    void testCreateCase() {
        DueDiligenceCase mockCase = new DueDiligenceCase("SUP-001", "Test Supplier", "analyst@example.com");
        mockCase.setId(UUID.randomUUID());
        
        when(caseRepository.save(any())).thenReturn(mockCase);

        DueDiligenceCase created = caseService.createCase("Test Supplier", "analyst@example.com");

        assertNotNull(created);
        assertEquals("Test Supplier", created.getSupplierName());
        assertEquals(CaseStatus.SUBMITTED, created.getStatus());
        assertEquals("analyst@example.com", created.getRequestedBy());
    }

    @Test
    @DisplayName("Should retrieve case by ID")
    void testGetCaseById() {
        UUID caseId = UUID.randomUUID();
        DueDiligenceCase mockCase = new DueDiligenceCase("SUP-001", "Test Supplier", "analyst@example.com");
        mockCase.setId(caseId);
        
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(mockCase));

        Optional<DueDiligenceCase> retrieved = caseService.getCaseById(caseId);

        assertTrue(retrieved.isPresent());
        assertEquals("Test Supplier", retrieved.get().getSupplierName());
    }

    @Test
    @DisplayName("Should update case status")
    void testUpdateCaseStatus() {
        UUID caseId = UUID.randomUUID();
        DueDiligenceCase mockCase = new DueDiligenceCase("SUP-001", "Test Supplier", "analyst@example.com");
        mockCase.setId(caseId);
        
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(mockCase));
        when(caseRepository.save(any())).thenReturn(mockCase);

        DueDiligenceCase updated = caseService.updateCaseStatus(caseId, CaseStatus.DOCUMENT_UPLOADED);

        assertNotNull(updated);
        assertEquals(CaseStatus.DOCUMENT_UPLOADED, updated.getStatus());
        assertNotNull(updated.getUpdatedAt());
    }

}
