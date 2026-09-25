package com.diligence.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.diligence.casework.CaseRepository;
import com.diligence.casework.DueDiligenceCase;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierVerificationService {

    private final ABNLookupService abnLookupService;
    private final ToolResultRepository toolResultRepository;
    private final CaseRepository caseRepository;
    private final ObjectMapper objectMapper;

    /**
     * Verify supplier by calling ABN Lookup tool
     * @param caseId Case to verify
     * @return ToolResult with match status and evidence
     */
    public ToolResult verifySupplier(UUID caseId) {
        DueDiligenceCase caseEntity = caseRepository.findById(caseId)
            .orElseThrow(() -> new RuntimeException("Case not found"));

        if (caseEntity.getSupplierAbn() == null) {
            throw new RuntimeException("Supplier ABN not set");
        }

        try {
            // 1. Call ABN Lookup API
            ABNLookupService.ABNLookupResult abnResult = abnLookupService.lookupABN(
                caseEntity.getSupplierAbn()
            );

            // 2. Compare names (fuzzy matching)
            NameMatchResult nameMatch = matchSupplierNames(
                caseEntity.getSupplierName(),
                abnResult.getBusinessName()
            );

            // 3. Build evidence
            String evidence = buildEvidence(caseEntity, abnResult, nameMatch);

            // 4. Create tool result
            ToolResult result = new ToolResult(
                caseEntity,
                "ABN_LOOKUP",
                "SUPPLIER_VERIFICATION",
                serializeToJson(new ToolInput(caseEntity.getSupplierAbn())),
                serializeToJson(abnResult),
                true,  // success
                null,  // no error
                evidence
            );

            // 5. Save to database
            return toolResultRepository.save(result);

        } catch (Exception e) {
            log.error("Supplier verification failed for case {}: {}", caseId, e.getMessage());

            // Save failed result
            ToolResult result = new ToolResult(
                caseEntity,
                "ABN_LOOKUP",
                "SUPPLIER_VERIFICATION",
                serializeToJson(new ToolInput(caseEntity.getSupplierAbn())),
                null,
                false,  // failed
                e.getMessage(),
                "ABN lookup failed: " + e.getMessage()
            );

            return toolResultRepository.save(result);
        }
    }

    /**
     * Compare supplier name from case with ABN registry name
     * Handles fuzzy matching (e.g., "Acme Corp" vs "Acme Corporation Pty Ltd")
     */
    private NameMatchResult matchSupplierNames(String caseName, String abnName) {
        if (caseName == null || abnName == null) {
            return new NameMatchResult(false, 0.0);
        }

        // Normalize both names
        String normalizedCaseName = normalizeName(caseName);
        String normalizedAbnName = normalizeName(abnName);

        // Exact match
        if (normalizedCaseName.equals(normalizedAbnName)) {
            return new NameMatchResult(true, 1.0);
        }

        // Check if one contains the other (common case: "Acme Corp" in "Acme Corporation Pty Ltd")
        if (normalizedAbnName.contains(normalizedCaseName) || normalizedCaseName.contains(normalizedAbnName)) {
            return new NameMatchResult(true, 0.95);
        }

        // Levenshtein distance for fuzzy matching
        double similarity = calculateSimilarity(normalizedCaseName, normalizedAbnName);
        boolean matches = similarity > 0.80;  // 80% match threshold

        return new NameMatchResult(matches, similarity);
    }

    /**
     * Normalize name for comparison (lowercase, remove punctuation)
     */
    private String normalizeName(String name) {
        return name.toLowerCase()
            .replaceAll("[^a-z0-9\\s]", "")  // Remove punctuation
            .replaceAll("\\s+", " ")          // Normalize spaces
            .trim();
    }

    /**
     * Calculate string similarity using Levenshtein distance
     */
    private double calculateSimilarity(String s1, String s2) {
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) return 1.0;

        int distance = levenshteinDistance(s1, s2);
        return 1.0 - ((double) distance / maxLength);
    }

    /**
     * Levenshtein distance algorithm
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Build human-readable evidence string
     */
    private String buildEvidence(DueDiligenceCase caseEntity,
                                 ABNLookupService.ABNLookupResult abnResult,
                                 NameMatchResult nameMatch) {
        StringBuilder evidence = new StringBuilder();

        evidence.append("ABN Lookup Tool Execution:\n");
        evidence.append("- ABN: ").append(caseEntity.getSupplierAbn()).append("\n");
        evidence.append("- Case Name: ").append(caseEntity.getSupplierName()).append("\n");
        evidence.append("- Registry Name: ").append(abnResult.getBusinessName()).append("\n");
        evidence.append("- Business Status: ").append(abnResult.getStatus()).append("\n");
        evidence.append("- Name Match: ").append(nameMatch.isMatches() ? "YES" : "NO").append("\n");
        evidence.append("- Similarity Score: ").append(String.format("%.2f", nameMatch.getSimilarity() * 100)).append("%\n");
        evidence.append("- Verdict: ");

        if (!abnResult.isFound()) {
            evidence.append("ABN not found in registry - HIGH RISK");
        } else if (!abnResult.getStatus().equals("Active")) {
            evidence.append("Business not active - REVIEW REQUIRED");
        } else if (nameMatch.isMatches()) {
            evidence.append("Name matches registry - VERIFIED");
        } else {
            evidence.append("Name does not match registry - INVESTIGATE");
        }

        return evidence.toString();
    }

    /**
     * Serialize object to JSON string
     */
    private String serializeToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * Tool input record
     */
    @Data
    @AllArgsConstructor
    public static class ToolInput {
        private String abn;
    }

    /**
     * Name match result
     */
    @Data
    @AllArgsConstructor
    private static class NameMatchResult {
        private boolean matches;
        private double similarity;
    }
}
