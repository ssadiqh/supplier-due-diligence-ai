---
sidebar_position: 3
---

# LLM Integration with Spring AI

How Claude API is integrated for evidence extraction.

## Architecture

```
┌────────────────────────────────────────────┐
│     DocumentEvidenceAgent                  │
│     (Call LLM for fact extraction)         │
└─────────────┬────────────────────────────┘
              │
              ↓
┌────────────────────────────────────────────┐
│     Spring AI 2.0.1 ChatModel              │
│     (Auto-configured from API key)         │
└─────────────┬────────────────────────────┘
              │
              ↓
┌────────────────────────────────────────────┐
│     Anthropic API (https://api.anthropic.com)
│     (Claude 3.5 Sonnet)                    │
└────────────────────────────────────────────┘
```

---

## Configuration

### 1. Add Dependency (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-anthropic</artifactId>
    <version>2.0.1</version>
</dependency>
```

### 2. Configure API Key (application.yml)

```yaml
spring:
  ai:
    anthropic:
      api-key: sk-ant-api03-...  # From https://console.anthropic.com/account/keys
      model: claude-3-5-sonnet-20241022
```

### 3. Create Bean (LlmConfig.java)

```java
@Configuration
@ConditionalOnProperty(name = "spring.ai.anthropic.api-key")
public class LlmConfig {
    // ChatModel bean auto-configured by Spring AI
    // No need to manually wire - Spring AI handles it
}
```

---

## Using the ChatModel

### DocumentEvidenceAgent Implementation

```java
@Service
public class DocumentEvidenceAgent {
    
    private final DocumentParser documentParser;
    private final ObjectMapper objectMapper;
    private final Optional<ChatModel> chatModel;  // Injected by Spring
    
    public DocumentEvidenceAgent(
        DocumentParser documentParser, 
        ObjectMapper objectMapper, 
        Optional<ChatModel> chatModel
    ) {
        this.documentParser = documentParser;
        this.objectMapper = objectMapper;
        this.chatModel = chatModel;
    }
    
    /**
     * Extract supplier facts from PDF using Claude AI.
     */
    public ExtractionResult extractSupplierEvidence(
        UUID caseId, 
        UUID documentId, 
        File documentFile
    ) {
        try {
            // 1. Parse PDF
            List<PageChunk> chunks = documentParser.parseDocument(documentFile);
            if (chunks.isEmpty()) {
                return createErrorResult(caseId, documentId, "No text found");
            }
            
            // 2. Check if LLM is available
            if (chatModel.isEmpty()) {
                return createNotConfiguredResult(caseId, documentId, chunks.size());
            }
            
            // 3. Call Claude
            String chunksText = formatChunksForPrompt(chunks);
            SupplierFactsOutput factsOutput = extractFactsViaLLM(chunksText);
            
            // 4. Build result
            ExtractionResult result = new ExtractionResult();
            result.setSuccess(true);
            result.setModelUsed("claude-3-5-sonnet-20241022");
            result.setTokensUsed(estimateTokens(chunksText));
            result.setEvidence(buildEvidenceString(factsOutput));
            
            return result;
            
        } catch (Exception e) {
            return createErrorResult(caseId, documentId, e.getMessage());
        }
    }
    
    /**
     * Call Claude API to extract facts.
     */
    private SupplierFactsOutput extractFactsViaLLM(String documentText) {
        // Create prompt
        PromptTemplate template = new PromptTemplate(
            "Extract supplier facts from this document:\n\n{text}"
        );
        Prompt prompt = template.create(Map.of("text", documentText));
        
        // Call Claude
        String response = chatModel.get()
            .call(prompt)
            .getResult()
            .getOutput()
            .getText();
        
        // Parse response as JSON
        return objectMapper.readValue(response, SupplierFactsOutput.class);
    }
}
```

---

## Request/Response Flow

### 1. Request to Claude

```
User Input (in DocumentEvidenceAgent):
  ├─ Document text (chunked): "Acme Corp, founded 2015, 50 employees..."
  ├─ System prompt: "Extract supplier facts..."
  ├─ Model: "claude-3-5-sonnet-20241022"
  └─ Temperature: 0.0 (deterministic)

Spring AI converts to HTTP Request:
  POST https://api.anthropic.com/v1/messages
  {
    "model": "claude-3-5-sonnet-20241022",
    "max_tokens": 1024,
    "temperature": 0.0,
    "messages": [
      {
        "role": "user",
        "content": "Extract supplier facts from: Acme Corp..."
      }
    ]
  }
```

### 2. Response from Claude

```json
{
  "id": "msg_...",
  "type": "message",
  "role": "assistant",
  "content": [
    {
      "type": "text",
      "text": "{\"facts\": [{\"category\": \"company_name\", \"value\": \"Acme Corp\", \"confidence\": 0.98}]}"
    }
  ],
  "model": "claude-3-5-sonnet-20241022",
  "stop_reason": "end_turn",
  "stop_sequence": null,
  "usage": {
    "input_tokens": 156,
    "output_tokens": 89
  }
}
```

### 3. Result in Application

```java
ExtractionResult result = new ExtractionResult();
result.setSuccess(true);
result.setModelUsed("claude-3-5-sonnet-20241022");
result.setTokensUsed(156 + 89);  // Input + output tokens
result.setEvidence("Company name: Acme Corp (confidence: 98%)");
```

---

## Token Counting

### Formula

```
Input tokens  = (document_text_length / 4) + system_prompt_tokens
Output tokens = (response_length / 4)
Total tokens  = input + output
Cost          = (input_tokens * $3 + output_tokens * $15) / 1,000,000
```

### Example

```
Document:    5,000 characters
Chunked:     450 characters per chunk (with overlap)
Tokens:      450 / 4 = ~112 tokens
System:      ~50 tokens
Total input: ~162 tokens
Response:    ~200 characters = ~50 tokens
Total:       ~212 tokens
Cost:        ($3 × 162 + $15 × 50) / 1,000,000 = $1.236 / 1M tokens
Per doc:     ~0.00062¢
```

---

## Error Handling

### Missing API Key

```java
if (chatModel.isEmpty()) {
    return createNotConfiguredResult(caseId, documentId, chunks.size());
    // Returns: ExtractionResult with success=false, 
    // errorMessage="NOT_CONFIGURED: LLM extraction not available"
}
```

### API Timeout

```java
try {
    // Call Claude (30-second timeout)
    String response = chatModel.get().call(prompt).getResult().getOutput().getText();
} catch (RestClientException e) {
    logger.error("Claude API timeout or error: {}", e.getMessage());
    return createErrorResult(caseId, documentId, "Claude API error: " + e.getMessage());
}
```

### Invalid JSON Response

```java
try {
    SupplierFactsOutput output = objectMapper.readValue(response, SupplierFactsOutput.class);
} catch (JsonProcessingException e) {
    logger.error("Failed to parse Claude response as JSON");
    return createErrorResult(caseId, documentId, "Invalid response format from Claude");
}
```

---

## System Prompts

### Extraction Prompt (in supplier_extraction_v1.txt)

```
You are a supplier due-diligence analyst. Extract key facts from the provided document.

For each fact, provide:
1. Category (company_name, founded_year, employee_count, revenue, industry, directors, certifications)
2. Value (the extracted fact)
3. Confidence (0.0-1.0)
4. Pages (which pages contain this)

Return as JSON:
{
  "facts": [
    {
      "category": "company_name",
      "value": "Acme Corporation",
      "confidence": 0.98,
      "pages": [1]
    }
  ],
  "summary": "Acme is an IT consulting firm..."
}
```

---

## Graceful Degradation

### When API Key Missing

```
1. Spring AI detects no spring.ai.anthropic.api-key
2. LlmConfig bean NOT created (@ConditionalOnProperty fails)
3. DocumentEvidenceAgent receives Optional.empty()
4. Extraction returns NOT_CONFIGURED status
5. Application continues working (tests pass, rules work)
```

### When Claude API Down

```
1. ChatModel.call() throws RestClientException
2. Caught in DocumentEvidenceAgent
3. Returns ErrorResult with status=false
4. Case continues to rules evaluation
5. Analyst sees "Extraction failed - Claude API error"
```

---

## Testing

### Unit Test (No API Key)

```java
@Test
void testExtractionWithoutLLM() {
    // DocumentEvidenceAgent initialized with Optional.empty()
    DocumentEvidenceAgent agent = new DocumentEvidenceAgent(
        documentParser, 
        objectMapper, 
        Optional.empty()  // No ChatModel
    );
    
    ExtractionResult result = agent.extractSupplierEvidence(caseId, docId, pdfFile);
    
    assertFalse(result.getSuccess());
    assertTrue(result.getErrorMessage().contains("NOT_CONFIGURED"));
    assertNull(result.getModelUsed());
}
```

### Integration Test (Real Claude API)

```java
@Test
void testExtractionWithRealAPI() {
    // Requires ANTHROPIC_API_KEY environment variable set
    ExtractionResult result = evidenceAgent.extractSupplierEvidence(caseId, docId, pdfFile);
    
    assertTrue(result.getSuccess());
    assertEquals("claude-3-5-sonnet-20241022", result.getModelUsed());
    assertNotNull(result.getTokensUsed());
    assertTrue(result.getEvidence().length() > 0);
}
```

---

## Pricing & Cost Management

### Free Tier (Anthropic)

- No free tier currently, but offers $5 credits for testing
- Recommended: Set spending limits in console.anthropic.com

### Cost Optimization

1. **Batch extractions:** Process multiple docs in one request
2. **Smaller chunks:** Use 300-char chunks instead of 500 to reduce tokens
3. **Cache prompts:** Claude API supports prompt caching (coming soon)

### Budget Example

```
$10 budget

Document 1: 2,000 tokens = $0.0135
Document 2: 1,500 tokens = $0.0101
...
Document 741: ~$10.00

So: ~740 documents with $10 credit
```

---

## Next Steps

- **Database schema:** See [Database](./database.md)
- **Testing strategy:** Read [Testing](./testing.md)
- **API reference:** Check [API Overview](../getting-started/api-overview.md)
