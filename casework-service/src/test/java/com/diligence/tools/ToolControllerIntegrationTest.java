package com.diligence.tools;

import com.diligence.casework.CaseRepository;
import com.diligence.casework.DueDiligenceCase;
import com.diligence.extraction.ExtractionResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ToolController Integration Tests")
class ToolControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private ToolResultRepository toolResultRepository;

    @Autowired
    private ExtractionResultRepository extractionResultRepository;

    private UUID caseId;

    @BeforeEach
    void setUp() {
        extractionResultRepository.deleteAll();
        toolResultRepository.deleteAll();
        caseRepository.deleteAll();

        DueDiligenceCase testCase = new DueDiligenceCase("Test Corp", "analyst@example.com");
        testCase.setSupplierAbn("12345678901");
        DueDiligenceCase savedCase = caseRepository.save(testCase);
        caseId = savedCase.getId();
    }

    @Test
    @DisplayName("Should verify supplier via ABN lookup")
    void testVerifySupplier() throws Exception {
        mockMvc.perform(post("/api/cases/" + caseId + "/verify-supplier"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolName").value("ABN_LOOKUP"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should retrieve all tool results for case")
    void testGetToolResults() throws Exception {
        // First verify supplier (creates tool result)
        mockMvc.perform(post("/api/cases/" + caseId + "/verify-supplier"))
                .andExpect(status().isOk());

        // Then retrieve results
        mockMvc.perform(get("/api/cases/" + caseId + "/tool-results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].toolName").value("ABN_LOOKUP"));
    }

    @Test
    @DisplayName("Should filter successful tool results")
    void testGetSuccessfulResults() throws Exception {
        mockMvc.perform(post("/api/cases/" + caseId + "/verify-supplier"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cases/" + caseId + "/tool-results/successful"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].success").value(true));
    }
}
