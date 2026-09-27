# Australian Supplier Due-Diligence AI

A learning-first implementation of an enterprise-grade AI system for Australian supplier onboarding and counterparty due-diligence.

## Phases 1-4: Complete | Phase 5: Foundation In Progress

**Phases 1-4 complete.** Create cases, upload documents, evaluate business rules, verify suppliers via ABN lookup.

**Phase 5 (Evidence Extraction) foundation implemented.** PDF text parsing and chunking working; LLM-based structured fact extraction in progress (awaits ChatClient integration).

### What's Implemented

- **Spring Boot Application:** RESTful API for case, document, rule, and tool management
- **PostgreSQL Persistence:** Cases with document, rule result, and tool result relationships
- **Project Lombok:** Reduced boilerplate (getters, setters, constructors auto-generated)
- **Flyway Migrations:** Database versioning (V1-V4 schemas)
- **Deterministic Rules Engine:** 8 rule types with 6-state RuleOutcome (PASS/FAIL/ERROR/NOT_EVALUATED/NOT_APPLICABLE/UNAVAILABLE)
- **Document Parsing:** PDF text extraction with configurable chunking (500-char chunks, 50-char overlap)
- **Filename Sanitization:** Path traversal protection for uploaded files
- **File Streaming:** Memory-efficient upload handling (Files.copy, not getBytes())
- **Case Ownership Enforcement:** Document access/deletion verified against case ownership
- **ABN Validation:** ISO/IEC 7064 mod 10-13 checksum validation
- **H2 In-Memory Testing:** Fast, isolated integration tests (no Docker required)
- **Unit & Integration Tests:** 47/47 tests passing (all test suites)

### Quick Start

#### Prerequisites
- Java 20
- Maven 3.9+
- Docker & Docker Compose

#### 1. Start PostgreSQL
```bash
docker compose up -d
```

#### 2. Build Casework Service
```bash
cd casework-service
mvn clean package
```

#### 3. Run Application
```bash
mvn spring-boot:run
```

The API runs on `http://localhost:8080`.

### API Endpoints

#### Create Case
```bash
curl -X POST http://localhost:8080/api/cases \
  -H "Content-Type: application/json" \
  -d '{
    "supplierName": "Acme Corporation",
    "requestedBy": "analyst@company.com"
  }'
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "supplierName": "Acme Corporation",
  "status": "SUBMITTED",
  "requestedBy": "analyst@company.com",
  "supplierAbn": null,
  "supplierLegalName": null,
  "createdAt": "2026-09-22T20:47:00",
  "updatedAt": null
}
```

#### Get All Cases
```bash
curl http://localhost:8080/api/cases
```

#### Get Case by ID
```bash
curl http://localhost:8080/api/cases/{id}
```

#### Update Case Status
```bash
curl -X PATCH "http://localhost:8080/api/cases/{id}/status?status=DOCUMENT_UPLOADED"
```

#### Upload Document
```bash
curl -X POST http://localhost:8080/api/cases/{caseId}/documents \
  -F "file=@evidence.pdf" \
  -F "uploadedBy=analyst@company.com"
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "fileName": "evidence.pdf",
  "fileType": "application/pdf",
  "fileSize": 245632,
  "uploadedAt": "2026-09-22T22:15:00",
  "uploadedBy": "analyst@company.com"
}
```

#### Get Documents for Case
```bash
curl http://localhost:8080/api/cases/{caseId}/documents
```

### Case Status Workflow

```
SUBMITTED
  ↓
DOCUMENT_UPLOADED
  ↓
EVIDENCE_EXTRACTED
  ↓
ENTITY_VERIFIED
  ↓
SANCTIONS_SCREENED
  ↓
RULES_EVALUATED
  ↓
POLICY_ASSESSED
  ↓
REVIEW_READY
  ↓
HUMAN_DECISION_PENDING
  ↓
COMPLETED
```

### Running Tests

```bash
cd casework-service
mvn test
```

Tests use H2 in-memory database for speed and isolation (no Docker required).

### Architecture

```
casework-service/
  src/
    main/
      java/com/diligence/
        casework/           # Case management (Phase 1)
          CaseController.java
          CaseService.java
          CaseRepository.java
          DueDiligenceCase.java
          CaseStatus.java
        documents/          # Document upload & storage (Phase 2)
          DocumentController.java
          DocumentService.java
          DocumentRepository.java
          Document.java
        rules/              # Deterministic rules engine (Phase 3)
          RuleController.java
          RuleService.java
          RuleRepository.java / RuleResultRepository.java
          Rule.java / RuleResult.java
          RuleType.java / RuleSeverity.java
        tools/              # External tool integration (Phase 4)
          ToolController.java
          SupplierVerificationService.java
          ABNLookupService.java
          ToolResult.java / ToolResultRepository.java
        config/             # Spring configuration
          RestTemplateConfig.java
        SupplierDueDiligenceApplication.java
      resources/
        application.yml     # App config
        db/migration/       # Flyway SQL migrations
    test/
      java/com/diligence/
        casework/           # Case tests
        documents/          # Document tests
        rules/              # Rule tests
  pom.xml
```

### What's Next (Phase 5 - LLM Integration)

**Checkpoint:** Evidence extraction foundation ready. PDF parsing, chunking, and rule validation all working.

Remaining work:
- Wire Spring AI ChatClient for LLM-based fact extraction
- Implement structured output deserialization for extracted entities
- Add evidence grounding with page references and confidence scores
- Integration with document evidence agent for end-to-end flow

### Notes

- **Foundation first.** Phases 1-5 build deterministic infrastructure before agents use LLM.
- **Tests are comprehensive.** Run `mvn test` to verify everything works (47/47 passing).
- **Database is PostgreSQL.** Local dev uses Docker; H2 for testing.
- **Flyway manages migrations.** Each schema change is versioned and tracked (V1-V4).
- **Tool calling ready.** Phase 4 demonstrates external API integration pattern for future agents.
- **PDF handling optimized.** Streaming, chunking, and path sanitization built in Phase 5.

---

## 📚 Full Documentation

Comprehensive documentation is available in the **Docusaurus site** (interactive HTML):

```bash
npm install
npm run start
```

Then open http://localhost:3000

**Documentation covers:**
- [Getting Started](./docs/getting-started/quickstart.md) - Setup & run the app
- [Architecture](./docs/architecture.md) - System design with diagrams
- [Models](./docs/models.md) - Data structures and enums
- [Phases](./docs/phases.md) - Roadmap and status
- **Deep Dives:**
  - [PDF Parsing](./docs/deep-dives/pdf-parsing.md) - Chunking algorithm
  - [Rules Evaluation](./docs/deep-dives/rules-evaluation.md) - Business rules
  - [LLM Integration](./docs/deep-dives/llm-integration.md) - Claude API setup
  - [Database](./docs/deep-dives/database.md) - Schema & migrations
  - [Testing](./docs/deep-dives/testing.md) - Test strategy

See [docs/Australian_Supplier_Due_Diligence_Agentic_AI.md](docs/Australian_Supplier_Due_Diligence_Agentic_AI.md) for project context and decisions.
