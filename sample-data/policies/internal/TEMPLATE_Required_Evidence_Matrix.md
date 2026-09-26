# Required Evidence Matrix

**Version:** 1.0  
**Effective Date:** 2026-01-01  
**Owner:** Procurement  

---

## Purpose

This matrix defines what documents are required for each supplier category, how Phase 5 validates them, and what counts as acceptable variations.

---

## Evidence Requirements by Supplier Category

### Standard Supplier (Annual spend < $1M)

| Evidence | Mandatory | Max Age | Phase 5 Validation | Acceptable Variations |
|----------|-----------|---------|-------------------|----------------------|
| ABN Declaration | YES | N/A | Extract ABN, verify against live API | Trading name may differ |
| Supplier Application | YES | N/A | Extract company name, employees, location | Signature optional (if uploaded separately) |
| Certificate of Currency | YES | 1 mo before expiry | Extract expiry date, insurer, cover amount | Email from broker acceptable |
| Code of Conduct Declaration | YES | N/A | Extract signature date, confirm acknowledgment | Unsigned = must follow up |
| Bank Account Verification | YES | 3 months | Extract account holder name, compare to ABN | Minor name variations OK |
| Tax Residency Certificate | YES | 2 years | Extract tax residency declaration | Must state "Australia" or specific jurisdiction |

**Modern Slavery Statement:** Only if >20 employees

---

### High-Value Supplier (Annual spend $1M–$10M)

All above, PLUS:

| Evidence | Mandatory | Max Age | Phase 5 Validation | Notes |
|----------|-----------|---------|-------------------|-------|
| Modern Slavery Statement | YES | 12 months | Extract statement date, entity name, scope | Parent company statement acceptable |
| Information Security Questionnaire | YES | 2 years | Extract answers on data protection, backups | Missing answers = escalate |
| Directors & Beneficial Ownership | YES | 6 months | Extract director names, compare to DFAT list | Flag name similarities for review |
| Public Liability Insurance (upgrade) | YES | 1 mo before expiry | Verify minimum $20M cover | Professional indemnity required if applicable |

---

### Strategic Supplier (Annual spend > $10M)

All high-value requirements, PLUS:

| Evidence | Mandatory | Max Age | Phase 5 Validation | Notes |
|----------|-----------|---------|-------------------|-------|
| Audited Financial Statements | YES | 12 months | Extract revenue, profitability, solvency | Accountant-prepared acceptable |
| Annual Report (if public) | CONDITIONAL | 12 months | Extract revenue, employee count, strategy | Verify matches ABN registration |
| Complete Compliance Questionnaire | YES | N/A | Extract all responses, flag incomplete | Legal review required |
| Parent Company Guarantee | CONDITIONAL | 3 years | Extract guarantor entity, ABN, signature | Only if parent co-owns supplier |
| Board Approval | YES | N/A | Confirm dated and signed by director | Corporate structure verification |

---

## Phase 5 Validation Rules

### ABN Declaration
- Extract ABN (11 digits)
- Compare against live ABN Lookup API
- **Pass:** Legal name matches with ≥80% similarity OR exact match
- **Warn:** Name variant (80-95% similarity) → request clarification
- **Fail:** Name mismatch (<80%) OR ABN not found

### Certificate of Currency
- Extract expiry date, insurer name, policy number
- **Pass:** Expiry date ≥30 days in future
- **Warn:** Expiry date within 30 days → request renewal
- **Fail:** Expiry date in past OR cover amount below minimum

### Modern Slavery Statement
- Extract entity name, reporting period, statement date
- **Pass:** Statement dated within 12 months AND for this entity OR parent company
- **Warn:** Statement >12 months old → request updated
- **Fail:** Statement not provided OR entity name does not match supplier

### Bank Account Verification
- Extract account holder name, BSB, account number
- **Pass:** Account holder name ≥80% similar to ABN legal name
- **Warn:** Name variant (70-79% similarity) → request confirmation
- **Fail:** Account holder name <70% similar to legal entity

### Directors & Beneficial Ownership
- Extract all director names
- Run fuzzy match against DFAT Consolidated List
- **Pass:** No matches
- **Warn:** Weak match (75-84%) → request country validation
- **Fail:** Strong match (≥85%) → escalate to Legal

---

## What Phase 5 Reports

For each document, Phase 5 generates:

```
{
  "documentType": "Certificate of Currency",
  "extractedFields": {
    "expiryDate": "2026-02-15",
    "insurer": "XYZ Insurance",
    "policyNumber": "POL-123456",
    "coverAmount": "$10,000,000"
  },
  "validation": {
    "status": "PASS",  // or WARN, FAIL
    "confidence": 0.95,
    "reasoning": "Expiry date 21 days in future, meets minimum cover"
  },
  "pageReferences": [2, 3],
  "action": "Approve"
}
```

---

## Acceptable Variations

### Name Variations
- ✅ "Acme Corp" ↔ "Acme Corporation Pty Ltd" (80-95% match)
- ✅ "Acme Holdings" ↔ "Acme Australia Holdings Pty Ltd" (80-95% match)
- ❌ "Acme" ↔ "Beta Solutions" (too different)

### Insurance Variations
- ✅ Certificate of Currency from broker email (current)
- ✅ "Public Liability" or "General Liability" (same thing)
- ❌ Quotation or proposal (not active coverage)

### Bank Account Variations
- ✅ Account name: "Acme Pty Ltd" vs Legal: "Acme Corporation Pty Ltd" (minor variation)
- ❌ Account name: "John Smith" vs Legal: "Acme Corp" (completely different)

### Modern Slavery Statement Variations
- ✅ Statement is for parent company AND ownership is disclosed
- ❌ Statement is for unrelated parent company without clear ownership

---

## Timeline for Phase 5 Analysis

**Target:** 15 minutes per document

- Parse PDF: 1 min
- Extract facts: 5 min
- Validate extracted values: 5 min
- Generate evidence: 4 min

**Total case (5 documents):** ~75 minutes

---

## Exceptions

Exceptions to this matrix require Chief Procurement Officer approval, documented with business justification.

Common exceptions:
- Overseas supplier (different taxation, insurance requirements)
- Government agency (no commercial insurance)
- Not-for-profit (different modern slavery obligations)

---

## Review and Updates

Updated when:
- New supplier categories added
- Insurance market conditions change
- Modern Slavery Act obligations expand
- ABN Lookup API capabilities improve

**Next review:** 2027-01-01
