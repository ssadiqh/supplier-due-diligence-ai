---
sidebar_position: 1
slug: /
---

# Supplier Due-Diligence AI

Welcome to the documentation for **Supplier Due-Diligence AI** — an enterprise-grade system for Australian supplier onboarding and counterparty due diligence.

## 🎯 What This System Does

This is a **case-based workflow application** that:

- **Extracts evidence** from supplier documents using PDF parsing and Claude AI
- **Verifies suppliers** against the Australian Business Register (ABN)
- **Evaluates rules** deterministically (sanctions, financial thresholds, industry restrictions)
- **Screens for risk** using supplier verification and compliance checks
- **Prepares review packages** for human decision-makers

**AI investigates and recommends. Humans retain control and make final decisions.**

## 📋 Who Should Read This

- **Developers**: Need to understand architecture, extend functionality, run tests
- **Stakeholders**: Want to know capabilities, current status, roadmap
- **Operators**: Need to set up, run, and troubleshoot the system

## 🚀 Quick Navigation

**New to the system?**
→ Start with [Quick Start](./getting-started/quickstart.md)

**Want to understand the design?**
→ Read [Architecture](./architecture.md)

**Need API details?**
→ See [API Overview](./getting-started/api-overview.md)

**Curious about how it works?**
→ Explore the [Deep Dives](./deep-dives/pdf-parsing.md)

## 📊 Phase Status

| Phase | Feature | Status |
|-------|---------|--------|
| **1** | Case management API | ✅ Complete |
| **2** | Document upload & storage | ✅ Complete |
| **3** | Deterministic rules engine | ✅ Complete |
| **4** | ABN lookup & supplier verification | ✅ Complete |
| **5** | PDF parsing & evidence extraction | 🔄 In Progress |
| **6** | LLM-based fact extraction | 🔄 In Progress |
| **7** | RAG & policy retrieval | ⏳ Planned |
| **8** | Agentic investigation | ⏳ Planned |
| **9** | Multi-agent orchestration | ⏳ Planned |
| **10** | Human-in-the-loop review UI | ⏳ Planned |

**Current:** Phase 5 foundation complete. PDF parsing working. Spring AI 2.0.1 + Claude integration ready.

## 🏗️ Technology Stack

```
┌─────────────────────────────────────────────────┐
│         Spring Boot 3.3.0 REST API              │
│         (Case, Document, Rule, Tool mgmt)       │
├─────────────────────────────────────────────────┤
│  Spring AI 2.0.1          │  PostgreSQL 15      │
│  (Claude API)             │  (Persistence)      │
├─────────────────────────────────────────────────┤
│  Apache PDFBox 3.0        │  Flyway Migrations  │
│  (PDF parsing, chunking)  │  (Schema versioning)│
└─────────────────────────────────────────────────┘
```

## 📈 Key Features

✅ **Case Management** - Track supplier cases through status workflow
✅ **Document Handling** - Upload PDFs with security (filename sanitization, streaming)
✅ **PDF Parsing** - Extract text with intelligent chunking (500 chars + 50-char overlap)
✅ **Rule Engine** - 6-outcome model (PASS/FAIL/ERROR/NOT_EVALUATED/NOT_APPLICABLE/UNAVAILABLE)
✅ **ABN Validation** - ISO/IEC 7064 mod 10-13 checksum verification
✅ **Supplier Verification** - Levenshtein distance matching with ABN lookup
✅ **LLM Integration** - Spring AI 2.0.1 with Claude 3.5 Sonnet
✅ **Comprehensive Tests** - 50+ tests, H2 in-memory database for speed

## 🔐 Design Principles

1. **No AI in critical decisions** - Rules are deterministic, LLM is advisory
2. **Evidence grounding** - Every finding cites its source
3. **Human authority** - Agents investigate, humans decide
4. **Immutable audit trails** - All results are permanent records
5. **Read-only agents** - No autonomous writes to source systems

## 📚 Documentation Map

```
Documentation/
├── Getting Started/
│   ├── Quick Start (setup & run)
│   └── API Overview (endpoints & flows)
├── Core Concepts/
│   ├── Architecture (system design)
│   ├── Models (data structures)
│   └── Phases (roadmap & status)
└── Deep Dives/
    ├── PDF Parsing (chunking algorithm)
    ├── Rules Evaluation (rule engine)
    ├── LLM Integration (Spring AI setup)
    ├── Database (schema & migrations)
    └── Testing (test strategy)
```

---

**Ready to get started?** → [Quick Start Guide](./getting-started/quickstart.md)
