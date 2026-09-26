# Sanctions Screening Procedure

**Version:** 1.0  
**Effective Date:** 2026-01-01  
**Owner:** Compliance  

---

## 1. Purpose

This procedure defines how sanctions screening is performed using the DFAT Consolidated List, including matching confidence levels and escalation actions.

---

## 2. Data Source

**DFAT Consolidated List**
- URL: https://www.dfat.gov.au/international-relations/security/sanctions/consolidated-list
- Format: XLSX
- Refresh: Monthly (scheduled job)
- Storage: PostgreSQL `sanctions_list` table
- Usage: Deterministic name matching (NOT in RAG)

---

## 3. Screening Triggers

Sanctions screening is performed:

1. **Every new supplier case** — Applicant name, company name, director names
2. **Monthly re-screening** — Active cases with open supplier records
3. **On request** — Compliance team requests re-screening of specific case

---

## 4. Matching Algorithm

Use fuzzy string matching (Levenshtein distance) to compare:

- Applicant/supplier name
- Director names (extract from ABN Lookup + documents)
- Trading names

Against DFAT list entries:

- Entity names
- Individual names
- Aliases

---

## 5. Confidence Levels and Actions

| Confidence | Match Type | Action | Authority |
|------------|-----------|--------|-----------|
| **≥95%** | Strong match | BLOCK immediately, escalate to Legal | Legal counsel |
| **85-94%** | Moderate match | Escalate to Compliance (request info) | Compliance Manager |
| **75-84%** | Weak match | Request supplier confirmation | Procurement Manager |
| **<75%** | No match | No action, log result | System |

---

## 6. Investigation Process

### For Moderate Matches (85-94%)

Compliance Officer:
1. Identifies specific field that matched (name, alias, country, ID)
2. Requests supplier information:
   - "Are you the [matched entity] listed on [date/source]?"
   - "Do you operate in [matched country]?"
   - "Do you have any relationship with [matched entity]?"
3. Evaluates response:
   - **Clear NO** → Case proceeds
   - **YES or UNCLEAR** → Escalate to Legal

### For Weak Matches (75-84%)

Procurement:
1. Notes match in case file
2. Asks supplier for clarification if name is common
3. Examples where clarification helps:
   - "John Smith" matching multiple people
   - Generic company name matching unrelated entities
   - Transliteration differences

---

## 7. False Positives

This procedure must account for false positives:

- **Name similarities:** "Anderson Holdings" ≠ "Anderson Trading"
- **Transliteration:** "Aleksander" ≠ "Alexander" (both acceptable)
- **Abbreviations:** "ABC Corp" ≠ "ABC Holdings"
- **Common names:** Multiple "John Smith" entries on DFAT list

**Action:** Weak matches require context validation, not automatic escalation.

---

## 8. Escalation to Legal

Legal team reviews when:

1. **Strong match identified (≥95%)**
   - Determines if match is false positive or true positive
   - Requests supplier documentation if needed
   - Makes final legal determination
   - Documents decision in case file

2. **Compliance cannot confirm identity**
   - Example: DFAT entity "Acme Trading Pvt Ltd" in Pakistan, supplier "Acme Trading Pty Ltd" in Australia
   - Legal researches company registration, ownership
   - Confirms whether entities are related

3. **Sanction has been removed**
   - Supplier claims sanction was lifted
   - Legal verifies against DFAT updates
   - Documents review date and outcome

---

## 9. Re-screening Procedure

### Monthly Batch Re-screening

For all active suppliers (not yet fully onboarded):
1. Run sanctions check against updated DFAT list
2. Log results for new/changed matches
3. Escalate any new matches immediately
4. Update case file with re-screening date

### Ad-Hoc Re-screening

Triggered by:
- Supplier change of address or ownership
- Regulatory notification
- Risk committee escalation
- Annual review

---

## 10. Documentation

Every sanctions screening result is recorded:

```
{
  "caseId": "SUP-000123",
  "screeningDate": "2026-01-15",
  "screeningType": "Initial",
  "searchTerms": ["Acme Corp", "John Smith", "Mary Johnson"],
  "matches": [
    {
      "confidence": 78,
      "dfatEntry": "Anderson John Smith (individual)",
      "country": "Iran",
      "reasoning": "Last name and first name partial match"
    }
  ],
  "action": "WEAK_MATCH_REVIEW",
  "reviewedBy": "compliance@company.com",
  "conclusion": "FALSE_POSITIVE - Common name, different individual",
  "documentedAt": "2026-01-15"
}
```

---

## 11. Sanctions List Updates

When DFAT publishes a new Consolidated List:

1. **Scheduled job** downloads updated XLSX
2. **Diff process** identifies new entries, removed entries, changes
3. **New entries** trigger re-screening of all active cases
4. **Removed entries** are noted (unlikely but possible)
5. **Changes** (e.g., name correction) are logged

---

## 12. Limitations

This procedure:
- ✅ Screens applicant and director names against DFAT list
- ✅ Provides confidence levels for human review
- ✅ Documents all findings

This procedure does NOT:
- ❌ Verify beneficial ownership (beyond directors)
- ❌ Screen against commercial PEP lists
- ❌ Verify third-party sanctions (banks, insurers)
- ❌ Screen associated entities automatically

---

## 13. Policy Review

Updated when:
- DFAT sanctions procedure changes
- Matching algorithm improves
- False positive rates indicate threshold adjustment needed

**Next review:** 2027-01-01
