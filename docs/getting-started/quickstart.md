---
sidebar_position: 1
---

# Quick Start Guide

Get the application running locally in 10 minutes.

## Prerequisites

- **Java 20+** ([download](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.9+** ([download](https://maven.apache.org/))
- **PostgreSQL 15+** ([download](https://www.postgresql.org/)) OR Docker
- **Node.js 16+** (for documentation site, optional)

## 1. Database Setup

### Option A: Docker (Recommended)

```bash
docker run -d \
  --name supplier-db \
  -e POSTGRES_DB=supplier_dd_db \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15
```

### Option B: Local PostgreSQL

```bash
createdb -U postgres supplier_dd_db
```

**Verify connection:**
```bash
psql -U postgres -d supplier_dd_db -c "SELECT version();"
```

## 2. Clone & Build

```bash
cd casework-service
mvn clean package -DskipTests
```

This builds the JAR and runs schema migrations automatically (Flyway).

## 3. Run the Application

```bash
mvn spring-boot:run
```

**Expected output:**
```
Started SupplierDueDiligenceApplication in 8.234 seconds
Tomcat initialized with port 8080 (http)
```

Application is now live at: **http://localhost:8080**

## 4. Test the API

### Create a Case
```bash
curl -X POST http://localhost:8080/api/cases \
  -H "Content-Type: application/json" \
  -d '{
    "businessName": "Acme Corporation",
    "requestedBy": "analyst@company.com"
  }'
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "businessName": "Acme Corporation",
  "status": "INTAKE",
  "requestedBy": "analyst@company.com",
  "createdAt": "2026-09-27T12:00:00"
}
```

Save the `id` for next steps.

### Upload a Document
```bash
curl -X POST http://localhost:8080/api/cases/{CASE_ID}/documents \
  -F "file=@sample-document.pdf" \
  -F "uploadedBy=analyst@company.com"
```

### Get Case Status
```bash
curl http://localhost:8080/api/cases/{CASE_ID}
```

### Evaluate Rules
```bash
curl -X POST http://localhost:8080/api/cases/{CASE_ID}/rule-evaluations
```

## 5. Run Tests

```bash
cd casework-service
mvn test
```

**Expected:** All 50 tests pass ✅

Tests use H2 in-memory database (no external dependencies needed).

## 6. View Documentation (Optional)

Build and serve the Docusaurus documentation site:

```bash
npm install
npm run build
npm run serve
```

Documentation will be available at: **http://localhost:3000**

## 🔐 Using Claude API (Optional)

To enable LLM-based evidence extraction with Claude:

1. **Get API key** from [console.anthropic.com](https://console.anthropic.com/account/keys)

2. **Add to `application.yml`:**
   ```yaml
   spring:
     ai:
       anthropic:
         api-key: sk-ant-api03-...
         model: claude-3-5-sonnet-20241022
   ```

3. **Restart the app:**
   ```bash
   mvn spring-boot:run
   ```

4. **Extract evidence:**
   ```bash
   curl http://localhost:8080/api/cases/{CASE_ID}/extractions/{DOCUMENT_ID}
   ```

## Troubleshooting

### "Connection to localhost:5432 refused"
→ Ensure PostgreSQL is running: `docker ps` or `psql -U postgres`

### "No tests to run"
→ Make sure you're in the `casework-service` directory: `cd casework-service && mvn test`

### "Port 8080 already in use"
→ Kill the process: `lsof -ti:8080 | xargs kill -9`

### "ClassNotFoundException: org.postgresql.Driver"
→ Run `mvn clean package` to download dependencies

## Next Steps

- **Learn the architecture:** Read [Architecture](../architecture.md)
- **Explore the API:** See [API Overview](./api-overview.md)
- **Understand the code:** Pick a [Deep Dive](../deep-dives/pdf-parsing.md)

---

**Need help?** Check [Getting Started troubleshooting](#troubleshooting) or review the [Phase Status](../phases.md).
