package com.diligence.rules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.diligence.casework.CaseRepository;
import com.diligence.casework.DueDiligenceCase;
import com.diligence.extraction.ExtractionResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("RuleController Integration Tests")
class RuleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private RuleResultRepository ruleResultRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private ExtractionResultRepository extractionResultRepository;

    private UUID caseId;
    private UUID ruleId;

    @BeforeEach
    void setUp() {
        extractionResultRepository.deleteAll();
        ruleResultRepository.deleteAll();
        ruleRepository.deleteAll();
        caseRepository.deleteAll();

        // Create test rule
        Rule testRule = new Rule("Test Rule", "Test Description",
                               RuleType.ABN_VALIDATION, RuleSeverity.HIGH);
        Rule savedRule = ruleRepository.save(testRule);
        ruleId = savedRule.getId();

        // Create test case
        DueDiligenceCase testCase = new DueDiligenceCase("Test Corp", "analyst@example.com");
        testCase.setSupplierAbn("12345678901");
        DueDiligenceCase savedCase = caseRepository.save(testCase);
        caseId = savedCase.getId();
    }

    @Test
    @DisplayName("Should create a new rule")
    void testCreateRule() throws Exception {
        String requestJson = objectMapper.writeValueAsString(
            new CreateRuleRequest("Sanction Check", "Check sanctions",
                                 RuleType.SANCTION_CHECK, RuleSeverity.CRITICAL)
        );

        mockMvc.perform(post("/api/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sanction Check"))
                .andExpect(jsonPath("$.ruleType").value("SANCTION_CHECK"))
                .andExpect(jsonPath("$.severity").value("CRITICAL"));
    }

    @Test
    @DisplayName("Should list all active rules")
    void testGetAllActiveRules() throws Exception {
        mockMvc.perform(get("/api/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Rule"));
    }

    @Test
    @DisplayName("Should get rule by ID")
    void testGetRuleById() throws Exception {
        mockMvc.perform(get("/api/rules/" + ruleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Rule"))
                .andExpect(jsonPath("$.ruleType").value("ABN_VALIDATION"));
    }

    @Test
    @DisplayName("Should evaluate all rules for a case")
    void testEvaluateAllRules() throws Exception {
        mockMvc.perform(post("/api/rules/api/cases/" + caseId + "/evaluate-rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].passed").value(true));
    }

    @Test
    @DisplayName("Should get rule results for a case")
    void testGetRuleResults() throws Exception {
        // First evaluate rules
        mockMvc.perform(post("/api/rules/api/cases/" + caseId + "/evaluate-rules"))
                .andExpect(status().isOk());

        // Then retrieve results
        mockMvc.perform(get("/api/rules/api/cases/" + caseId + "/rule-results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rule.name").value("Test Rule"));
    }
}
