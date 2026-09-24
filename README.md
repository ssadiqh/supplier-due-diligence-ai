# Australian Supplier Due-Diligence AI

A learning-first implementation of an enterprise-grade AI system for Australian supplier onboarding and counterparty due-diligence.

## Phase 1-4: Case API, Documents, Rules & Tool Integration ✓

Foundation complete. Create cases, upload documents, evaluate business rules, verify suppliers via ABN lookup.

### What's Implemented

- **Spring Boot Application:** RESTful API for case, document, and rule management
- **PostgreSQL Persistence:** Cases with document and rule result relationships
- **Project Lombok:** Reduced boilerplate (getters, setters, constructors auto-generated)
- **Flyway Migrations:** Database versioning (V1 cases, V2 documents, V3 rules)
- **Deterministic Rules Engine:** Business logic evaluation without AI
- **H2 In-Memory Testing:** Fast, isolated integration tests (no Docker required)
- **Unit & Integration Tests:** 26/26 tests passing (3 Phase 1 + 3 Phase 2 + 4 Phase 3 + 6 Phase 4, 8 integration tests across phases)

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

### What's Next (Phase 5)

- Document evidence extraction using Spring AI
- PDF text parsing and structured fact extraction
- LLM tool calling and prompt design
- Evidence grounding with page references

### Notes

- **No LLM AI yet.** Phases 1-4 focus on deterministic logic, API patterns, and tool calling infrastructure.
- **Tests are comprehensive.** Run `mvn test` to verify everything works (26/26 passing).
- **Database is PostgreSQL.** Local dev uses Docker; H2 for testing.
- **Flyway manages migrations.** Each schema change is versioned and tracked (V1-V4).
- **Tool calling ready.** Phase 4 demonstrates external API integration pattern for future agents.

---

See [docs/Australian_Supplier_Due_Diligence_Agentic_AI.md](docs/Australian_Supplier_Due_Diligence_Agentic_AI.md) for the full project context and architecture decisions.

See [docs/Project Context.md](docs/Project%20Context.md) for detailed scope and business context.
