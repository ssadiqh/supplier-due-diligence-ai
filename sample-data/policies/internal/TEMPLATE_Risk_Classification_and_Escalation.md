# Risk Classification and Escalation Standard

**Version:** 1.0  
**Effective Date:** 2026-01-01  
**Owner:** Compliance & Procurement  

---

## 1. Risk Scoring Framework

Cases are assigned a risk level (Low, Medium, High, Critical) based on **findings from all phases**.

---

## 2. Risk Calculation

### Phase 3: Rules Engine (Deterministic)

Rule severity determines initial risk:

| Rule | Severity | Finding |
|------|----------|---------|
| ABN not found/cancelled | CRITICAL | Risk += 3 |
| ABN name mismatch | HIGH | Risk += 2 |
| Insurance expired | HIGH | Risk += 2 |
| Insurance expiring <30 days | MEDIUM | Risk += 1 |
| Modern slavery statement missing | MEDIUM | Risk += 1 |
| Bank account name mismatch | MEDIUM | Risk += 1 |
| Required document missing | MEDIUM | Risk += 1 |

### Phase 4-6: Verification Results

- ABN Lookup: Adds confidence or flags variant
- Sanctions Screening: Strong match adds 3, moderate adds 1
- Entity Resolution: Duplicate/conflict adds 1

### Phase 5: Document Extraction

- Missing/incomplete documents add points
- Low-confidence extractions add warnings

### Phase 7: Policy Assessment

- Policy gaps or unmet obligations add points
- Agent recommendations inform risk assessment

---

## 3. Risk Level Definition

### Low Risk (Score: 0-2)
- ✅ ABN active, legal name matches
- ✅ No sanctions matches
- ✅ Insurance current and adequate
- ✅ All required documents provided
- ✅ No policy gaps identified
- ✅ No extraction conflicts

**Decision:** PROCEED (Procurement Officer approval)

**Timeline:** Same day

---

### Medium Risk (Score: 3-5)
- ⚠️ Minor findings requiring clarification
- ⚠️ Examples:
  - Name variant (trading name differs from legal)
  - Insurance expiring within 30 days
  - Modern slavery statement dated >9 months ago
  - Weak sanctions match (officer confirms NOT the matched entity)
  - Incomplete answers in questionnaire

**Action:** REQUEST INFORMATION or proceed with conditions

**Decision:** Procurement Manager + Compliance Officer (joint)

**Timeline:** 2-5 business days

---

### High Risk (Score: 6-10)
- 🚨 Multiple findings requiring escalation
- 🚨 Examples:
  - Sanctions moderate match (85-94%)
  - Insurance about to expire
  - Modern slavery statement missing (required)
  - Bank account name significantly differs from legal entity
  - Required document missing
  - Unresolved entity discrepancies

**Action:** ESCALATE (request additional information or further investigation)

**Decision:** Compliance Manager + Legal (for sanctions findings)

**Timeline:** 5-10 business days

---

### Critical Risk (Score: 11+)
- 🛑 Block immediately
- 🛑 Examples:
  - ABN not found or cancelled
  - Sanctions strong match (≥95%)
  - Fraud indicators detected
  - Multiple high-risk findings combined

**Action:** BLOCK (pending legal review)

**Decision:** Legal Counsel (+ Chief Procurement Officer for override)

**Timeline:** 10-20 business days (may require external investigation)

---

## 4. Escalation Paths

### Low → Proceed
Procurement Officer documents decision in case file.
```
Decision: PROCEED
Approved by: [Officer Name]
Date: [Date]
Notes: "No findings. Proceeding to master data."
```

### Medium → Request Information
Return to supplier with specific questions:
```
Request Information:
1. "Your insurance expires in [X days]. Please provide updated CoC."
2. "Your modern slavery statement is [X months] old. Please provide 2024 version."
3. "Bank account name differs from ABN. Please confirm account holder."

Resubmit by: [Date + 5 business days]
```

### Medium → Proceed with Conditions
Approve but add conditions:
```
Decision: PROCEED WITH CONDITIONS
Condition 1: Provide updated insurance by [date]
Condition 2: Modern slavery statement due by [date]
Condition 3: Address confirmation by [date]

Approval: [Manager Name]
Conditions must be met before final supplier activation.
```

### High/Critical → Escalate
Route to higher authority with summary:
```
Escalation: HIGH RISK
Summary: Insurance expired 15 days ago + Modern slavery statement missing
Sanctions: Weak match on director name (John Anderson - requires confirmation)
Finding: Bank account held in subsidiary name (requires clarification)

Escalated to: Compliance Manager
For review by: [Date]
Required decision: REQUEST INFO / ESCALATE / BLOCK
```

---

## 5. Appeal and Override

### Medium Risk Appeals
Supplier may dispute findings by providing:
- Updated documents (insurance, modern slavery statement)
- Clarifications (bank account explanation, name variations)
- Supporting evidence (deed poll, business name registration)

**Authority:** Procurement Manager reviews and may change decision to Low Risk

### High/Critical Risk Appeals
Supplier may request review by providing:
- Legal documentation
- DFAT clarification (for sanctions)
- Third-party verification

**Authority:** Chief Procurement Officer (joint with Legal for sanctions)

**Approval:** Documented in case file with clear reasoning

---

## 6. Time-Sensitive Findings

Some findings have time urgency:

| Finding | Escalation | Timeline |
|---------|-----------|----------|
| Insurance expires <7 days | IMMEDIATE | Block until renewed (1-3 days) |
| Sanctions strong match | IMMEDIATE | Escalate to Legal same day |
| ABN cancelled | IMMEDIATE | Block same day |
| Bank fraud indicators | IMMEDIATE | Escalate to Legal same day |

---

## 7. Combining Findings

When multiple findings exist:

```
Finding 1: Insurance expired (Severity: HIGH) = +2 points
Finding 2: Modern slavery statement missing (Severity: MEDIUM) = +1 point
Finding 3: Weak sanctions match (Severity: MEDIUM) = +1 point
---
Total Risk Score: 4 → MEDIUM RISK

Decision: REQUEST INFORMATION
- Updated insurance
- Modern slavery statement
- Sanctions clarification (from supplier)
```

---

## 8. Risk Reassessment

A case moves to a higher risk level if:
- New information emerges
- Supplier fails to respond to requests
- DFAT list is updated with matching entity

A case moves to lower risk if:
- Supplier provides requested information
- Findings are resolved
- Appeals are approved

---

## 9. Documentation

Every decision is documented:

```json
{
  "caseId": "SUP-000123",
  "riskScore": 4,
  "riskLevel": "MEDIUM",
  "findings": [
    {"type": "InsuranceNearExpiry", "severity": "MEDIUM", "points": 1},
    {"type": "ModernSlaveryStatementOld", "severity": "MEDIUM", "points": 1}
  ],
  "decision": "REQUEST_INFORMATION",
  "requestedInformation": ["Updated insurance CoC", "2024 Modern slavery statement"],
  "deadline": "2026-02-01",
  "approvedBy": "procurement-manager@company.com",
  "approvalDate": "2026-01-20",
  "notes": "Minor documentation gaps. Standard re-submission requested."
}
```

---

## 10. Policy Review

Updated when:
- Risk framework improves
- New risk factors identified
- Escalation procedures change

**Next review:** 2027-01-01
