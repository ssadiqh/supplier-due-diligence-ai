# Phase 5 Learning Walkthrough

## Document Evidence Agent & LLM Integration

---

## WHAT IS PHASE 5?

**Goal:** Use LLM to extract structured facts from supplier documents

**Why it matters:**
- Automates document review (no human needed to read PDFs)
- Grounds evidence in source documents (citations with page numbers)
- Maintains immutable record (what LLM said, when, about what document)
- Foundation for intelligent decision-making in later phases

**Key Achievement:** Transform supplier documents into verified facts with confidence scores and page citations

---

## THE PROBLEM PHASE 5 SOLVES

Phase 4: We verified supplier ABN (business registry check)
```
Case: "Acme Corp, ABN 16009661901"
  ↓
ABN Lookup: "Registry shows: Acme Corporation Pty Ltd, Active"
  ↓
Result: Name matches ✓
```

But we don't know:
- Is Acme Corp a real, stable company?
- Do they have office locations / employee count?
- Are there any red flags in their history?
- What's their regulatory compliance status?

**Answer:** Read their documents (annual reports, tax filings, certifications, credentials)

Phase 5 solution:
```
Document (PDF): Acme Corp 2024 Annual Report
  ↓
LLM extracts facts:
  - Employees: 1,250
  - Revenue: $50M
  - Locations: Sydney, Melbourne, Brisbane
  - Certifications: ISO 9001, ISO 14001
  - Risk flags: None identified
  ↓
Evidence stored with source pages: "Page 3-5, 12, 18"
  ↓
Result: VERIFIED WITH EVIDENCE
```

---

## ARCHITECTURE: The Flow

```
Document (PDF)
       ↓
   DocumentParser
       ├─ Extract text from PDF
       ├─ Split into chunks (500-char windows, 50-char overlap)
       └─ Maintain page references
       ↓
   DocumentEvidenceAgent
       ├─ 1. Load document chunks
       ├─ 2. Call Spring AI ChatClient with prompt
       ├─ 3. LLM extracts facts + page references
       ├─ 4. Parse structured output
       ├─ 5. Build evidence string
       └─ 6. Save ExtractionResult (immutable)
       ↓
   ExtractionResult (Database - forever immutable)
       └─ toolName: "DOCUMENT_EXTRACTION"
       └─ input: {documentId, documentName}
       └─ output: {facts, confidence, pages}
       └─ evidence: "Extracted from pages 3-5, 12..."
```

**Key difference from Phase 4:**
- Phase 4: Called external API, got structured response, saved result
- Phase 5: Call LLM, guide extraction with prompt, parse unstructured output, save result

**Same pattern:** Input → Tool → Output → Evidence → Record

---

## COMPONENT 1: DocumentParser

**What it does:** Extract text from PDF and break into processable chunks

**File:** `src/main/java/com/diligence/documents/DocumentParser.java`

**Key Method:** `parseDocument(Document doc)`

```
Input: PDF file stored in data/case-documents/{caseId}/{filename}.pdf

Process:
  1. Open PDF using Apache PDFBox
  2. Extract text preserving page boundaries
  3. Split into chunks:
     - Size: 500 characters (fits in context, captures context)
     - Overlap: 50 characters (maintains continuity between chunks)
  4. Create PageChunk objects with:
     - Page number (1-indexed)
     - Text content
     - Start/end position in document
  
Output: List<PageChunk>
  [
    {page: 1, text: "Company Overview...", start: 0, end: 523},
    {page: 1, text: "...history continues...", start: 474, end: 997},  // 50-char overlap
    {page: 2, text: "Financial Summary...", start: 1000, end: 1523}
  ]
```

**Why this design:**
- ✅ Chunks small enough for LLM context
- ✅ Overlap prevents losing info at boundaries
- ✅ Page numbers preserved for citations
- ✅ Streaming-friendly (process chunks one by one)

**Error Handling:**
- Invalid PDF → log error, return empty list
- Corrupted pages → skip, continue with remaining
- Large PDFs (>100MB) → warn in logs, process anyway

---

## COMPONENT 2: DocumentEvidenceAgent

**What it does:** Orchestrate LLM extraction with Spring AI

**File:** `src/main/java/com/diligence/agents/DocumentEvidenceAgent.java`

**Main Method:** `extractSupplierEvidence(UUID caseId, UUID documentId)`

**Step-by-step execution:**

```
1. Fetch Document from database
   └─ Contains: filename, fileSize, caseId

2. Load and parse document
   └─ Get: List<PageChunk> with page numbers

3. Build prompt for LLM
   └─ System: "Extract supplier facts from document..."
   └─ User: "Document chunks [CHUNKS]"

4. Call Spring AI ChatClient
   └─ Model: gpt-4 (or configurable)
   └─ Temperature: 0.0 (deterministic - no creativity)
   └─ Output format: Structured JSON (SupplierFactsOutput class)

5. Parse structured response
   └─ Facts: List<SupplierFact>
   └─ Confidence: 0.0-1.0 per fact
   └─ Page references: [3, 5, 12, ...]

6. Build evidence string
   └─ "Extracted facts from pages 3, 5, 12"
   └─ "Company has 1,250 employees (high confidence)"

7. Save ExtractionResult
   └─ Store: {success, facts, evidence, error}
   └─ Immutable: never changes again

8. Return result to controller
   └─ Always HTTP 200
```

**Key Design: Spring AI ChatClient**

```java
// Spring AI provides fluent API for LLM calls
ChatClient chatClient = ChatClient.create(chatModel);

Message response = chatClient
  .prompt()
  .system("You are a supplier facts extractor...")
  .user("Extract facts from: " + documentText)
  .call()
  .getResult()
  .getOutput();
```

Benefits:
- ✅ Automatic retry on rate-limit
- ✅ Token counting built-in
- ✅ Structured output parsing
- ✅ Multiple model support
- ✅ Easy to test (mock ChatModel)

---

## COMPONENT 3: Structured Output (JSON Schema)

**Problem:** LLM returns unstructured text, we need machine-readable facts

**Solution:** Structured output with JSON schema

**File:** `src/main/java/com/diligence/agents/SupplierFactsOutput.java`

```java
@Data
public class SupplierFactsOutput {
  private List<SupplierFact> facts;
  private String summary;
  private boolean complete;  // Did extraction find all facts?
}

@Data
public class SupplierFact {
  private String category;        // "employees", "revenue", "locations", etc.
  private String value;           // "1,250 employees"
  private double confidence;      // 0.85 = 85% confident in this fact
  private List<Integer> pages;    // [3, 5, 12] = found on these pages
  private String reasoning;       // Why did we extract this?
}
```

**Spring AI converts this to JSON schema:**
```json
{
  "type": "object",
  "properties": {
    "facts": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "category": {"type": "string"},
          "value": {"type": "string"},
          "confidence": {"type": "number"},
          "pages": {"type": "array", "items": {"type": "integer"}},
          "reasoning": {"type": "string"}
        }
      }
    },
    "summary": {"type": "string"},
    "complete": {"type": "boolean"}
  }
}
```

**LLM now returns:**
```json
{
  "facts": [
    {
      "category": "employees",
      "value": "1,250 employees",
      "confidence": 0.95,
      "pages": [3, 5],
      "reasoning": "Stated in company overview and staffing report"
    },
    {
      "category": "revenue",
      "value": "$50M FY2024",
      "confidence": 0.88,
      "pages": [12, 14],
      "reasoning": "From financial statements"
    }
  ],
  "summary": "Company is established with stable operations",
  "complete": true
}
```

**Why JSON schema matters:**
- ✅ No parsing errors (structured, not text)
- ✅ Confidence scores quantify uncertainty
- ✅ Page citations for human verification
- ✅ Reasoning explains extraction decision
- ✅ Easy to store and query in database

---

## COMPONENT 4: Prompt Engineering

**The prompt is the "instructions" for LLM extraction**

**File:** `src/main/resources/prompts/supplier_extraction_v1.txt`

```
# Supplier Evidence Extraction Prompt

You are extracting factual information from supplier documents.

## Task
Extract verified facts about the supplier from the provided document chunks.
Report only facts explicitly stated in the document.

## Facts to extract
1. **employees** - Company headcount, staffing levels
2. **revenue** - Annual/period revenue figures
3. **locations** - Office locations, facilities
4. **certifications** - ISO, industry certifications, accreditations
5. **regulatory_status** - Licenses, permits, compliance status
6. **red_flags** - Negative items: litigation, bankruptcies, sanctions
7. **ownership** - Who owns the company, corporate structure
8. **years_in_business** - How long operating

## Confidence scoring
- 1.0 = Explicitly stated ("The company has 1,250 employees")
- 0.85 = Clearly implied ("Headcount increased from 1,200 to 1,250")
- 0.70 = Inferred ("Staff grew 4% from prior year's 1,200")
- 0.50 = Ambiguous ("Company mentions growth")
- Do NOT report below 0.50

## Page references
Track which pages facts come from. Must be accurate.

## Important constraints
- Do NOT assume or infer beyond document text
- Do NOT include marketing claims without verification
- Do NOT report figures without currency/period specified
- Mark "complete: false" if document seems incomplete (e.g., PDF cut off)

## Output format
Return JSON matching this exact structure:
{
  "facts": [
    {
      "category": "...",
      "value": "...",
      "confidence": 0.XX,
      "pages": [1, 2, ...],
      "reasoning": "..."
    }
  ],
  "summary": "...",
  "complete": true/false
}
```

**Key Design Principles:**
1. ✅ **Be specific** — list exact categories we care about
2. ✅ **Quantify uncertainty** — confidence scores, not yes/no
3. ✅ **Cite sources** — page numbers for verification
4. ✅ **Guard against hallucination** — "Do NOT assume"
5. ✅ **Version it** — `supplier_extraction_v1.txt` allows v2, v3 later
6. ✅ **Keep it concise** — LLM processes faster, cheaper

**Prompt Versioning (A/B testing):**
```
v1: Initial extraction (all facts)
v2: Focus on risk indicators (for risk assessment)
v3: Regulatory compliance only (for compliance checking)
v4: Financial health indicators (for credit analysis)
```

Store in database which prompt version extracted facts → can compare results later

---

## COMPONENT 5: ExtractionResult Entity

**What it does:** Immutable record of document extraction

**File:** `src/main/java/com/diligence/agents/ExtractionResult.java`

**Similar to ToolResult from Phase 4, but with LLM-specific fields:**

```
id: UUID (unique identifier)
caseEntity: Reference to Case (CASCADE DELETE)
documentEntity: Reference to Document (CASCADE DELETE)
toolName: "DOCUMENT_EXTRACTION" (identifies tool)
toolType: "EVIDENCE_EXTRACTION" (categorizes tool)
input: JSON {documentId, documentName, chunkCount}
output: JSON {facts[], summary, complete}
success: Boolean (did extraction succeed?)
errorMessage: String (if failed, why?)
evidence: String (human-readable summary with confidence)
promptVersion: "v1" (which prompt was used?)
modelUsed: "gpt-4-turbo" (which LLM model?)
tokensUsed: 1,234 (cost tracking)
executedAt: LocalDateTime (IMMUTABLE)
```

**Pattern: Same as Phase 4**
- Input captured (what we asked for)
- Output captured (what LLM returned)
- Success/failure tracked
- Evidence human-readable
- Timestamp immutable

**New field: promptVersion**
- Allows A/B testing different extraction strategies
- Can re-extract with v2 if v1 had issues
- Tracks which prompt version was most accurate

---

## COMPONENT 6: ExtractionResultRepository

**Spring Data JPA repository:**

```java
public interface ExtractionResultRepository 
    extends JpaRepository<ExtractionResult, UUID> {
    
    List<ExtractionResult> findByDocumentId(UUID documentId);
    List<ExtractionResult> findByCaseIdAndSuccess(UUID caseId, Boolean success);
    List<ExtractionResult> findByPromptVersion(String version);
    List<ExtractionResult> findByModelUsed(String model);
}
```

**Queries:**
- Get all extractions for a document
- Get successful extractions for a case
- Compare extraction results from different prompt versions
- Track which models were most accurate

---

## DATABASE SCHEMA

**Table: extraction_results**

```sql
CREATE TABLE extraction_results (
  id UUID PRIMARY KEY,
  case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
  document_id UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
  tool_name VARCHAR(100),           -- "DOCUMENT_EXTRACTION"
  tool_type VARCHAR(100),           -- "EVIDENCE_EXTRACTION"
  input TEXT,                       -- JSON: {documentId, documentName, chunkCount}
  output TEXT,                      -- JSON: {facts[], summary, complete}
  success BOOLEAN,
  error_message TEXT,
  evidence TEXT,                    -- human-readable findings
  prompt_version VARCHAR(20),       -- "v1", "v2", "v3"
  model_used VARCHAR(100),          -- "gpt-4-turbo", "claude-3-opus", etc.
  tokens_used INTEGER,              -- for cost tracking
  executed_at TIMESTAMP             -- immutable
);

-- Indices for common queries
CREATE INDEX idx_extraction_results_document_id ON extraction_results(document_id);
CREATE INDEX idx_extraction_results_case_id ON extraction_results(case_id);
CREATE INDEX idx_extraction_results_success ON extraction_results(success);
CREATE INDEX idx_extraction_results_prompt_version ON extraction_results(prompt_version);
CREATE INDEX idx_extraction_results_executed_at ON extraction_results(executed_at);
```

---

## TEST COVERAGE: 9 Tests

**Unit Tests (DocumentParserTest):**

1. **testParseValidPDF**
   - Input: Valid PDF file
   - Expected: List<PageChunk> with correct page numbers
   - Tests: PDF parsing works

2. **testParseChunking**
   - Input: PDF text that exceeds chunk size
   - Expected: Multiple chunks with 50-char overlap
   - Tests: Chunking algorithm works

3. **testParseInvalidPDF**
   - Input: Corrupted PDF file
   - Expected: Empty list or error handled gracefully
   - Tests: Error handling works

**Unit Tests (DocumentEvidenceAgentTest):**

4. **testExtractSupplierEvidence_Success**
   - Input: Valid document with clear supplier info
   - Expected: success=true, facts extracted, pages cited
   - Tests: Full extraction flow works

5. **testExtractSupplierEvidence_Mocked**
   - Input: Same document, mock ChatModel
   - Expected: Predictable facts extracted
   - Tests: Mocking works for testing

6. **testExtractSupplierEvidence_NoFactsFound**
   - Input: Document with minimal supplier info
   - Expected: success=true but facts=empty, evidence explains
   - Tests: Handles "nothing to extract" case

**Integration Tests (DocumentEvidenceControllerTest):**

7. **testExtractEvidence**
   - POST /cases/{caseId}/documents/{docId}/extract-evidence
   - Expected: HTTP 200, ExtractionResult returned
   - Tests: REST endpoint works

8. **testGetExtractionResults**
   - GET /cases/{caseId}/extraction-results
   - Expected: List of all extractions for case
   - Tests: Result retrieval works

9. **testComparePromptVersions**
   - Extract same document with v1 and v2 prompts
   - Expected: Can compare results by prompt_version
   - Tests: A/B testing capability works

---

## KEY CONCEPTS EXPLAINED

### 1. Spring AI ChatClient

```
ChatClient = Wrapper around LLM API calls
Provides:
  - Automatic retry logic
  - Token counting
  - Structured output parsing
  - Model-agnostic interface (works with OpenAI, Anthropic, local models)

Usage:
  ChatClient client = ChatClient.create(chatModel);
  String response = client.prompt()
    .system("You are...")
    .user("Extract facts...")
    .call()
    .getResult()
    .getOutput()
    .getContent();
```

### 2. Structured Output (JSON Schema)

**Problem:** LLM text output is unstructured
```
"The company has approximately 1,250 employees across three locations..."
↓
Hard to parse, extract fields, store in database
```

**Solution:** JSON schema tells LLM expected format
```
{
  "facts": [{"category": "employees", "value": "1,250", ...}],
  "summary": "...",
  "complete": true
}
↓
Easy to parse, extract fields, store
```

### 3. Evidence Grounding (Citations)

**Why page numbers matter:**

```
Claim: "Company has 1,250 employees"
Without pages: Humans can't verify
With pages: "See page 5 and 12" - can verify

Evidence string: "Employee count stated on pages 5, 12 of annual report"
↓
Human can look it up, confirm
```

**Always capture:**
- Page numbers where fact found
- Confidence score (0.5-1.0)
- Reasoning (why did we extract this?)
- Source document

### 4. Prompt Versioning

```
v1: Extract all facts (baseline)
v2: Extract only high-confidence facts (> 0.90)
v3: Extract risk indicators (red flags only)
v4: Extract financial metrics

Compare results:
- Which version finds more useful facts?
- Which version has fewer hallucinations?
- Which version is fastest/cheapest?
```

A/B test prompts before rolling out to production.

### 5. Temperature = 0.0

```
Temperature = LLM's creativity level
- 0.0 = Deterministic (same input → same output)
- 0.5 = Balanced
- 1.0+ = Creative (random outputs)

For fact extraction:
  Temperature = 0.0 ← Reproducible, reliable
  (Don't want creative "inventions")
```

### 6. Confidence Scores

```
Confidence 1.0 = "The company has 1,250 employees"
            └─ Explicitly stated

Confidence 0.85 = "Headcount increased from 1,200 to 1,250 last year"
             └─ Clearly implied but not exact

Confidence 0.70 = "Staff grew 4% from 1,200"
             └─ Calculated from doc, not stated

Confidence < 0.50 = "Company mentioned growth"
               └─ Too vague, don't report

Always use scores. Helps downstream decisions.
```

---

## WORKFLOW: How Phase 5 Works End-to-End

```
BEFORE Phase 5:
Case: "Acme Corp, ABN verified"
Question: "What do we know about this company?"
Answer: "We verified their ABN. That's it."

AFTER Phase 5:

1. User uploads 3 documents:
   - Annual report 2024
   - Tax filing 2024
   - Certifications

2. System processes:
   Document 1 → Extract facts → Save 1,234 employee count
   Document 2 → Extract facts → Save $50M revenue
   Document 3 → Extract facts → Save ISO 9001 certified

3. All stored as ExtractionResult with:
   - Page citations (can verify)
   - Confidence scores (can assess uncertainty)
   - Prompt version (can reproduce)

4. Human review:
   "Company is established ($50M revenue), certified (ISO 9001),
    stable (1,234 employees). No red flags in documents.
    Risk level: LOW"

Question: "Why do you say low risk?"
Answer: "Because page 3 shows stable operations, page 12 shows compliance"
  └─ Can click and verify immediately
```

---

## QUICK REFERENCE

### To Run Phase 5
```bash
cd casework-service
mvn spring-boot:run
```

### Environment Setup (application.yml)
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}  # Get from OpenAI platform
      model: gpt-4-turbo          # Or use gpt-3.5-turbo for testing

document:
  extraction:
    prompt-version: v1            # Which prompt to use
    confidence-threshold: 0.70    # Don't report below 70%
```

### To Test (end-to-end)
```bash
# Create a case
curl -X POST http://localhost:8080/api/cases \
  -H "Content-Type: application/json" \
  -d '{"supplierName":"Acme Corp","requestedBy":"analyst@company.com"}'

# Upload document
curl -X POST http://localhost:8080/api/cases/{caseId}/documents \
  -F "file=@annual_report.pdf"

# Extract evidence from document
curl -X POST http://localhost:8080/api/cases/{caseId}/documents/{docId}/extract-evidence

# View extraction results
curl http://localhost:8080/api/cases/{caseId}/extraction-results

# View results by prompt version (compare v1 vs v2)
curl http://localhost:8080/api/cases/{caseId}/extraction-results?promptVersion=v1
curl http://localhost:8080/api/cases/{caseId}/extraction-results?promptVersion=v2
```

### When OPENAI_API_KEY is Missing (Testing)
```bash
# Use mock ChatModel in tests
# Mock returns predictable facts:
# - "1,250 employees" (confidence 0.95)
# - "$50M revenue" (confidence 0.90)
# - "ISO 9001 certified" (confidence 1.0)

# No API calls made, tests run instantly
mvn test
```

---

## How Phase 5 Connects to Phase 4

**Phase 4 (ABN Lookup):**
```
Case: "Acme Corp, ABN 16009661901"
↓
External API: Australian Business Register
↓
Result: "Name matches registry ✓"
```

**Phase 5 (Document Extraction):**
```
Case: "Acme Corp, ABN 16009661901"
  (already verified in Phase 4)
↓
Documents: Annual report, tax filing, certifications
↓
LLM extracts facts from documents
↓
Result: "Company has 1,250 employees, $50M revenue, ISO certified"
```

**Both use same pattern:**
- Input → Tool → Output → Evidence → Immutable Record
- HTTP 200 always (failures stored as data)
- Evidence grounding (citations/page numbers)
- Confidence tracking

**Next: Phase 6 consolidates both (ABN + Document evidence together)**

---

## How Phase 5 Fits in the 10-Phase Plan

```
Phase 1: Create cases
   ↓
Phase 2: Upload documents
   ↓
Phase 3: Evaluate rules (deterministic logic)
   ↓
Phase 4: Verify supplier via tool (external API)
   ↓
Phase 5: Extract evidence from documents (LLM) ← YOU ARE HERE
   ↓
Phase 6: MCP Server & governance
   ↓
Phases 7-10: RAG, agents, orchestration
```

---

## Next Phase (6): Preview

**MCP Server & Governance:**
- Expose tools through Model Context Protocol
- Add access control (who can call which tools)
- Audit logging (track all tool calls)
- Tool schemas (structured definitions for agents)

**Pattern continues:**
- Tool execution → Evidence → Immutable record
- Humans verify
- Agents recommend, never decide alone

---

## Summary

**Phase 5 teaches you:**
1. ✅ Spring AI ChatClient (LLM integration)
2. ✅ Structured output (JSON schema)
3. ✅ Document parsing and chunking (PDF handling)
4. ✅ Evidence grounding (citations with confidence)
5. ✅ Prompt engineering (A/B testing, versioning)
6. ✅ Cost tracking (token counting)
7. ✅ Immutable extraction records (audit trail for LLM decisions)

**The Big Picture:**
Phase 5 shows how to integrate LLMs safely:
- Capture all evidence (what LLM saw, said, when)
- Grade confidence (not everything is 100% certain)
- Cite sources (humans can verify)
- Version prompts (improve over time)
- Never throw errors (save failures as data)

This is production-grade LLM architecture! 🚀

---

Ready for questions about any component?
