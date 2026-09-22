# Australian Supplier Due-Diligence AI

A learning-first implementation of an enterprise-grade AI system for Australian supplier onboarding and counterparty due-diligence.

## Phase 1: Case API & Persistence ✓

The foundation is now complete. You can create, retrieve, and manage supplier cases via REST API.

### What's Implemented

- **Spring Boot Application:** RESTful API for case management
- **PostgreSQL Persistence:** Case entity with status workflow
- **Flyway Migrations:** Database versioning and initialization
- **Testcontainers Integration:** Isolated integration testing with real PostgreSQL
- **Unit & Integration Tests:** Comprehensive test coverage

### Quick Start

#### Prerequisites
- Java 20
- Maven 3.9+
- Docker & Docker Compose

#### 1. Start PostgreSQL
```bash
docker compose up -d
```

#### 2. Build Backend
```bash
cd backend
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

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "caseId": "SUP-1234567890",
  "supplierName": "Acme Corporation",
  "status": "SUBMITTED",
  "requestedBy": "analyst@company.com",
  "createdAt": "2026-09-22T20:47:00"
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
cd backend
mvn test
```

Tests use Testcontainers to spin up isolated PostgreSQL instances.

### Architecture

```
backend/
  src/
    main/
      java/com/supplier/diligence/
        casework/           # Case management
          CaseController.java
          CaseService.java
          CaseRepository.java
          DueDiligenceCase.java
          CaseStatus.java
        config/             # Spring config
        SupplierDueDiligenceApplication.java
      resources/
        application.yml     # App config
        db/migration/       # Flyway SQL migrations
    test/
      java/com/supplier/diligence/
        casework/           # Tests
  pom.xml
```

### What's Next (Phase 2)

- Document upload and storage
- File type validation
- Persisting case documents

### Notes

- **No AI yet.** Phase 1 focuses on REST/persistence patterns and the case model foundation.
- **Tests are comprehensive.** Run `mvn test` to verify everything works.
- **Database is PostgreSQL.** Local dev uses Docker; later phases will use Azure PostgreSQL.
- **Flyway manages migrations.** Each schema change is versioned and tracked.

---

See [docs/Australian_Supplier_Due_Diligence_Agentic_AI.md](docs/Australian_Supplier_Due_Diligence_Agentic_AI.md) for the full project context and architecture decisions.

See [docs/Project Context.md](docs/Project%20Context.md) for detailed scope and business context.
