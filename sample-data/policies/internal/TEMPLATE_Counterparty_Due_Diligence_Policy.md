# Counterparty Due Diligence Policy

**Version:** 1.0  
**Effective Date:** 2026-01-01  
**Owner:** Compliance & Legal  
**Classification:** Internal

---

## 1. Purpose and Scope

This policy establishes the due-diligence requirements for all supplier and counterparty onboarding. It applies to new suppliers, contractors, service providers, joint-venture partners and financial institutions.

**Out of scope:** Government agencies, internal teams, subsidiaries already on the Supplier Master.

---

## 2. Risk Categories

All counterparties are classified into one of four risk levels:

| Risk Level | Description | ABN Status | Sanctions Match | Insurance | Modern Slavery | Decision |
|------------|-------------|-----------|-----------------|-----------|----------------|----------|
| **Low** | Established, clear documentation | Active | None | Current | Compliant or N/A | Approve |
| **Medium** | Minor inconsistencies, some gaps | Active | Weak match | Minor issue | Partial evidence | Review |
| **High** | Multiple findings, escalation required | Inactive or cancelled | Moderate match | Expired | Missing/inadequate | Escalate |
| **Critical** | Sanctions match, fraud risk | Cannot verify | Strong match | N/A | Not applicable | Block |

---

## 3. Mandatory Checks

Every case requires:

### 3.1 ABN Verification
- **Tool:** ABN Lookup API (live)
- **Rule:** ABN must be active and registered in Australia
- **Finding types:**
  - ✅ Match: Legal name matches application (100-95% similarity)
  - ⚠️ Variant: Legal name differs from application (80-94% similarity) → Request clarification
  - ❌ Mismatch: Name does not match ABN (<80%) → Escalate
  - ❌ Invalid: ABN not found or cancelled → Block

### 3.2 Sanctions Screening
- **Source:** DFAT Consolidated List (refreshed monthly)
- **Method:** Fuzzy name matching (Director, company name, aliases)
- **Confidence thresholds:**
  - Strong match (95%+): CRITICAL → Block pending legal review
  - Moderate match (85-94%): HIGH → Escalate to Compliance
  - Weak match (75-84%): MEDIUM → Request country-of-operations validation
  - Below 75%: No action

### 3.3 Insurance Verification
- **Document:** Certificate of Currency (CoC)
- **Requirements:**
  - Public Liability: Minimum $10 million (or $5M for service providers)
  - Professional Indemnity: Where applicable to services
  - CoC must be current (dated within last 30 days)
- **Findings:**
  - ✅ Current: Approve
  - ⚠️ Expiring within 30 days: Request renewal
  - ❌ Expired: Block until renewed

### 3.4 Modern Slavery Assessment
- **Trigger:** Suppliers with >20 employees OR overseas operations
- **Evidence:** Modern Slavery Statement (per Modern Slavery Act 2018)
- **Findings:**
  - ✅ Statement provided and recent: Approve
  - ⚠️ Statement provided by parent company: Request confirmation
  - ❌ No statement provided: Request or escalate per risk level
  - ❌ Statement >12 months old: Request updated

### 3.5 Bank Account Verification
- **Document:** Bank confirmation letter or statement
- **Validation:**
  - Account holder name should match legal entity
  - ⚠️ Minor variations OK (e.g., "Acme" vs "Acme Pty Ltd")
  - ❌ Significant mismatch: Escalate

---

## 4. Escalation Rules

### ABN Issues
- **Not found or cancelled:** CRITICAL → Block immediately
- **Name mismatch (<80% similarity):** HIGH → Escalate to Procurement
- **Name variant (80-95%):** MEDIUM → Request supporting evidence

### Sanctions Findings
- **Strong match (95%+):** CRITICAL → Escalate to Legal (not proceeding without legal clearance)
- **Moderate match (85-94%):** HIGH → Escalate to Compliance (request additional information)
- **Weak match (75-84%):** MEDIUM → Compliance reviews, requests country validation if needed

### Insurance Issues
- **Expired:** HIGH → Block until renewed
- **Expiring within 30 days:** MEDIUM → Request renewal
- **Below minimum cover:** HIGH → Request upgrade or escalate

### Modern Slavery
- **No statement (required):** HIGH → Request or escalate
- **Statement >12 months:** MEDIUM → Request updated statement
- **Unclear ownership structure:** MEDIUM → Request clarification on parent/subsidiary

---

## 5. Decision Authorities

| Risk Level | Authority | Timeline |
|-----------|-----------|----------|
| **Low** | Procurement Officer (self-approve) | Same day |
| **Medium** | Procurement Manager | 2 business days |
| **High** | Compliance & Procurement Manager | 5 business days |
| **Critical** | Legal + Compliance (joint) | 10 business days |

---

## 6. Decision Options

Once review is complete, the authorized approver records ONE of:

1. **PROCEED** — Approve supplier, proceed to master data
2. **REQUEST INFORMATION** — Return to supplier with specific information gaps
3. **ESCALATE** — Route to higher authority or Legal for review

The system cannot auto-approve. A human must review and record the decision.

---

## 7. Override and Appeal

If a supplier disputes a finding:

- **ABN Mismatch:** Procurement requests supporting legal documentation (deed poll, business name registration)
- **Sanctions Match:** Legal reviews and may submit request to DFAT for clarification
- **Insurance:** Supplier provides updated CoC
- **Modern Slavery:** Supplier confirms ownership structure or provides updated statement

Final override authority: Chief Procurement Officer (with Legal sign-off for sanctions)

---

## 8. Policy Review

This policy is reviewed annually and updated if:
- ABN Lookup API capabilities change
- DFAT sanctions procedures change
- Insurance market conditions shift
- Modern Slavery Act obligations expand

**Next review date:** 2027-01-01
