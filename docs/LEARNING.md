# Learning Log: Supplier Due Diligence AI

## Release 1 Scope and Build Order

This document captures learning objectives, implementation order, and key decisions for Release 1.

### Vertical slices (in order)

1. **Case API, database, document upload**
   - REST endpoints for case creation
   - PostgreSQL schema with pgvector extension
   - File upload and storage
   - Structured case state model

2. **Deterministic workflow and rules**
   - Rule engine for due-diligence checks
   - Case progression gates
   - Evidence requirements

3. **Document Evidence Agent**
   - Document classification
   - Field extraction with confidence
   - Page-level evidence grounding

4. **ABN Lookup and Supplier Master tools**
   - ABN validation via public API
   - Supplier Master search and mock data
   - Tool error handling and retry logic

5. **MCP server and governance layer**
   - Bounded tool definitions
   - Authentication and authorisation
   - Case-scoped access control
   - Audit logging

6. **Policy RAG and retrieval**
   - Policy ingestion and chunking
   - pgvector storage and embedding
   - Hybrid retrieval with filters
   - Citation tracking

7. **Entity Resolution Agent**
   - Name matching and comparison
   - Duplicate detection
   - Discrepancy reporting

8. **Policy and Risk Agent**
   - Policy question formulation
   - RAG retrieval with metadata filters
   - Obligation mapping
   - Gap identification

9. **Review Synthesis Agent**
   - Case consolidation
   - Review package generation
   - Recommendation formulation

10. **Events, retries, human review hand-off**
    - Asynchronous processing
    - Failure recovery
    - Human decision recording

### Learning goals

- Spring Boot 3 patterns: dependency injection, JPA, REST, testing
- Spring AI: model integration, tool calling, RAG abstractions
- Bounded agents: typed inputs/outputs, specialisation, orchestration
- Deterministic rules: decision tables, gates, evidence requirements
- PostgreSQL and pgvector: schema design, embeddings, hybrid search
- MCP: standardised tool definition, governed access, audit
- LLM tool use: safe schema validation, error handling, result interpretation
- Agentic loops: investigation within limits, retry strategies, state persistence
- Evidence grounding: citation tracking, source verification, confidence assessment

### Key decision checkpoints

- **Case state model**: Ensure all agent results and decisions are captured in typed structures
- **Tool boundaries**: Keep agents read-only; no autonomous writes to source systems
- **Rule authority**: Confirm rule results cannot be overridden by LLM reasoning
- **Human gates**: Verify human decisions are protected from agent writes
- **RAG scope**: Ensure policy corpus is separate from supplier-specific evidence
- **Citation accuracy**: Validate that every finding references a document, tool, policy or rule

## Completed

### Foundation (Phases 1-4) ✅
- [x] Phase 1: Case API & Persistence (5 tests ✓)
  - REST endpoints for case creation, retrieval, status updates
  - PostgreSQL schema with V1 migration
  - Single UUID ID design (simplified from dual ID pattern)
  - Project Lombok for boilerplate reduction
  - H2 in-memory database for testing
  
- [x] Phase 2: Document Upload & Storage (6 tests ✓)
  - Multipart file handling
  - One-to-many case ↔ documents relationship
  - File validation (PDF only, max 10MB)
  - REST endpoints for upload, list, delete
  - Database migration V2 with CASCADE DELETE
  
- [x] Phase 3: Deterministic Rules & Workflow (9 tests ✓)
  - 8 rule types (SANCTION_CHECK, ABN_VALIDATION, etc.)
  - 4 severity levels (CRITICAL, HIGH, MEDIUM, LOW)
  - Rule evaluation engine with evidence tracking
  - Case status workflow (10 states)
  - Database migration V3 for rules and results
  
- [x] Phase 4: ABN Lookup & Tool Integration (6 tests ✓)
  - ABNLookupService with HTTP client
  - SupplierVerificationService with fuzzy name matching (Levenshtein distance)
  - ToolResult immutable audit entity
  - Tool calling pattern established
  - REST endpoints for verification and result queries
  - Database migration V4 with performance indices
  - Configuration with timeout/retry settings

- [x] Support Infrastructure
  - Docker Compose for PostgreSQL + Docker for local dev
  - Base pom.xml with Spring Boot 3.3.0 and Java 20
  - Spring Boot application.yml with all configurations
  - Comprehensive test suite (26/26 passing)
  - Flyway database migration versioning (V1-V4)

## In Progress

- Phase 5: Document Evidence Agent (Spring AI integration for PDF extraction)

## Next

1. Phase 5: Document Evidence Agent (5-7 days)
   - Spring AI ChatClient integration
   - PDF text extraction and parsing
   - LLM tool calling for fact extraction
   - Evidence grounding with page references

2. Phase 6: MCP Server & Governance (4 days)
   - Model Context Protocol server
   - Tool authorization and access control
   - Audit logging

3. Phases 7-10: RAG, Entity Resolution, Policy Assessment, Orchestration
