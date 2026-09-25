# Supplier Due-Diligence AI: 10-Phase Implementation Plan

## Overview
Phased learning-first approach starting with REST API, progressing through tools, rules, RAG, and agents. Each phase is independent, testable, and demonstrates one capability. Target: ~9 weeks total; first working vertical slice in 4-5 weeks.

## Phase Breakdown

### Phase 1: Case API & Persistence (5 days) ✅ COMPLETE
- **Goal:** Create, persist and retrieve supplier cases via REST API
- **Tech:** Spring Boot, JPA, PostgreSQL, Flyway migrations
- **Deliverable:** POST/GET cases, case status workflow
- **Learning:** Spring REST patterns, database migrations, integration testing with Testcontainers
- **Status:** All 5 tests passing, committed to repository

### Phase 2: Document Upload & Storage (4 days) ✅ COMPLETE
- **Goal:** Upload and persist PDFs linked to cases
- **Tech:** Multipart file handling, file I/O, entity relationships
- **Deliverable:** Upload documents, validate file types, persist metadata
- **Learning:** Spring file handling, Bean Validation, error handling
- **Status:** 6 tests passing (3 unit, 3 integration), committed to repository

### Phase 3: Deterministic Rules & Workflow (4 days) ✅ COMPLETE
- **Goal:** Apply business rules; keep AI out of critical decisions
- **Tech:** Rule enums, case status machine, rule service
- **Deliverable:** Evaluate rules, prevent LLM override, track rule results
- **Learning:** Separation of concerns (AI vs deterministic), authority boundaries
- **Status:** 6 tests passing (4 unit, 6 integration), committed to repository

### Phase 4: ABN Lookup & First Tool (4 days) ✅ COMPLETE
- **Goal:** Integrate real external API (ABN Lookup), validate supplier identity
- **Tech:** HTTP client, API error handling, supplier verification
- **Deliverable:** ABN validation, name matching, mismatch detection
- **Learning:** External APIs, tool calling patterns, timeouts and retries
- **Status:** 6 tests passing (3 unit, 3 integration), committed to repository

### Phase 5: Document Evidence Agent (5-7 days)
- **Goal:** Use Spring AI to extract structured facts from PDFs
- **Tech:** PDF parsing, Spring AI ChatClient, tool calling, prompt versioning
- **Deliverable:** Extract supplier facts with page references and confidence
- **Learning:** LLM tool calling, evidence grounding, prompt design

**Checkpoint:** First working vertical slice complete. Can upload PDF → extract evidence → validate ABN → apply rules → review.

### Phase 6: MCP Server & Governance (4 days)
- **Goal:** Expose tools through Model Context Protocol; add access control
- **Tech:** MCP Java SDK, tool schemas, authorization, audit logging
- **Deliverable:** Case-scoped tools, access control, call audit trail
- **Learning:** MCP protocol, tool governance, read-only boundaries

### Phase 7: RAG Foundation & Policy Retrieval (5-6 days)
- **Goal:** Build RAG infrastructure; query policies for obligations
- **Tech:** pgvector, chunking, embeddings, Spring AI RAG, citations
- **Deliverable:** Ingest policies, retrieve via semantic search, track citations
- **Learning:** Vector search, RAG abstractions, metadata filtering, grounding

### Phase 8: Entity Resolution Agent (4 days)
- **Goal:** Compare supplier info from multiple sources; detect duplicates
- **Tech:** Agent orchestration, tool calling, comparison logic
- **Deliverable:** Find duplicates, reconcile discrepancies, recommend status
- **Learning:** Multi-tool agents, uncertainty handling, recommend-only patterns

### Phase 9: Policy & Risk Agent (3 days)
- **Goal:** Assess supplier against policies; identify gaps
- **Tech:** Agent + RAG, obligation mapping
- **Deliverable:** Policy compliance assessment with citations
- **Learning:** RAG in agent context, policy grounding

### Phase 10: Review Synthesis & Orchestration (4 days)
- **Goal:** Consolidate findings; implement full orchestration workflow
- **Tech:** Case orchestrator, review package, human decision protection
- **Deliverable:** End-to-end case processing, human review interface
- **Learning:** Workflow orchestration, human-in-the-loop patterns

## Timeline

| Phase | Duration | Cumulative | Key Milestone |
|-------|----------|-----------|---------------|
| 1 | 5 days | 1 week | ✅ API & DB working |
| 2 | 4 days | 2 weeks | Document upload working |
| 3 | 4 days | 2.5 weeks | Rules + workflow |
| 4 | 4 days | 3 weeks | ABN validation live |
| 5 | 5-7 days | 4-5 weeks | **First vertical slice** |
| 6 | 4 days | ~5 weeks | Governance & audit |
| 7 | 5-6 days | 6-7 weeks | RAG + policy retrieval |
| 8 | 4 days | 7-8 weeks | Entity resolution |
| 9 | 3 days | ~8 weeks | Policy assessment |
| 10 | 4 days | ~9 weeks | Full orchestration |

## Folder Structure

```
casework-service/
  src/main/java/com/diligence/
    casework/       # Case management (Phase 1-2)
    documents/      # Upload, parsing (Phase 2, 5)
    rules/          # Deterministic rules (Phase 3)
    tools/          # ABN Lookup, Supplier Master (Phase 4, 8)
    agents/         # All agents (Phases 5-10)
    mcp/            # MCP server (Phase 6)
    rag/            # RAG pipeline (Phase 7)
    config/         # Spring config
  src/test/java/    # Growing test suite
  src/main/resources/
    db/migration/   # Flyway schemas (evolving)
    prompts/        # Versioned agent skills
data/
  case-documents/   # Uploaded case documents
sample-data/
  documents/        # Test PDFs
  policies/         # Policy corpus
docs/               # Architecture, decisions
```

## Key Decisions

1. **Start without AI:** Phases 1-4 build infrastructure before agent use
2. **One capability per phase:** Each phase teaches one concept
3. **Mock before real:** Use mock data until infrastructure is solid
4. **Test at every step:** Unit, integration, and end-to-end tests
5. **Immutable human decisions:** Never let agents write the final outcome
6. **Evidence grounding:** Every finding must cite source (page, tool, rule, policy)
7. **Governance-first:** MCP and tool authorization before agents go autonomous

## What You'll Learn

- **Spring Boot patterns** (REST, JPA, config, testing)
- **API integration** (external APIs, error handling)
- **LLM tool calling** (Spring AI, prompt design, structured outputs)
- **RAG & vector search** (embeddings, pgvector, citations)
- **Agent orchestration** (bounded agents, tool governance, workflow)
- **Enterprise patterns** (audit logging, access control, state machines)

## Progress Tracking

- **Phase 1:** ✅ Complete & Optimized
  - Case API with REST endpoints (POST/GET/PATCH)
  - PostgreSQL persistence with Flyway migrations
  - Single UUID ID design (simplified from dual ID)
  - Project Lombok for boilerplate reduction
  - 5 passing tests (3 unit, 2 integration)
  - Package: com.diligence.casework
  - Full documentation (phase1_review.html + code walkthrough)

- **Phase 2:** ✅ Complete & Tested
  - Document upload with multipart file handling
  - One-to-many relationship (Case ↔ Documents)
  - File validation (PDF only, max 10MB)
  - File storage in data/case-documents/{caseId}/
  - REST endpoints (POST upload, GET list, DELETE)
  - 6 passing tests (3 unit, 3 integration)
  - Database migration (V2__Create_documents_table.sql)
  - Package: com.diligence.documents

- **Phase 3:** ✅ Complete & Tested
  - 8 rule types (SANCTION_CHECK, ABN_VALIDATION, etc.)
  - 4 severity levels (CRITICAL, HIGH, MEDIUM, LOW)
  - Deterministic rule evaluation service
  - Rule result tracking with evidence
  - 6 tests (4 unit, 6 integration)
  - Database migration (V3__Create_rules_tables.sql)
  - Package: com.diligence.rules

- **Phase 4:** ✅ Complete & Tested
  - ABN Lookup Service with mock API (real API integration deferred to Phase 5)
  - Supplier Verification Service with fuzzy name matching (Levenshtein distance)
  - Tool Result entity and repository with CASCADE DELETE
  - REST endpoints (POST verify supplier, GET tool results with filtering)
  - Evidence tracking with JSON serialization of tool input/output
  - Database migration (V4__Create_tool_results_table.sql) with indices
  - 6 passing tests (3 unit, 3 integration) — 26/26 total test suite passing
  - Clean code: removed unused variables and RestTemplate (will add back in Phase 5)
  - Package: com.diligence.tools
  - Configuration: application.yml with ABN lookup settings (ready for Phase 5)

---

See `phase1_review.html` for detailed Phase 1 documentation with code walkthroughs and architecture explanations.
