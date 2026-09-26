# Sample Data Directory

**Status:** 🟡 75% COMPLETE → 85% AFTER REORGANIZATION

Public source corpus assembled. Infrastructure (fixtures, expected results, manifests) now in place. Still need: complete internal policies, test scenarios, expected results for scenarios.

---

## 📁 Current Final Structure

```
sample-data/
├── policies/                    Authority hierarchy for governance
│   ├── public/                  Australian government guidance (6 PDFs)
│   └── internal/                Organizational rules (5 templates + 3 missing)
│
├── reference-data/              Deterministic screening data (NOT RAG)
│   ├── sanctions/
│   │   ├── raw/                 DFAT Consolidated List (monthly refresh)
│   │   ├── fixtures/            Stable test cases (exact, partial, false-positive)
│   │   └── expected/            Known-good screening results (JSON)
│   ├── abn-lookup/
│   │   ├── fixtures/            API test responses (active, cancelled, error)
│   │   └── expected/            Expected validation results (JSON)
│   └── supplier-master/
│       ├── fixtures/            Test records (duplicate, variant, inactive)
│       └── expected/            Entity resolution results (JSON)
│
├── rules/                       Executable rule definitions
│   └── due-diligence-rule-catalogue.yaml
│
├── document-templates/          Empty templates for assessment forms
│   └── modern-slavery-supplier-questionnaire.docx
│
├── public-examples/             Real public documents for testing
│   ├── asic/                    Official ASIC extracts
│   └── modern-slavery/          Real Modern Slavery Statement
│
├── scenarios/                   Complete end-to-end test cases
│   ├── synthetic-benign/        Low-risk scenario (inputs + expected)
│   ├── synthetic-risk/          High-risk scenario (inputs + expected)
│   └── adversarial/             Security tests (OCR, injection, corruption)
│
├── manifests/                   Version control and provenance
│   ├── source-documents.yaml    URLs, versions, SHA-256
│   └── dataset-version.yaml     Changelog and status
│
└── evaluation/                  Test cases for agent evaluation
    └── rag/                     RAG retrieval test queries and metrics
```

---

## ✅ What's Ready Now

### Governance Infrastructure
- ✅ **policies/public/** — 6 government policy documents (ready for RAG embedding)
- ✅ **policies/internal/** — 5 policy templates (need customization)
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
| Internal policies (3 missing) | 📝 5/8 | Supplier Onboarding, Modern Slavery Assessment, Human Approval |
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

## 📋 Critical Missing Items (Blocking Implementation)

### 1. Internal Policies (CRITICAL)
- [ ] `policies/internal/supplier-onboarding-standard.md` — Workflow stages, gates, failure handling
- [ ] `policies/internal/modern-slavery-assessment-standard.md` — Triggers, statement acceptability, escalation
- [ ] `policies/internal/human-approval-and-override-policy.md` — Decision authorities, prohibited AI decisions

**Why blocking:** Agents cannot make decisions without organizational policy boundaries.

### 2. Test Scenarios (NEEDED FOR IMPLEMENTATION)
- [ ] `scenarios/synthetic-benign/input/` — 3 valid documents
- [ ] `scenarios/synthetic-benign/expected/` — 5 JSON result files
- [ ] `scenarios/synthetic-risk/input/` — 5 documents with inconsistencies
- [ ] `scenarios/synthetic-risk/expected/` — 5 JSON result files

**Why needed:** Without expected results, cannot objectively evaluate extraction and decision quality.

### 3. Source Manifests (NEEDED FOR REPRODUCIBILITY)
- [ ] `manifests/source-documents.yaml` — Government PDFs: URLs, versions, SHA-256 checksums
- [ ] `manifests/dataset-version.yaml` — Changelog, component status, next milestone

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
