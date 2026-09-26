# Human Approval and Override Policy

**Version:** 1.0  
**Effective Date:** 2026-01-01  
**Classification:** Internal Policy  
**Authority:** CEO, Chief Compliance Officer

---

## 1. PURPOSE

This policy establishes the framework for human decision-making in supplier onboarding. It defines:
- Which decisions require human approval (AI cannot decide alone)
- Who has authority to approve different supplier categories
- How and when humans can override AI recommendations
- Prohibited AI decisions (where humans must always decide)
- Audit trail and accountability requirements

**Core Principle:** Humans decide. AI recommends.

---

## 2. SCOPE

This policy applies to:
- All supplier onboarding decisions
- All supplier escalations from AI screening
- All overrides of AI recommendations
- All policy exceptions and risk acceptances

---

## 3. DECISION TYPES

### Type A: Automated Decisions (AI Decides)
These decisions are fully automated and do NOT require human approval:

| Decision | Criteria | Authority |
|----------|----------|-----------|
| PASS Stage 2 | ABN active + Sanctions clear + No duplicates | System |
| REQUEST INFORMATION | Incomplete data or clarification needed | System |
| BLOCK (ABN Inactive) | ABN status != "Active" in registry | System |
| BLOCK (Sanctions Match 1.0) | Exact entity match on sanctions list | System |

**Why automated:** These decisions have zero discretion (binary pass/fail against objective criteria)

---

### Type B: Assisted Decisions (AI Recommends, Humans Approve)
These decisions require human review and approval:

| Decision | AI Input | Human Authority | Required Because |
|----------|----------|------------------|-----------------|
| REQUEST INFORMATION (Sanctions) | Match 0.70-0.92 | Analyst | Ambiguous matching requires judgment |
| ESCALATE (Policy Assessment) | Risk factors + gaps | Procurement Manager | Policy interpretation requires judgment |
| MEDIUM RISK Approval | Risk score 3-6 | Manager | Risk tolerance is business decision |
| HIGH RISK Escalation | Risk score 7+ or violations | Compliance Manager | Strategic decision about supplier suitability |

**Approval Process:**
1. AI completes assessment → generates recommendations + evidence
2. Human reviews AI analysis
3. Human may:
   - **APPROVE** AI recommendation as-is
   - **MODIFY** conditions/restrictions
   - **OVERRIDE** recommendation (see Type C below)
4. Human documents decision + reasoning
5. Decision recorded with audit trail (who, when, why)

---

### Type C: Override Decisions (Humans Override AI, with Approval Chain)
These decisions contradict AI recommendations and require escalation:

| Scenario | AI Says | Human Overrides | Authority | Justification Required |
|----------|---------|-----------------|-----------|------------------------|
| Accept HIGH risk supplier | REJECT | APPROVE | CFO | Business case, risk mitigation |
| Reject LOW risk supplier | APPROVE | REJECT | Manager | Reputational, regulatory, business |
| Waive insurance requirement | BLOCK (Insurance gap) | APPROVE without | CFO | Business justification, risk acknowledged |
| Waive Modern Slavery audit | CONDITIONAL (audit required) | APPROVE without | Chief Compliance Officer | Regulatory exception, documented rationale |

**Override Process:**
1. AI recommends one decision
2. Human proposes different decision
3. Human documents override rationale (business case, risk acceptance, etc.)
4. Override must be approved by override authority (see table above)
5. Decision recorded with full audit trail:
   - AI recommendation + confidence/reasoning
   - Human decision + reasoning
   - Override authority + approval
   - Timestamp

**Example:**
```
AI Assessment: HIGH modern slavery risk (score 8)
AI Recommendation: REJECT supplier

Human Override Request:
- Authority: CFO
- Reason: "Strategic supplier for critical supply chain component. 
           No viable alternatives. Proposing CONDITIONAL APPROVAL 
           with 3rd-party labor audit within 90 days."
- Conditions: [list mitigation measures]
- Business case: "Without this supplier, production halts"

CFO Decision: APPROVE override
Rationale: "Risk acknowledged and accepted. Conditions mitigate to 
           acceptable level. Critical business need justifies exception."

Audit Record: Created with all details above, timestamp, CFO signature
```

---

## 4. PROHIBITED AI DECISIONS

**These decisions can NEVER be made by AI alone. Human authority is mandatory:**

### 4.1 Supplier Rejection (for non-blocking reasons)
**Prohibited:** AI recommending REJECT based on:
- Modern slavery risk assessment
- Financial instability
- Weak insurance
- Policy gaps
- Risk factors

**Why prohibited:** Rejection of a potentially qualified supplier is too significant for AI alone

**Required:** Compliance manager or CFO must review and approve REJECT decision

---

### 4.2 Risk Exceptions (HIGH → MEDIUM or HIGH → LOW)
**Prohibited:** AI downgrading risk classification without human review

**Why prohibited:** Risk classification determines oversight level; AI cannot make this judgment call

**Required:** Compliance manager must review AI assessment and approve any risk downgrade

---

### 4.3 Waiving Mandatory Controls
**Prohibited:** AI recommending to skip or waive:
- Sanctions screening
- ABN validation
- Insurance requirement (if policy-mandated)
- Modern Slavery Statement (if >$100M turnover)
- Financial assessment (if >$50K spend)

**Why prohibited:** Mandatory controls exist for regulatory/risk reasons; cannot be waived by system

**Required:** Appropriate authority (CFO, Compliance Manager, Category Head) must explicitly approve waiver

---

### 4.4 Executive Exceptions
**Prohibited:** AI approving suppliers when normal process requires exception:
- Supplier that fails scoring but has strategic value
- Supplier with prior violations or regulatory findings
- Supplier with ownership changes or control issues
- Supplier in sanctioned country or high-risk jurisdiction

**Why prohibited:** These decisions carry organizational risk and require executive judgment

**Required:** CFO or Chief Compliance Officer must review and approve

---

## 5. APPROVAL AUTHORITIES

### 5.1 Approval Matrix

| Decision | Spend <$50K | Spend $50-100K | Spend $100-500K | Spend >$500K |
|----------|------------|----------------|-----------------|--------------|
| **LOW Risk** (Score 0-2) | Analyst ✓ | Manager ✓ | Manager ✓ | CFO ✓ |
| **MEDIUM Risk** (Score 3-6) | Manager ✓ | Manager ✓ | Manager + Compliance | CFO |
| **HIGH Risk** (Score 7+) | Escalate | Escalate | CFO | CFO + Board approval |
| **REJECT** (blocking) | System ✓ | System ✓ | System ✓ | System ✓ |
| **OVERRIDE** (against AI) | Manager | CFO | CFO + Compliance | CFO + Board |

**Legend:**
- ✓ = Authority can approve alone
- Manager + Compliance = Both required
- CFO + Board = Sequential approval required

---

### 5.2 Authority Definitions

| Title | Level | Spend Authority | Can Approve | Can Override |
|-------|-------|-----------------|-------------|--------------|
| **Analyst** | IC | N/A | LOW risk only | NO |
| **Procurement Manager** | Manager | $0-100K | LOW + MEDIUM | With Compliance Manager |
| **Category Head** | Director | $100K-500K | MEDIUM + exceptions | With CFO approval |
| **Compliance Manager** | Manager | All | All escalations | Policy exceptions only |
| **Chief Compliance Officer** | Executive | All | All | All |
| **CFO** | Executive | >$500K | All + overrides | All with CEO notification |
| **CEO** | Executive | Unlimited | All + board items | All (final authority) |

---

### 5.3 Conflict of Interest Restrictions

**These authorities CANNOT approve if conflicted:**
- Approving relative or spouse as supplier
- Approving supplier where family member holds significant ownership
- Approving supplier owned by recent company departing employee (within 12 months)
- Approving supplier referred by directly-interested party

**If conflict exists:**
1. Recuse yourself from approval
2. Escalate to next-level authority
3. Document recusal in audit trail

---

## 6. OVERRIDE FRAMEWORK

### 6.1 When Overrides are Allowed
Overrides are permitted in LIMITED circumstances:

| Scenario | Condition | Authority |
|----------|-----------|-----------|
| Strategic supplier with HIGH risk | Business case justifies, mitigation plan provided | CFO |
| Sole-source supplier | No viable alternatives documented | CFO + Compliance |
| Regulatory exemption | Regulatory authority exempts requirement | Chief Compliance Officer |
| Technology/vendor lock-in | Strategic technology dependency documented | CFO |
| Market recovery supplier | Temporary shortage, short-term engagement | CFO |

### 6.2 When Overrides are PROHIBITED
Overrides are NOT permitted for:
- Supplier on international sanctions list (exact match, confidence 1.0)
- Supplier with known current human trafficking or forced labor
- Supplier subject to regulatory ban or debarred
- Supplier in country subject to comprehensive trade embargo

**Why:** These violate legal/regulatory requirements, not just internal policy

---

## 7. OVERRIDE PROCESS (Step-by-Step)

**Step 1: AI Assessment Complete**
- AI recommends REJECT, BLOCK, or HIGH RISK ESCALATE
- Documentation includes: assessment, recommendation, evidence, confidence

**Step 2: Human Questions AI Recommendation**
- Procurement manager or category head believes override is justified
- Reviews AI evidence + business case

**Step 3: Override Proposal**
```
OVERRIDE PROPOSAL

Supplier: _____________________
AI Recommendation: REJECT (HIGH modern slavery risk, score 8)

Override Proposed Decision: CONDITIONAL APPROVAL

Rationale:
- Business case: [explain why supplier is needed]
- Mitigation plan: [how will risks be managed]
- Alternatives: [why other suppliers insufficient]
- Risk acknowledgment: [confirm stakeholders understand risk]

Proposed Conditions:
- [Specific conditions to mitigate risk]
- [Monitoring schedule]
- [Escalation triggers]

Prepared By: [Name, Title]
Date: ___________
```

**Step 4: Override Authority Reviews**
- CFO (for HIGH risk/spend >$500K) OR
- Compliance Manager (for policy exceptions) OR
- CEO (for strategic overrides)

**Step 5: Authority Decision**
```
OVERRIDE DECISION

Authority: _______________
Decision: APPROVED / REJECTED

Rationale: [Why authority agrees/disagrees with override]

Conditions [if approved]:
- [Any additional conditions beyond proposal]

Risk Acknowledgment: [Authority confirms understanding of residual risk]

Authority Signature: __________  Date: _________
```

**Step 6: Audit Trail Documentation**
- Original AI recommendation with evidence
- Human override proposal with business case
- Authority decision and rationale
- All conditions and monitoring requirements
- Timestamp and signatures
- Maintained for 7 years

---

## 8. REVERSALS & ESCALATION TRIGGERS

### 8.1 When Approved Suppliers Can Be Reversed

An approved supplier can be **reversed to REJECTED** if:
- Post-approval facts emerge (e.g., sanctions match found, bankruptcy filing)
- Supplier violates conditions (e.g., fails to provide required audit)
- Regulatory/legal requirement changes (e.g., country added to sanctions)
- Supplier fails to meet onboarding commitments (e.g., doesn't get required certification)

**Process:**
1. Compliance manager identifies reason for reversal
2. Notify supplier (if safe to do so) and allow 30-day response
3. Re-assess with new information
4. If still fails: reject and terminate contract (following contract termination clause)
5. Document reversal in audit trail

---

### 8.2 Escalation Triggers (Monitor Approved Suppliers)

**Re-escalate to Compliance Manager if:**
- [ ] Supplier appears on new sanctions list
- [ ] Public filing shows insolvency/bankruptcy
- [ ] Negative press: labor violations, fraud, sanctions evasion
- [ ] Supplier fails to provide required monitoring documents
- [ ] Whistleblower complaint about labor practices
- [ ] Supplier contract violation or compliance breach
- [ ] Change in UBO (>50% ownership change)

**Action:** Compliance manager re-assesses risk and may reverse approval if necessary

---

## 9. OVERRIDE STATISTICS & MONITORING

**The organization must track:**
- [ ] Total suppliers approved: ___
- [ ] Approvals that matched AI recommendation: ___%
- [ ] Overrides approved (against AI REJECT): ___
- [ ] Overrides approved (against AI HIGH RISK): ___
- [ ] Reversals (approved suppliers later rejected): ___
- [ ] Outcome of overridden suppliers: [% met conditions, % escalated, % terminated]

**Quarterly Review:**
- CFO + Chief Compliance Officer review override statistics
- Assess: Is AI working well? Are overrides justified? Any patterns?
- Report to Board if >10% of approvals override AI recommendation

**Why track:** Detects if AI is too conservative (too many overrides) or too liberal (poor override outcomes)

---

## 10. RESPONSIBILITY & ACCOUNTABILITY

### 10.1 AI System Responsibility
- [ ] Provide complete assessment with evidence
- [ ] Show confidence levels and reasoning
- [ ] Flag uncertainties and ambiguities
- [ ] Never claim certainty for judgment calls
- [ ] Make recommendations clear (APPROVE / CONDITIONAL / ESCALATE / REJECT)

### 10.2 Human Approver Responsibility
- [ ] Review AI assessment thoroughly
- [ ] Document reasoning for approval/override
- [ ] Confirm authority level for decision
- [ ] Check for conflicts of interest
- [ ] Ensure audit trail is complete
- [ ] Monitor approved suppliers per schedule

### 10.3 Escalation Authority Responsibility
- [ ] Review override proposals promptly (SLA: 5 business days)
- [ ] Ensure business case is documented
- [ ] Confirm risk mitigation is adequate
- [ ] Approve or reject with documented rationale
- [ ] Monitor reversed suppliers for lessons learned

---

## 11. CONSEQUENCES OF NON-COMPLIANCE

**If human approval bypassed:**
- Approval is VOID (supplier not actually onboarded)
- Case escalated to Compliance Manager
- Perpetrator: Coaching/retraining required
- Repeat offenses: Escalation to HR

**If prohibited decision made by AI:**
- Immediately escalate to Chief Compliance Officer
- Audit all recent decisions from that AI version
- Remediate decisions that violated this policy
- Implement safeguards to prevent recurrence

**If override conditions not monitored:**
- Monitoring gaps escalated to Category Head
- Supplier risk re-assessed as if condition failed
- May trigger contract termination

---

## 12. DOCUMENTATION TEMPLATE

### Approval Record (Required for all non-automated decisions)

```
SUPPLIER APPROVAL RECORD

Supplier Name: _____________________  Date: __________
Supplier ID: _____________________

AI ASSESSMENT SUMMARY:
- Risk Classification: LOW / MEDIUM / HIGH
- Risk Score: ____ / 12
- AI Recommendation: APPROVE / CONDITIONAL / ESCALATE / REJECT
- Confidence Level: ____%

HUMAN REVIEW:
Reviewer Name: ______________________  Title: ____________
Review Date: _________  Time Spent: _____ hours
Approval Level: Analyst / Manager / Director / Executive

AI EVIDENCE REVIEWED:
☐ ABN validation results
☐ Sanctions screening results
☐ Financial assessment
☐ Modern slavery assessment
☐ Insurance verification
☐ Duplicate check
☐ Document completeness

HUMAN DECISION:
☐ Approve per AI recommendation
☐ Modify conditions (see below)
☐ Override AI recommendation (see below)

IF CONDITIONAL APPROVAL:
Conditions:
1. _________________________________________________
2. _________________________________________________
3. _________________________________________________

Monitoring Schedule: [Frequency]
Escalation Triggers: [What would cause reversal]

IF OVERRIDE:
Reason for Override: ___________________________________
Business Justification: ________________________________
Risk Mitigation: _______________________________________
Override Authority: ____________________________________
Override Approval Date: ________________________________

SIGNATURES:
Approver: ________________________  Date: __________
Override Authority (if override): _____________  Date: __________

AUDIT TRAIL:
Timestamp: __________
System ID: __________
Notes: ________________________________________________
```

---

## 13. RELATED POLICIES

- Supplier Onboarding Standard (decision gates)
- Modern Slavery Assessment Standard (policy framework)
- Due Diligence Rule Catalogue (rule definitions)

---

## 14. POLICY REVIEW

This policy will be reviewed:
- Annually (minimum)
- Upon major AI system changes
- Upon regulatory changes
- Upon significant override or reversal events

**Last Reviewed:** [Date]  
**Next Review Date:** [Date + 12 months]

---

## APPENDIX A: Quick Reference - Who Approves What

```
SPEND <$50K, LOW RISK
→ Procurement Analyst (or Manager)

SPEND $50-100K, LOW RISK
→ Procurement Manager

SPEND $100-500K, LOW RISK
→ Procurement Manager

SPEND >$500K, LOW RISK
→ CFO

SPEND <$50K, MEDIUM RISK
→ Procurement Manager

SPEND $50-100K, MEDIUM RISK
→ Procurement Manager

SPEND $100-500K, MEDIUM RISK
→ Manager + Compliance Manager

SPEND >$500K, MEDIUM RISK
→ CFO + Compliance Manager

SPEND $100K+, HIGH RISK
→ CFO + Compliance Manager

OVERRIDE AGAINST AI REJECT
→ CFO (+ Compliance if policy exception)

OVERRIDE AGAINST AI ESCALATE
→ CFO

POLICY WAIVER (Mandatory Control)
→ Chief Compliance Officer (or CFO for spend)

SANCTIONED COUNTRY/ENTITY
→ CANNOT OVERRIDE (except CEO if legal exemption exists)
```
