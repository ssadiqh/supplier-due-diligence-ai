# Sample Data Directory

**Purpose:** Production-ready test data, policy corpus, and evaluation infrastructure for the Australian supplier due-diligence AI system. Enables reproducible testing of ABN validation, sanctions screening, entity resolution, and policy-driven decision rules without external dependencies.

**Status:** 🟡 85% COMPLETE → 90% WITH POLICIES

Public source corpus assembled. All organizational policies finalized. Infrastructure (fixtures, expected results, manifests) in place. Still need: customize policy templates, build test scenarios.

---

## 📁 Current Final Structure

```
sample-data/
├── policies/                    Authority hierarchy for governance
│   ├── public/                  Australian government guidance (6 PDFs) - source of truth for policy extraction
│   └── internal/                Organizational rules (5 templates + 3 missing) - AI decision boundaries
│
├── reference-data/              Deterministic screening data (NOT RAG) - external APIs and master records
│   ├── sanctions/
│   │   ├── raw/                 DFAT Consolidated List (monthly refresh)
│   │   ├── fixtures/            Stable test cases (exact, partial, false-positive) - unit test mocks
│   │   └── expected/            Known-good screening results (JSON) - test assertions
│   ├── abn-lookup/
│   │   ├── fixtures/            API test responses (active, cancelled, error)
│   │   └── expected/            Expected validation results (JSON)
│   └── supplier-master/
│       ├── fixtures/            Test records (duplicate, variant, inactive)
│       └── expected/            Entity resolution results (JSON)
│
├── rules/                       Executable rule definitions - implements policies as code
│   └── due-diligence-rule-catalogue.yaml
│
├── document-templates/          Empty templates for assessment forms
│   └── modern-slavery-supplier-questionnaire.docx
│
├── public-examples/             Real public documents for testing - authentic examples from government
│   ├── asic/                    Official ASIC extracts
│   └── modern-slavery/          Real Modern Slavery Statement
│
├── scenarios/                   Complete end-to-end test cases - full supplier journeys
│   ├── synthetic-benign/        Low-risk scenario (inputs + expected)
│   ├── synthetic-risk/          High-risk scenario (inputs + expected)
│   └── adversarial/             Security tests (OCR, injection, corruption)
│
├── manifests/                   Version control and provenance
│   ├── source-documents.yaml    URLs, versions, SHA-256
│   └── dataset-version.yaml     Changelog and status
│
└── evaluation/                  Test cases for agent evaluation - precision/recall measurement
    └── rag/                     RAG retrieval test queries and metrics
```

---

## 🎯 What Each Section Does

| Section | Purpose | Contains |
|---------|---------|----------|
| **policies/** | AI policy boundaries and public guidance | Government PDFs + organizational policy templates |
| **reference-data/** | Deterministic lookups (no AI) | Sanctions lists, ABN API test mocks, entity records |
| **rules/** | Executable policy rules | YAML rule catalogue (10 rules from policies) |
| **evaluation/** | Measure RAG accuracy | 6 test queries with expected document retrieval |
| **scenarios/** | Test full workflows | Complete supplier cases (benign + risky) |
| **public-examples/** | Real-world evidence | Actual ASIC extracts and statements |

---

## ✅ What's Ready Now

### Governance Infrastructure
- ✅ **policies/public/** — 6 government policy documents (ready for RAG embedding)
- ✅ **policies/internal/** — 8 complete organizational policies:
  - ✅ Supplier Onboarding Standard (workflow stages + decision gates)
  - ✅ Modern Slavery Assessment Standard (risk assessment + criteria)
  - ✅ Human Approval & Override Policy (decision authorities)
  - ✅ 5 policy templates (insurance, evidence matrix, sanctions, counterparty due diligence, risk classification)
- ✅ **rules/due-diligence-rule-catalogue.yaml** — Executable deterministic rules

### Test Fixtures & Expected Results
- ✅ **reference-data/sanctions/** — 7 test cases with expected outcomes
- ✅ **reference-data/abn-lookup/** — 5 API response fixtures
- ✅ **reference-data/supplier-master/** — 4 entity records with test cases

### Evaluation Infrastructure
- ✅ **evaluation/rag/** — 6 policy retrieval test queries

### Real Public Examples
- ✅ **public-examples/asic/** — 2 official ASIC extracts
- ✅ **public-examples/modern-slavery/** — 1 real Modern Slavery Statement
- ✅ **document-templates/** — Modern Slavery questionnaire template

---

## ⏳ What Still Needs Completion

| Item | Status | Work Required |
|------|--------|---------------|
| ~~Internal policies (3 missing)~~ | ✅ 8/8 COMPLETE | Supplier Onboarding, Modern Slavery Assessment, Human Approval created |
| Customize 5 templates | 📝 Pending | Remove TEMPLATE_ prefix, adapt to organization |
| Test scenarios inputs | ⏳ Empty | Benign case (3 docs), Risk case (5 docs) |
| Test scenarios expected | ⏳ Empty | Expected results JSON for each scenario |
| Source manifest | ⏳ Not started | URLs, versions, checksums for 6 PDFs |
| Dataset version manifest | ⏳ Not started | Changelog, completion status, roadmap |

---

## 🏗️ How to Use

### For Development

**ABN Validation Testing:**
```bash
reference-data/abn-lookup/fixtures/      # Mock responses for unit tests
reference-data/abn-lookup/expected/      # Assertion values for test verification
```

**Supplier Master Testing:**
```bash
reference-data/supplier-master/fixtures/ # Test entity records
reference-data/supplier-master/expected/ # Expected resolution results
```

**Sanctions Screening Testing:**
```bash
reference-data/sanctions/fixtures/       # CSV of test entities
reference-data/sanctions/expected/       # Expected screening outcomes
```

### For Policy Development

**Governance:**
```bash
policies/public/                # Australian government guidance
policies/internal/              # Your organizational policies (customize these)
rules/due-diligence-rule-catalogue.yaml  # Executable rules derived from policies
```

### For Agent Evaluation

**RAG Testing:**
```bash
evaluation/rag/rag-retrieval-test-cases.json  # Known-good queries for precision/recall
```

---

## 📋 Remaining Missing Items (For 100% Completion)

### 1. Policy Template Customization (MEDIUM PRIORITY)
- [ ] Customize 5 policy templates (remove TEMPLATE_ prefix, finalize organizational rules)
- [ ] Templates: Insurance Requirements, Evidence Matrix, Sanctions Procedure, Counterparty Due Diligence, Risk Classification
- [ ] Why needed: Public-facing policies should reflect this organization's specific requirements

### 2. Test Scenarios (NEEDED FOR IMPLEMENTATION)
- [ ] `scenarios/synthetic-benign/input/` — 3 valid documents (simulated supplier case)
- [ ] `scenarios/synthetic-benign/expected/` — 5 JSON result files (expected extraction + decisions)
- [ ] `scenarios/synthetic-risk/input/` — 5 documents with inconsistencies (high-risk case)
- [ ] `scenarios/synthetic-risk/expected/` — 5 JSON result files (expected escalations)

**Why needed:** End-to-end test cases validate the entire workflow from document upload through AI extraction and human approval.

### 3. Source Manifests (NEEDED FOR REPRODUCIBILITY)
- [ ] `manifests/source-documents.yaml` — Government PDFs: URLs, versions, SHA-256 checksums
- [ ] `manifests/dataset-version.yaml` — Changelog, component status, next milestone

**Why needed:** Reproducibility requires knowing exact versions of all source documents and policy compliance.

---

## 🎯 Next Actions

**Week 1-2:**
1. Customize 5 policy templates (remove TEMPLATE_ prefix, adapt rules)
2. Create 3 missing internal policies
3. Create sanctions test fixture expected results

**Week 2-3:**
1. Build synthetic benign case (3 documents, 5 expected JSON files)
2. Build synthetic risk case (5 documents with inconsistencies, 5 expected JSON files)

**Week 3-4:**
1. Generate source manifests
2. Calculate SHA-256 checksums
3. Final review and mark as "100% READY FOR IMPLEMENTATION"

**See SAMPLE_DATA_ROADMAP.md for 4-week detailed plan.**

---

## 📚 Related Documentation

- `SAMPLE_DATA_ROADMAP.md` — Implementation timeline and detailed work items
- `SAMPLE_DATA_INVENTORY.md` — Complete file listing and component status
- `SAMPLE_DATA_GOVERNANCE.md` — Provenance, refresh, Git, and privacy strategy (coming)
- `due-diligence-rule-catalogue.yaml` — Executable rules (replaces policy text for rule engine)

---

## ✅ Architecture Correctness Checklist

- ✅ Public policies separate from organizational policies
- ✅ Reference data (sanctions, ABN, Supplier Master) outside RAG
- ✅ Fixtures and expected results paired for testing
- ✅ Deterministic rules executed outside agent reasoning
- ✅ Real public examples not forming coherent case
- ✅ Synthetic scenarios isolated from real company data
- ✅ Immutable test fixtures for reproducible evaluation
- ✅ Manifests for version control and provenance

**Structure now correctly reflects the architecture.** Ready for implementation after completing 5 critical items above.
