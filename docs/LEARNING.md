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

- [x] Folder structure and GitHub repo created
- [x] Docker Compose for local PostgreSQL + pgvector
- [x] Base pom.xml and backend module
- [x] Spring Boot application.yml template

## In Progress

- Case API and database schema

## Next

- Implement first vertical slice: case creation, document upload, basic rules
