---
sidebar_position: 4
---

# Database Schema & Migrations

How PostgreSQL schema is managed via Flyway.

## Schema Overview

```sql
-- Core tables
cases                  -- Supplier cases
documents             -- Uploaded PDFs
page_chunks           -- Extracted text chunks
rule_results          -- Rule evaluation outcomes
extraction_results    -- LLM extraction results
tool_results          -- External tool results

-- Relationships
cases.id ← documents.case_id
cases.id ← rule_results.case_id
cases.id ← extraction_results.case_id
documents.id ← extraction_results.document_id
documents.id ← page_chunks.document_id
```

---

## Migration Files

### V1__create_cases.sql

```sql
CREATE TABLE cases (
    id UUID PRIMARY KEY,
    business_name VARCHAR(255) NOT NULL,
    requested_by VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'INTAKE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_cases_status ON cases(status);
```

### V2__create_documents.sql

```sql
CREATE TABLE documents (
    id UUID PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50),
    file_size BIGINT,
    uploaded_by VARCHAR(255),
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_documents_case_id ON documents(case_id);
```

### V3__create_rules.sql

```sql
CREATE TABLE rule_results (
    id UUID PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    rule_type VARCHAR(50) NOT NULL,
    outcome VARCHAR(50) NOT NULL,
    explanation TEXT,
    evaluated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rule_results_case_id ON rule_results(case_id);
CREATE INDEX idx_rule_results_outcome ON rule_results(outcome);
```

### V4__create_tools.sql

```sql
CREATE TABLE tool_results (
    id UUID PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    tool_name VARCHAR(100) NOT NULL,
    tool_type VARCHAR(100),
    input_data TEXT,
    output_data TEXT,
    success BOOLEAN,
    error_message TEXT,
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tool_results_case_id ON tool_results(case_id);
```

### V5__create_extractions.sql

```sql
CREATE TABLE extraction_results (
    id UUID PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    document_id UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    success BOOLEAN,
    model_used VARCHAR(100),
    tokens_used INTEGER,
    evidence TEXT,
    error_message TEXT,
    extracted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_extractions_case_id ON extraction_results(case_id);
CREATE INDEX idx_extractions_document_id ON extraction_results(document_id);
```

---

## Entity-Relationship Diagram

```
┌─────────────────┐
│     cases       │
├─────────────────┤
│ id (PK)         │
│ business_name   │
│ status          │
│ created_at      │
└────────┬────────┘
         │
         ├─────────────────────┬────────────────┐
         │                     │                │
         ↓                     ↓                ↓
    ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
    │  documents   │   │ rule_results │   │ tool_results │
    ├──────────────┤   ├──────────────┤   ├──────────────┤
    │ id (PK)      │   │ id (PK)      │   │ id (PK)      │
    │ case_id (FK) │   │ case_id (FK) │   │ case_id (FK) │
    │ file_name    │   │ rule_type    │   │ tool_name    │
    │ uploaded_at  │   │ outcome      │   │ success      │
    └──────┬───────┘   │ evaluated_at │   └──────────────┘
           │           └──────────────┘
           │
           ├──────────────────┬────────────────┐
           │                  │                │
           ↓                  ↓                ↓
    ┌──────────────┐ ┌──────────────────┐
    │ page_chunks  │ │extraction_results│
    ├──────────────┤ ├──────────────────┤
    │ document_id  │ │ id (PK)          │
    │ page_number  │ │ case_id (FK)     │
    │ text         │ │ document_id (FK) │
    │ start_pos    │ │ model_used       │
    │ end_pos      │ │ tokens_used      │
    └──────────────┘ │ extracted_at     │
                     └──────────────────┘
```

---

## Indexes

### Primary Key Indexes (Auto-created)

```sql
cases(id)
documents(id)
rule_results(id)
tool_results(id)
extraction_results(id)
```

### Foreign Key Indexes

```sql
documents(case_id)
rule_results(case_id)
tool_results(case_id)
extraction_results(case_id, document_id)
```

### Performance Indexes

```sql
cases(status)                    -- Fast filtering by case status
rule_results(outcome)            -- Fast querying by result
```

---

## Query Examples

### Find all documents for a case

```sql
SELECT d.* FROM documents d
WHERE d.case_id = 'case-uuid'
ORDER BY d.uploaded_at DESC;
```

### Get rule evaluation summary

```sql
SELECT 
    rule_type,
    COUNT(*) as count,
    COUNT(CASE WHEN outcome = 'PASS' THEN 1 END) as passed,
    COUNT(CASE WHEN outcome = 'FAIL' THEN 1 END) as failed
FROM rule_results
WHERE case_id = 'case-uuid'
GROUP BY rule_type;
```

### Find extraction results with tokens used

```sql
SELECT 
    er.id,
    er.document_id,
    er.model_used,
    er.tokens_used,
    er.success
FROM extraction_results er
WHERE er.case_id = 'case-uuid'
ORDER BY er.extracted_at DESC;
```

### Case ownership enforcement (composite key)

```sql
-- Get document only if it belongs to this case
SELECT d.* FROM documents d
WHERE d.id = 'doc-uuid' 
  AND d.case_id = 'case-uuid';
```

---

## Data Integrity

### Cascading Deletes

```sql
-- If case is deleted, all related data is deleted
documents(case_id) REFERENCES cases(id) ON DELETE CASCADE
rule_results(case_id) REFERENCES cases(id) ON DELETE CASCADE
extraction_results(case_id) REFERENCES cases(id) ON DELETE CASCADE
```

### Foreign Key Constraints

```sql
-- Can't insert document without valid case
documents(case_id) REFERENCES cases(id)

-- Can't insert rule result without valid case
rule_results(case_id) REFERENCES cases(id)
```

### Immutability

Fields that should never change:
- `cases.created_at`
- `documents.uploaded_at`
- `rule_results.evaluated_at`
- Results are append-only (no UPDATE statements)

---

## Flyway Management

### How Flyway Works

1. **On startup:** Check `flyway_schema_history` table
2. **Detect:** Which migrations have run
3. **Execute:** Any new migrations (V*.sql files)
4. **Record:** Success in history table

### Migration Files Location

```
src/main/resources/db/migration/
├── V1__create_cases.sql
├── V2__create_documents.sql
├── V3__create_rules.sql
├── V4__create_tools.sql
└── V5__create_extractions.sql
```

### Configuration (application.yml)

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baselineOnMigrate: true
```

---

## Testing Database (H2)

### In-Memory H2 for Tests

```java
// application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop  // Recreate schema each test
```

### Benefits

- ✅ Fast (in-memory, no network)
- ✅ Isolated (fresh DB per test)
- ✅ No Docker required
- ✅ Tests run in parallel

### Flyway still runs

```
Flyway detects H2 database
Runs V1, V2, V3, V4, V5 migrations
Creates schema fresh for each test
Tests execute
Schema dropped after test
```

---

## Production Considerations

### Backup Strategy

```bash
# PostgreSQL backup
pg_dump supplier_dd_db > backup-$(date +%Y%m%d).sql

# Restore
psql supplier_dd_db < backup-20260927.sql
```

### Connection Pooling (HikariCP)

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      connection-timeout: 30000
      idle-timeout: 600000
```

### Replication & HA

For production:
- PostgreSQL streaming replication
- Hot standby
- Automated failover

### Monitoring

```sql
-- Monitor table sizes
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

---

## Future Enhancements

### Partitioning

For very large tables (millions of rows):
```sql
-- Partition extraction_results by month
CREATE TABLE extraction_results_2026_09 PARTITION OF extraction_results
    FOR VALUES FROM ('2026-09-01') TO ('2026-10-01');
```

### Archiving

Old cases can be archived:
```sql
-- Archive cases older than 1 year
INSERT INTO cases_archive 
SELECT * FROM cases WHERE updated_at < NOW() - INTERVAL '1 year';
DELETE FROM cases WHERE updated_at < NOW() - INTERVAL '1 year';
```

### Vector Storage (Phase 7)

For RAG, add pgvector extension:
```sql
CREATE EXTENSION IF NOT EXISTS vector;

ALTER TABLE page_chunks ADD COLUMN embedding vector(1536);
CREATE INDEX ON page_chunks USING ivfflat (embedding vector_cosine_ops);
```

---

## Troubleshooting

### Flyway Migration Failed

```
Error: Flyway migration V3__create_rules.sql failed

Check:
1. Are you on the latest code?
2. Did migration run partially before?
3. Is the SQL syntax correct?

Fix:
1. Check Flyway version compatibility
2. Review error message for SQL syntax
3. Consider rolling back and re-running
```

### Cascading Delete Warning

```
Be careful: Deleting a case deletes ALL documents and results

Use with caution in production
Add soft-deletes (is_deleted flag) instead
```

---

## Next Steps

- **Testing strategy:** See [Testing](./testing.md)
- **Quick start:** Read [Quick Start](../getting-started/quickstart.md)
- **Models reference:** Check [Models](../models.md)
