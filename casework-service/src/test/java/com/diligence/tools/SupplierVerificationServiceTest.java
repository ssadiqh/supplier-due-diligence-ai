package com.diligence.tools;

import com.diligence.casework.CaseRepository;
import com.diligence.casework.DueDiligenceCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("SupplierVerificationService Tests")
class SupplierVerificationServiceTest {

    @Mock
    private ABNLookupService abnLookupService;

    @Mock
    private ToolResultRepository toolResultRepository;

    @Mock
    private CaseRepository caseRepository;

    private SupplierVerificationService verificationService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        verificationService = new SupplierVerificationService(abnLookupService, toolResultRepository, caseRepository, objectMapper);
    }

    @Test
    @DisplayName("Should verify supplier with matching name")
    void testVerifySupplier_NameMatches() {
        UUID caseId = UUID.randomUUID();
        DueDiligenceCase caseEntity = new DueDiligenceCase("Acme Corp", "analyst@example.com");
        caseEntity.setId(caseId);
        caseEntity.setSupplierAbn("12345678901");

        ABNLookupService.ABNLookupResult abnResult = new ABNLookupService.ABNLookupResult(
            "12345678901",
            "Acme Corporation Pty Ltd",
            "Active",
            true
        );

        when(caseRepository.findById(caseId)).thenReturn(Optional.of(caseEntity));
        when(abnLookupService.lookupABN("12345678901")).thenReturn(abnResult);
        when(toolResultRepository.save(any())).thenAnswer(inv -> {
            ToolResult result = inv.getArgument(0);
            result.setId(UUID.randomUUID());
            return result;
        });

        ToolResult result = verificationService.verifySupplier(caseId);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertTrue(result.getEvidence().contains("Name matches registry"));
    }

    @Test
    @DisplayName("Should detect name mismatch")
    void testVerifySupplier_NameMismatch() {
        UUID caseId = UUID.randomUUID();
        DueDiligenceCase caseEntity = new DueDiligenceCase("Acme Corp", "analyst@example.com");
        caseEntity.setId(caseId);
        caseEntity.setSupplierAbn("12345678901");

        ABNLookupService.ABNLookupResult abnResult = new ABNLookupService.ABNLookupResult(
            "12345678901",
            "XYZ Holdings Pty Ltd",
            "Active",
            true
        );

        when(caseRepository.findById(caseId)).thenReturn(Optional.of(caseEntity));
        when(abnLookupService.lookupABN("12345678901")).thenReturn(abnResult);
        when(toolResultRepository.save(any())).thenAnswer(inv -> {
            ToolResult result = inv.getArgument(0);
            result.setId(UUID.randomUUID());
            return result;
        });

        ToolResult result = verificationService.verifySupplier(caseId);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertTrue(result.getEvidence().contains("Name does not match"));
    }

    @Test
    @DisplayName("Should handle ABN lookup failure")
    void testVerifySupplier_LookupFails() {
        UUID caseId = UUID.randomUUID();
        DueDiligenceCase caseEntity = new DueDiligenceCase("Test Corp", "analyst@example.com");
        caseEntity.setId(caseId);
        caseEntity.setSupplierAbn("12345678901");

        when(caseRepository.findById(caseId)).thenReturn(Optional.of(caseEntity));
        when(abnLookupService.lookupABN("12345678901"))
            .thenThrow(new RuntimeException("API error"));
        when(toolResultRepository.save(any())).thenAnswer(inv -> {
            ToolResult result = inv.getArgument(0);
            result.setId(UUID.randomUUID());
            return result;
        });

        ToolResult result = verificationService.verifySupplier(caseId);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertTrue(result.getErrorMessage().contains("API error"));
    }
}
