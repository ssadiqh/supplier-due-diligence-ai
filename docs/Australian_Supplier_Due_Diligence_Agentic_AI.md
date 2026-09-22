# Australian Supplier Onboarding and Counterparty Due-Diligence Assistant

## Context and Continuation Brief

This document captures the agreed context, decisions and boundaries for a home-learning project that demonstrates enterprise-grade AI architecture through a realistic Australian supplier-onboarding and counterparty due-diligence use case. It is intended to be supplied to a new chat session or another AI so work can continue without reconstructing earlier discussions.

---

## 1. Project purpose

Build and learn by implementing a realistic, evidence-based application using:

- Skills and versioned agent instructions.
- LLM tool calling.
- Model Context Protocol (MCP).
- Retrieval-Augmented Generation (RAG).
- Vector storage and hybrid retrieval.
- Bounded specialist agents.
- Agentic investigation within controlled limits.
- Durable orchestration and event-driven processing.
- Deterministic rules and workflow gates.
- Human-in-the-loop review.
- Security, auditability, evaluation and responsible-AI controls.

The project is inspired by an earlier trust-onboarding and AML-review architecture, but it must not reproduce NAB systems, data, policies or intellectual property. It uses Australian public sources, purpose-built organisational policies, synthetic private documents and a mocked Supplier Master.

The objective is not to build a chatbot. The objective is to build a case-based enterprise workflow in which AI investigates and recommends while deterministic services and authorised humans retain control.

---

## 2. Selected use case

### Australian Supplier Onboarding and Counterparty-Due-Diligence Assistant

A procurement or compliance analyst receives a supplier application and supporting documents. The solution extracts evidence, verifies the Australian business, screens sanctions information, searches an internal Supplier Master, evaluates deterministic rules, retrieves applicable policies, identifies inconsistencies and prepares an evidence-backed review package.

The authorised reviewer chooses one of three outcomes:

- **Proceed**
- **Request Information**
- **Escalate**

The AI system does not approve or reject suppliers, determine legal compliance, create supplier records, alter source systems or make final sanctions decisions.

### Business outcome

Reduce the time and inconsistency involved in onboarding Australian suppliers by automating document extraction, entity verification, sanctions screening, policy assessment and review preparation, while preserving evidence traceability and human decision ownership.

### Business actors

- **Requester / procurement officer:** captures supplier information and uploads documents.
- **Due-diligence analyst:** reviews extracted evidence, discrepancies, risks and policy obligations.
- **Compliance or Legal specialist:** handles sanctions, integrity or unresolved high-risk cases.
- **Authorised approver:** records the final outcome.
- **Platform operator:** monitors ingestion, workflow failures, model performance and audit records.

---

## 3. Release 1 scope

Release 1 is a complete learning release rather than a production deployment. It demonstrates the full architectural pattern at manageable scale.

### In scope

- Submit and persist a supplier-onboarding case.
- Upload a supplier application and supporting PDFs.
- Extract structured facts with page-level evidence and confidence.
- Validate a real ABN through ABN Lookup.
- Search a mocked internal Supplier Master.
- Ingest and screen against the real DFAT Consolidated List.
- Apply versioned deterministic due-diligence rules.
- Build a focused RAG over approved supplier and procurement policies.
- Run bounded Document Evidence, Entity Resolution, Policy/Risk and Review Synthesis agents.
- Maintain durable, structured case state.
- Use controlled agentic behaviour for re-analysis, alternative searches and targeted policy retrieval.
- Create a human-review package.
- Record Proceed, Request Information or Escalate as the human decision.
- Record tool calls, policy citations, rule results, agent versions and reviewer actions.
- Include a small evaluation dataset and automated tests.

### Deferred

- Automated paid ASIC extracts.
- PPSR integration.
- Commercial credit services such as Equifax, illion or CreditorWatch.
- Real bank-account verification.
- Commercial adverse-media or PEP data.
- Complete beneficial-ownership verification.
- Production-scale high availability.
- Kubernetes/AKS.
- Enterprise SSO and full production IAM.
- Large-scale policy corpora and high-volume processing.

---

## 4. Public Australian sources

### 4.1 ABN Lookup — live API

ABN Lookup is the primary live external integration. Its free web services support ABN, ACN and name searches, status, GST information, entity type, business names, state/postcode filters and some ACNC/DGR details. Registration provides an authentication GUID.

- [ABN Lookup](https://abr.business.gov.au/)
- [ABN Lookup Web Services](https://abr.business.gov.au/Tools/WebServices)
- [Web-service methods](https://abr.business.gov.au/Documentation/WebServiceMethods)

Potential MCP tools:

- `lookup_abn`
- `validate_abn`
- `search_abn_by_name`
- `get_business_names`
- `get_entity_status`
- `get_gst_status`

### 4.2 ASIC company registers

ASIC provides free basic company information, while detailed company extracts, officeholder information and many lodged documents are paid. For Release 1, use free searches, published sample extract PDFs and a mock ASIC adapter.

- [ASIC company and organisation registers](https://www.asic.gov.au/online-services/search-asic-registers/company-and-organisation-registers)

### 4.3 DFAT Consolidated List — scheduled feed

The Australian Sanctions Office publishes an XLSX Consolidated List of sanctioned individuals, entities and vessels. It includes names, aliases and supporting attributes. It should be downloaded by a scheduled ingestion job, validated, versioned and stored locally for deterministic screening.

- [DFAT Consolidated List](https://www.dfat.gov.au/international-relations/security/sanctions/consolidated-list)
- [DFAT sanctions compliance policy](https://www.dfat.gov.au/international-relations/security/sanctions/compliance-policy)

Sanctions screening is algorithmic and evidence-based. An LLM may explain a potential match but cannot determine that two parties are legally the same entity. Strong matches must be escalated to a human.

### 4.4 Modern Slavery Statements Register

The public register provides real corporate statements suitable for document classification, extraction, comparison and evidence citation. Start with a curated selection of downloaded statements. Do not depend on undocumented scraping.

- [Modern Slavery Statements Register](https://modernslaveryregister.gov.au/)

### 4.5 ACNC Charity Register — later extension

The ACNC provides a public charity register and downloadable data. It can later support charity and not-for-profit counterparties.

- [ACNC](https://www.acnc.gov.au/)

### 4.6 Public policy corpus

- [Commonwealth Procurement Rules](https://www.finance.gov.au/government/procurement/commonwealth-procurement-rules)
- [Commonwealth Supplier Code of Conduct](https://www.finance.gov.au/government/procurement/ethical-conduct-suppliers/commonwealth-supplier-code-conduct-overview)
- [DFAT sanctions compliance policy](https://www.dfat.gov.au/international-relations/security/sanctions/compliance-policy)
- [Australian foreign-bribery guidance](https://www.ag.gov.au/crime/foreign-bribery)
- Modern-slavery guidance and selected public statements.

Public guidance must be supplemented with fictional but realistic, version-controlled organisational policies:

- Counterparty Due-Diligence Policy.
- Supplier Onboarding Standard.
- Required Evidence Matrix.
- Sanctions Screening Procedure.
- Risk Classification and Escalation Standard.
- Insurance Requirements.
- Modern-Slavery Assessment Standard.
- Human Approval and Override Policy.

---

## 5. Case documents

A Release 1 case may include:

- Supplier application form.
- ABN and legal-entity declaration.
- ASIC sample company extract.
- Certificate of currency.
- Modern-slavery statement.
- Supplier Code of Conduct declaration.
- Information-security questionnaire.
- Synthetic bank-account evidence.
- Licence or professional certification.
- Public annual report.

Real public documents can be used where lawful and appropriate. Private-style documents such as bank letters and insurance evidence should be synthetic and contain designed inconsistencies for testing.

Example scenario: the application uses a trading name, ABN Lookup returns a different legal name, the insurance certificate is near expiry, a modern-slavery statement belongs to the parent company, and one submitted name resembles a DFAT-listed alias.

---

## 6. Technology decisions

### Primary development stack

- **Language:** Java 21 or later.
- **Application framework:** Spring Boot.
- **AI integration:** Spring AI.
- **MCP:** Official MCP Java SDK with Spring AI MCP integration.
- **API:** Spring Web / REST.
- **Contracts:** Java records, Jackson and Bean Validation.
- **Database:** PostgreSQL.
- **Vector extension:** pgvector.
- **Document parsing:** Apache Tika/PDFBox plus Azure Document Intelligence.
- **Eventing:** Azure Service Bus.
- **UI:** React with TypeScript.
- **Testing:** JUnit, Testcontainers and agent evaluation datasets.
- **Packaging:** Docker.
- **Local environment:** Docker Compose.

Python is optional later for evaluation notebooks, experimental document pipelines or custom ML. It is not required for Release 1.

### Deployment platform

- **Compute:** Azure Container Apps.
- **Scheduled/background execution:** Azure Container Apps Jobs.
- **Database:** Azure Database for PostgreSQL Flexible Server.
- **Vector storage:** pgvector within Azure PostgreSQL.
- **Documents:** Azure Blob Storage.
- **OCR/layout:** Azure Document Intelligence.
- **Models and embeddings:** Azure OpenAI behind Spring AI abstractions.
- **Messaging:** Azure Service Bus.
- **Secrets:** Azure Key Vault.
- **Images:** Azure Container Registry.
- **Observability:** Application Insights and OpenTelemetry.
- **Authentication:** Microsoft Entra ID in a later increment.
- **UI hosting:** Azure Static Web Apps or a containerised frontend.

Start locally with Docker Compose. Deploy to Azure Container Apps after the vertical slice works. Do not start with AKS.

### Initial deployment structure

Begin as a modular monolith:

- One Spring Boot application containing case API, orchestration, agents, rules and RAG.
- One separately deployable MCP server representing the governed integration boundary.
- One React UI.
- PostgreSQL and document storage.
- Introduce Service Bus when asynchronous processing begins.

---

## 7. RAG and vector database

### Agreed choice

- **RAG implementation:** Spring AI modular RAG.
- **Local vector database:** PostgreSQL with pgvector in Docker.
- **Azure vector database:** Azure Database for PostgreSQL with pgvector.
- **Embeddings:** Azure OpenAI through Spring AI's `EmbeddingModel` abstraction.
- **Potential later comparison:** Azure AI Search.

Spring AI RAG is the Java application layer that coordinates query transformation, embedding, retrieval, metadata filtering, context augmentation and LLM invocation. It is not the vector database and does not automatically provide governance, citations, policy approval, access control, audit, evaluation or orchestration.

pgvector stores policy chunks, embeddings and metadata and performs semantic similarity search. Start with HNSW and cosine distance. Add PostgreSQL full-text search and result fusion for hybrid retrieval after vector retrieval works.

### RAG content boundary

The shared RAG contains approved policy and guidance. It must not contain supplier-specific evidence such as applications, insurance certificates, bank documents or company extracts. Case documents remain isolated case evidence so information cannot leak between suppliers.

### Policy-ingestion flow

1. Receive an approved policy document.
2. Extract text and structure.
3. Remove repetitive headers and footers.
4. Chunk by headings, sections and paragraphs.
5. Add policy/version/effective-date metadata.
6. Generate embeddings.
7. Store text, metadata and embeddings in pgvector.
8. Publish the searchable policy version.
9. Retain the original document for citation.

Important metadata includes policy ID, version, title, section, source URL, page, effective dates, status, jurisdiction and document type.

### Retrieval flow

1. Receive the agent's policy question.
2. Apply current-policy and jurisdiction filters.
3. Generate a query embedding.
4. Run semantic search.
5. Later, combine it with PostgreSQL full-text search.
6. Optionally rerank results.
7. Supply the strongest passages and citations to the LLM.
8. Require the model to state when evidence is insufficient.

Agents never receive direct database access. They call a governed capability such as:

```text
search_policy(case_id, question, policy_types, as_of_date, jurisdiction, top_k)
```

Azure AI Search is deferred because the initial corpus is small, PostgreSQL is already required, pgvector works locally and on Azure, and the project should expose how RAG works. Azure AI Search can later be introduced to compare managed hybrid search, semantic ranking, independent scaling, latency, retrieval accuracy and cost.

---

## 8. Agent model

An agent is not a separately trained model. It is a bounded Java component combining:

> Role + versioned skill/instructions + typed input + approved tools + model configuration + typed output + execution limits

### Specialist agents

#### Document Evidence Agent

- Classifies uploaded documents.
- Extracts supplier facts and evidence locations.
- Detects missing, conflicting or low-confidence information.
- Returns typed facts with page references and confidence.

#### Entity Resolution Agent

- Compares application, document, ABN and Supplier Master information.
- Searches using ABN, legal name or trading name.
- Identifies duplicates and discrepancies.
- Recommends match status but cannot create or select a supplier record.

#### Policy and Risk Agent

- Retrieves applicable approved policies through RAG.
- Maps obligations to available evidence.
- Distinguishes deterministic rule hits, policy-grounded gaps and AI observations.
- Cannot invent obligations or change rule severity.

#### Review Synthesis Agent

- Consolidates verified facts, rule results, policy findings and conflicts.
- Produces a concise review package, questions and a recommended next action.
- Cannot populate the human-decision field.

### What agents may decide

- Which approved search term to use.
- Whether to retry an extraction within limits.
- Whether an alternative business-name search is appropriate.
- Which policy question to retrieve.
- Whether evidence conflicts require additional investigation.

### What agents may not decide

- Which tools they are authorised to use.
- Mandatory workflow stages.
- Retry, timeout or tool-call limits.
- Rule severity.
- Whether sanctions screening is required.
- Final supplier approval or escalation.
- Creation or modification of authoritative supplier records.

Agents do not maintain private conversational memory. The orchestrator owns structured, persisted case state and sends each agent only the data required for its task.

---

## 9. Skills

A skill is a versioned task package rather than merely a prompt. It may contain:

- Role and purpose.
- Instructions and decision boundaries.
- Examples.
- Input and output schemas.
- Tool-use guidance.
- Validation and escalation rules.
- Prohibited actions.

Planned skills include:

- `extract_supplier_evidence`
- `resolve_australian_business`
- `investigate_name_mismatch`
- `assess_supplier_policy_obligations`
- `prepare_counterparty_review`

Example layout:

```text
skills/
  resolve-australian-business/
    SKILL.md
    examples/
    schemas/
```

---

## 10. Tools and MCP

The MCP server is the governed boundary between agent reasoning and operational systems. It exposes typed, case-scoped, audited and initially read-only tools.

Initial MCP tools:

```text
lookup_abn
search_abn_by_name
search_supplier_master
get_supplier_profile
screen_sanctions
get_sanctions_source_record
evaluate_due_diligence_rules
search_policy
get_case_evidence
save_agent_result
```

The MCP layer enforces authentication, authorisation, schemas, timeouts, response minimisation, call limits, audit logging and safe error handling.

Feed ingestion is not agentic. Downloading and parsing the DFAT list and ingesting policy documents are deterministic scheduled platform jobs.

---

## 11. Deterministic rules

Initial rules include:

| Condition | Controlled outcome |
|---|---|
| Invalid or cancelled ABN | Block progression and request correction |
| Legal-name mismatch | Request supporting evidence |
| Duplicate active supplier | Escalate to master-data review |
| Strong sanctions candidate | Mandatory Compliance escalation |
| Required document missing | Request information |
| Insurance expired | Block progression |
| Insurance nearing expiry | Warning |
| Bank-account name differs from legal entity | Escalate |
| Policy finding lacks adequate source evidence | Prevent supported recommendation |
| Required integration check fails | Preserve case and route to manual review |

The approved rule catalogue supplies rule identifiers, conditions, severity, outcome, version and effective dates. The LLM may explain a rule hit but cannot change it.

---

## 12. Orchestration and events

The orchestration runtime is deterministic infrastructure. It owns persisted state, sequencing, parallel execution, retries, timeouts, mandatory gates, recovery and human hand-off. Bounded agentic decisions occur inside the controlled workflow.

Indicative flow:

```text
CASE_SUBMITTED
  -> document evidence
  -> evidence sufficiency gate
  -> entity verification and sanctions screening
  -> deterministic rules and policy assessment
  -> review synthesis
  -> REVIEW_READY
  -> human decision
```

Important events:

- `CASE_SUBMITTED`
- `DOCUMENT_ANALYSIS_COMPLETED`
- `EVIDENCE_INSUFFICIENT`
- `ENTITY_VERIFICATION_COMPLETED`
- `SANCTIONS_SCREENING_COMPLETED`
- `POLICY_ASSESSMENT_COMPLETED`
- `REVIEW_READY`
- `HUMAN_DECISION_RECORDED`
- `SANCTIONS_LIST_UPDATED`

`SANCTIONS_LIST_UPDATED` may trigger re-screening of appropriate open or recently approved cases.

---

## 13. Case state

The orchestrator owns one typed case state. Agents exchange structured tasks and results through it rather than communicating through open-ended peer-to-peer conversation.

```json
{
  "caseId": "SUP-000123",
  "application": {},
  "documents": [],
  "documentEvidence": [],
  "entityResolution": {},
  "sanctionsResults": [],
  "policyAssessment": [],
  "ruleResults": [],
  "missingInformation": [],
  "agentConflicts": [],
  "reviewRecommendation": {},
  "humanDecision": {}
}
```

Only the authorised review interface may populate `humanDecision`.

---

## 14. Security and responsible-AI boundaries

- Agent tools are deny-by-default and initially read-only.
- Tool calls are case-scoped and schema-validated.
- Uploaded documents are untrusted data, never instructions.
- Tool allowlists are enforced outside the model.
- No credentials are included in prompts or tool results.
- PII is minimised in model inputs, logs and traces.
- Policies, prompts, skills, models, rules and feeds are versioned.
- Every material finding requires document, tool, rule or policy evidence.
- Unsupported output cannot silently advance the case.
- Agent retries and tool calls are capped.
- A technical failure results in controlled retry or manual review.
- The human decision is protected from agent writes.
- Sanctions matches are potential matches requiring human investigation, not automatic legal determinations.

---

## 15. Evaluation and testing

Each agent requires:

- Contract and schema tests.
- Approved-tool tests.
- Scenario tests.
- Evidence-grounding tests.
- Boundary tests preventing prohibited decisions.
- Repeatability tests.
- Prompt-injection and adversarial-document tests.
- Timeout, dependency-failure and recovery tests.

Evaluate separately:

- Document classification accuracy.
- Field extraction accuracy.
- Evidence/page localisation.
- Entity-match accuracy.
- Sanctions false-positive and false-negative rates.
- RAG retrieval precision and recall.
- Citation correctness.
- Rule correctness.
- Groundedness of recommendations.
- Human corrections and overrides.

---

## 16. Development workspace and repository structure

### IDE and local development decision

- **Primary IDE:** Visual Studio Code.
- **Java support:** Extension Pack for Java.
- **Spring support:** Spring Boot Extension Pack.
- **AI coding support:** Codex and, optionally, Claude Code through their VS Code integrations.
- **Runtime:** Java 21, Maven and Docker Desktop.
- **Local services:** PostgreSQL with pgvector through Docker Compose.

VS Code is sufficient for Java, Spring Boot, Spring AI, Maven, JUnit, debugging, React/TypeScript, Docker and Azure development. Its Java language support is based on Eclipse JDT, which should make the transition familiar to an experienced Eclipse user.

A ChatGPT or Claude subscription is a development-assistant choice; it is not an application runtime dependency. The application will eventually access models through Azure OpenAI using separately configured Azure credentials and consumption billing.

### GitHub and workspace separation

Create the project as a separate private GitHub repository, for example:

```text
supplier-due-diligence-ai
```

Clone or create it as a sibling of existing repositories, never inside another Git repository:

```text
C:\Development\
  existing-project-one\
  existing-project-two\
  supplier-due-diligence-ai\
```

The project uses one learning-oriented monorepo. The backend, future frontend, sample data, infrastructure and documentation remain together initially. Components may be separated into independently deployed services later when scale, security or operational isolation requires it.

### Agreed simple folder structure

```text
supplier-due-diligence-ai/
  backend/
    src/
      main/
        java/com/sadiq/diligence/
          casework/       # Supplier case management
          documents/      # Upload, parsing and extraction
          rules/          # Deterministic business rules
          rag/            # Chunking, embeddings and retrieval
          agents/         # Bounded agents and orchestration
          tools/          # ABN Lookup and other capabilities
          mcp/            # MCP integration
          config/         # Spring configuration
        resources/
          application.yml
          db/migration/   # Flyway migrations
          prompts/        # Version-controlled prompts
      test/
    pom.xml
    Dockerfile
  frontend/               # React reviewer interface; add later
    src/
    package.json
    Dockerfile
  sample-data/
    suppliers/            # Synthetic supplier documents
    policies/             # Approved learning policy corpus
    expected-results/     # Evaluation baselines
  infrastructure/
    local/                # Docker/local configuration
    azure/                # Azure IaC; add later
  docs/
    architecture.md
    business-flow.md
    learning-log.md
  .github/
    workflows/            # CI/CD; add later
  compose.yaml
  .env.example
  .gitignore
  AGENTS.md
  README.md
```

The backend packages deliberately make each learning concept visible:

| Folder | Primary learning purpose |
|---|---|
| `casework` | REST APIs, persistence and business workflow |
| `documents` | Upload, parsing and document intelligence |
| `rules` | Deterministic decisions versus AI judgement |
| `rag` | Chunking, embeddings, vector retrieval and grounding |
| `agents` | Agent boundaries, typed results and orchestration |
| `tools` | External APIs and model tool calling |
| `mcp` | Standardised, governed AI-tool connectivity |
| `config` | Spring configuration and dependency injection |
| `resources/prompts` | Prompt design, testing and versioning |
| `sample-data` | Repeatable scenarios and evaluation |
| `docs` | Architecture decisions and learning record |

Do not create every folder merely to complete the tree. Begin with `backend`, `sample-data`, `docs`, `compose.yaml`, `.gitignore` and `README.md`. Inside the backend, start with `casework` and `config`, then introduce `tools`, `documents`, `rules`, `rag`, `agents` and `mcp` as each capability is learned and implemented.

Keep early feature packages deliberately small. For example:

```text
casework/
  CaseController.java
  CaseService.java
  DueDiligenceCase.java
  CaseRepository.java
  CaseStatus.java
```

Do not introduce ports, adapters, domain, application and infrastructure sublayers inside every feature on day one. Add internal subpackages only when a feature becomes large enough to justify them.

Never commit secrets, real supplier information, uploaded private documents, extracted evidence, production logs or vector exports. Store credentials locally through ignored environment files and later in Azure Key Vault. Commit only `.env.example` with placeholder variable names and use synthetic supplier documents for learning and tests.

---

## 17. Build order

Do not create every agent at once. Implement the system incrementally:

1. Case API, database, document upload and structured case state.
2. Deterministic workflow and basic rule service.
3. Document Evidence Agent with one document type.
4. ABN Lookup and Supplier Master tools.
5. Governed MCP server and client.
6. Policy ingestion and pgvector retrieval.
7. Spring AI RAG with citations and metadata filters.
8. Entity Resolution Agent.
9. Policy and Risk Agent.
10. Review Synthesis Agent.
11. Asynchronous events, retries and human-review hand-off.
12. Evaluation, adversarial testing, observability and Azure deployment.

The first working vertical slice should process one supplier application and one PDF, extract a small number of facts, perform live ABN validation, apply a few deterministic rules and produce a human-review response. Capabilities are then added without changing the core case model.

---

## 18. Instructions for a new AI or chat

When continuing this project:

1. Treat the decisions in this document as the current baseline unless the user explicitly changes them.
2. Work step by step, section by section; do not deliver the entire implementation in one response.
3. Keep AI investigation separate from deterministic workflow, rules and human authority.
4. Do not introduce autonomous approval, source-system writes or unrestricted agent-to-agent communication.
5. Prefer typed Java contracts, structured outputs and evidence-backed results.
6. Preserve the agreed Java/Spring/Azure/PostgreSQL architecture unless a concrete technical constraint justifies reconsideration.
7. Use public Australian sources and synthetic private documents; do not imply access to NAB data, policies or systems.
8. Verify current APIs, service capabilities, prices and regulations against authoritative sources before relying on them.
9. Separate Release 1 learning scope from production-scale enhancements.
10. The next implementation activity should create the minimal repository skeleton and then implement the first vertical slice: case creation, persistence, one document upload, live ABN validation, a few deterministic rules and a human-review response.
