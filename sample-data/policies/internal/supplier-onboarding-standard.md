# Supplier Onboarding Standard

**Version:** 1.0  
**Effective Date:** 2026-01-01  
**Classification:** Internal Policy  
**Authority:** Procurement & Compliance

---

## 1. PURPOSE

This policy defines the end-to-end workflow for onboarding new suppliers. It establishes mandatory check gates, decision authorities, and failure handling procedures to ensure all suppliers meet organizational risk and compliance standards before engagement.

---

## 2. APPLICABILITY

This standard applies to:
- All new supplier onboarding (first-time engagement)
- Supplier re-verification (every 24 months minimum)
- High-value suppliers (>$100K annual spend) — enhanced checks
- Regulated suppliers (government contracts, finance, health) — risk-based checks

---

## 3. WORKFLOW STAGES

### Stage 1: Initial Registration (Days 1-2)
**Responsible:** Procurement team  
**Gate:** Information completeness

**Required Information:**
- [ ] Legal business name
- [ ] ABN (Australian Business Number)
- [ ] Trading names and historical names
- [ ] Registered office address
- [ ] Contact person + contact details
- [ ] Industry classification
- [ ] Estimated annual spend
- [ ] Goods/services to be provided

**Gate Decision:** PASS / FAIL (incomplete)
- **PASS** → Proceed to Stage 2
- **FAIL** → Request missing information, loop back to collection

---

### Stage 2: Deterministic Screening (Days 2-5)
**Responsible:** Compliance system (automated)  
**Tools:** ABN Lookup API, Sanctions screening, Business Register lookups

**Mandatory Checks:**
1. **ABN Validation** (ABN_001)
   - [ ] ABN must be active in Australian Business Register
   - [ ] Legal name must match supplier declaration
   - Result: PASS / BLOCK_CASE / REQUEST_INFORMATION

2. **Sanctions Screening** (SANC_001-SANC_003)
   - [ ] Check supplier entity against DFAT Consolidated Sanctions List
   - [ ] Check UBO (Ultimate Beneficial Owner) if known
   - [ ] Confidence threshold: >0.92 = escalate, 0.70-0.92 = request clarification
   - Result: PROCEED / ESCALATE_TO_COMPLIANCE / REQUEST_INFORMATION / BLOCK_PENDING_LEGAL

3. **Business Register Status** (ABN_001)
   - [ ] Check for active business registration
   - [ ] Check for pending bankruptcy or liquidation
   - Result: PASS / ALERT / BLOCK_CASE

4. **Duplicates & Variant Names** (SUP_001)
   - [ ] Check Supplier Master for duplicate or variant entries
   - [ ] If found: consolidate records or flag as related entity
   - Result: NEW_ENTITY / DUPLICATE_FOUND / VARIANT_FOUND

**Gate Decision:** PASS / REQUEST_INFORMATION / ESCALATE_TO_COMPLIANCE / BLOCK_CASE
- **PASS** → All checks passed, proceed to Stage 3
- **REQUEST_INFORMATION** → Contact supplier for clarification (e.g., UBO details), re-screen
- **ESCALATE_TO_COMPLIANCE** → Human review required (sanctions match >0.92, active litigation, etc.)
- **BLOCK_CASE** → Supplier must be rejected (failed ABN, known sanctions entity, liquidation)

---

### Stage 3: Policy Alignment Assessment (Days 5-10)
**Responsible:** Document intelligence agent (LLM) + procurement analyst  
**Input:** Supplier-provided documents, public filings

**Required Documentation (depends on supplier type & spend):**

**All Suppliers:**
- [ ] Annual report or financial statement (most recent)
- [ ] Certificate of currency (insurance verification)

**High-Value Suppliers (>$100K):**
- [ ] Audited financial statements (2 years)
- [ ] Bank reference or credit check consent
- [ ] D&B or equivalent credit report

**Regulated Suppliers (government, finance, health, critical infrastructure):**
- [ ] Modern Slavery Statement (if >$100M Australian turnover)
- [ ] ASIC/ABN lookup extracts
- [ ] Beneficial ownership declaration
- [ ] Proof of insurance ($5M+ public liability minimum)

**Assessment Criteria:**

| Assessment | Trigger | Decision |
|------------|---------|----------|
| **Modern Slavery Risk** (See MS Standard) | Any supplier with global supply chain | High-risk: Escalate; Low-risk: Approve |
| **Insurance Adequacy** (INS_001) | All suppliers | $5M minimum public liability cert required |
| **Financial Stability** (FIN_001) | >$50K annual spend | Audited statements, positive working capital |
| **Evidence Completeness** (EVD_001) | All suppliers | All required documents provided and complete |

**Gate Decision:** PASS / REQUEST_INFORMATION / ESCALATE_TO_COMPLIANCE
- **PASS** → All policies satisfied, proceed to Stage 4
- **REQUEST_INFORMATION** → Request additional documents/clarification, re-assess
- **ESCALATE_TO_COMPLIANCE** → Risk assessment required (high modern slavery risk, weak financials, insurance gaps)

---

### Stage 4: Human Approval (Days 10-15)
**Responsible:** Procurement manager + Compliance officer  
**Authority:** Required for suppliers with >$100K spend or policy exceptions

**Review Checklist:**
- [ ] All stages completed (1-3)
- [ ] No blocking issues remain
- [ ] Risk classification assigned (LOW / MEDIUM / HIGH)
- [ ] Exception approvals documented (if any)
- [ ] Approval authority has signing rights for supplier category

**Risk Classification:**
- **LOW** — All checks passed, no concerns, standard payment terms
- **MEDIUM** — Minor concerns (e.g., new business <2 years), enhanced monitoring required
- **HIGH** — Significant concerns (modern slavery risk, weak financials), restricted payment terms, quarterly re-assessment

**Approval Decision:** APPROVED / CONDITIONALLY APPROVED / REJECTED
- **APPROVED** — Supplier can be engaged, standard contract terms
- **CONDITIONALLY APPROVED** — Supplier approved with conditions (e.g., monthly reporting, escrow payment, restricted spend)
- **REJECTED** — Supplier cannot be engaged, provide written reason

---

### Stage 5: Engagement (Days 15+)
**Responsible:** Procurement + contracts team

**Actions Upon Approval:**
- [ ] Generate supplier master record
- [ ] Assign Supplier ID
- [ ] Create contract (with risk-based terms)
- [ ] Set up vendor in ERP
- [ ] Schedule first review date (12/24 months based on risk)
- [ ] Document approval and risk classification in audit trail

**Monitoring** (ongoing):
- **LOW risk:** Annual verification (due every 24 months)
- **MEDIUM risk:** Semi-annual review (due every 12 months)
- **HIGH risk:** Quarterly review (due every 3 months) + monthly reporting

---

## 4. FAILURE HANDLING

### Scenario A: Supplier Fails Deterministic Screening
**Issue:** ABN invalid, sanctions match, liquidation pending  
**Authority:** Compliance officer (no escalation needed)  
**Action:** Send rejection letter with reason, archive case

**Escalation:** If supplier disputes:
- Compliance manager reviews with supplier (offer opportunity to clarify)
- If still fails: escalate to Chief Compliance Officer for final decision

### Scenario B: Supplier Fails Policy Assessment
**Issue:** Modern slavery risk HIGH, insurance gap, financials weak  
**Authority:** Compliance manager  
**Action:** 
1. Request additional information/remediation plan
2. If provided: re-assess
3. If not provided or still insufficient: escalate to Category Head

**Escalation Path:**
- Category Head can approve HIGH-risk supplier with documented business justification
- Requires enhanced monitoring agreement with supplier
- CFO approval if supplier spend >$500K

### Scenario C: Information Gaps or Clarifications Needed
**Issue:** Missing documents, unclear UBO ownership, conflicting data  
**Authority:** Procurement analyst  
**Action:**
1. Request specific information with 10-business-day deadline
2. Re-assess upon receipt
3. If not provided: move to decision (Escalate or Reject)

### Scenario D: Supplier Escalated for Human Review
**Issue:** Multiple concerns require human judgment (not blocking)  
**Authority:** Procurement manager + Compliance officer  
**Action:**
1. Review all evidence and AI assessments
2. Conduct reference checks if needed
3. Make final decision: APPROVED / CONDITIONALLY APPROVED / REJECTED

---

## 5. DECISION AUTHORITIES & LIMITS

| Decision | Authority | Limit | Escalation |
|----------|-----------|-------|------------|
| PASS Stage 2 (Deterministic) | System (automated) | No limit | N/A |
| REQUEST INFO | Analyst | No limit | No |
| ESCALATE (non-blocking) | Analyst → Manager | No limit | Manager decides |
| APPROVE (LOW risk, <$100K) | Procurement manager | $100K | CFO if >$500K |
| APPROVE (MEDIUM/HIGH risk) | Compliance officer | No limit | CFO if HIGH + >$100K |
| CONDITIONALLY APPROVE | Manager + Compliance | $100K | CFO if >$500K |
| REJECT | Compliance officer | No limit | CFO appeal allowed |

---

## 6. TIMELINE & SLA

| Stage | Duration | SLA |
|-------|----------|-----|
| Stage 1 (Registration) | 1-2 days | 3 days |
| Stage 2 (Deterministic screening) | 2-3 days | 5 days |
| Stage 3 (Policy assessment) | 5 days | 10 days |
| Stage 4 (Human approval) | 5 days | 5 days |
| **Total onboarding** | **13-15 days** | **20 days** |

**Expedited Process (for low-risk, <$50K suppliers):**
- Skip Stage 3 if all Stage 2 checks pass with no findings
- Reduced SLA: 7 days total

---

## 7. RE-VERIFICATION SCHEDULE

| Risk Level | Re-verification Frequency | Triggers |
|------------|---------------------------|----------|
| LOW | Every 24 months | Change in beneficial ownership, ABN status change |
| MEDIUM | Every 12 months | Contract >$1M awarded, new regulatory requirement |
| HIGH | Every 3 months | Ongoing monitoring, monthly reporting required |

**Trigger Events** (immediate re-assessment required):
- Supplier appears on new sanctions list
- Public announcement of bankruptcy/restructuring
- Change in executive leadership (>50% turnover)
- Negative media coverage regarding sanctions, fraud, or labor violations
- Breach of contract or compliance violation

---

## 8. DOCUMENTATION & AUDIT TRAIL

All onboarding decisions must be documented with:
- [ ] Date and decision
- [ ] Authority making decision (name, title)
- [ ] Basis for decision (which checks passed/failed, policy references)
- [ ] Risk classification assigned
- [ ] Conditions (if conditional approval)
- [ ] Signature/approval

**Retention:** Maintain records for 7 years (per Australian consumer law and tax obligations)

---

## 9. POLICY REVIEW

This policy will be reviewed:
- Annually (minimum)
- Upon significant regulatory changes
- Upon process failures or escalations >5 per year

**Last Reviewed:** [Date]  
**Next Review Date:** [Date + 12 months]

---

## 10. RELATED POLICIES

- Modern Slavery Assessment Standard (Stage 3 detailed assessment)
- Human Approval and Override Policy (Stage 4 authorities)
- Due Diligence Rule Catalogue (executable rules for stages 2-3)
- Insurance Requirements Policy (INS_001 verification)

---

## APPENDIX A: Document Checklist

```
STAGE 1: Initial Registration
☐ Legal business name
☐ ABN
☐ Trading names
☐ Registered office address
☐ Contact information
☐ Industry classification

STAGE 2: Deterministic Screening
☐ ABN validation (PASS)
☐ Sanctions screening (PROCEED)
☐ Business register status (ACTIVE)
☐ Duplicate check (NEW_ENTITY)

STAGE 3: Policy Assessment
☐ Annual report/financial statement
☐ Insurance certificate of currency
[Additional docs based on supplier category]

STAGE 4: Human Approval
☐ All stages completed
☐ No blocking issues
☐ Risk classification: LOW/MEDIUM/HIGH
☐ Approval authority signature
☐ Conditions documented (if conditional)

STAGE 5: Engagement
☐ Supplier master record created
☐ Supplier ID assigned
☐ Contract generated
☐ Review schedule set
```
