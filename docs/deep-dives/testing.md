---
sidebar_position: 5
---

# Testing Strategy & Implementation

How the system is tested end-to-end.

## Test Coverage

**Current:** 50+ tests passing ✅

```
Unit Tests               ~60%
Integration Tests        ~35%
E2E Tests               ~5%
Total Coverage          80%+
```

---

## Test Organization

```
src/test/java/com/diligence/
├── casework/
│   ├── CaseControllerIntegrationTest.java
│   └── CaseServiceTest.java
├── documents/
│   ├── DocumentControllerIntegrationTest.java
│   └── DocumentServiceTest.java
├── extraction/
│   ├── DocumentEvidenceAgentTest.java
│   ├── DocumentEvidenceControllerTest.java
│   └── DocumentParserTest.java
├── rules/
│   ├── RuleControllerIntegrationTest.java
│   └── RuleServiceTest.java
└── tools/
    ├── SupplierVerificationServiceTest.java
    └── ToolControllerIntegrationTest.java
```

---

## Unit Tests (No Database)

### Example: DocumentParserTest

```java
@DisplayName("Document Parser Unit Tests")
class DocumentParserTest {
    
    private DocumentParser documentParser;
    
    @BeforeEach
    void setUp() {
        documentParser = new DocumentParser();
    }
    
    @Test
    @DisplayName("Should return empty list for non-existent file")
    void testParseNonExistentFile() {
        File nonExistent = new File("nonexistent.pdf");
        List<PageChunk> chunks = documentParser.parseDocument(nonExistent);
        assertNotNull(chunks);
        assertTrue(chunks.isEmpty());
    }
    
    @Test
    @DisplayName("Should chunk 1000-char text correctly")
    void testChunking() {
        String text = "A".repeat(1000);
        List<PageChunk> chunks = documentParser.createChunks(text);
        
        // Should create chunks with 500 chars + 50 overlap
        assertEquals(3, chunks.size());
        assertEquals(500, chunks.get(0).getText().length());
    }
    
    @Test
    @DisplayName("PageChunk should store correct data")
    void testPageChunkDataStorage() {
        PageChunk chunk = new PageChunk(1, "test", 0, 4);
        
        assertEquals(1, chunk.getPageNumber());
        assertEquals("test", chunk.getText());
        assertEquals(0, chunk.getStartPosition());
        assertEquals(4, chunk.getEndPosition());
    }
}
```

---

## Integration Tests (With H2 Database)

### Example: CaseServiceTest

```java
@DisplayName("Case Service Integration Tests")
@SpringBootTest
class CaseServiceTest {
    
    @Autowired
    private CaseService caseService;
    
    @Autowired
    private CaseRepository caseRepository;
    
    private UUID testCaseId;
    
    @BeforeEach
    void setUp() {
        // H2 in-memory database is fresh for each test
        testCaseId = UUID.randomUUID();
    }
    
    @Test
    @DisplayName("Should create a case")
    void testCreateCase() {
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setId(testCaseId);
        caseEntity.setBusinessName("Test Corp");
        caseEntity.setStatus(CaseStatus.INTAKE);
        
        caseService.saveCase(caseEntity);
        
        CaseEntity retrieved = caseRepository.findById(testCaseId).orElseThrow();
        assertEquals("Test Corp", retrieved.getBusinessName());
    }
    
    @Test
    @DisplayName("Should transition case status")
    void testCaseStatusTransition() {
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setStatus(CaseStatus.INTAKE);
        caseService.saveCase(caseEntity);
        
        caseEntity.setStatus(CaseStatus.DOCUMENT_UPLOADED);
        caseService.saveCase(caseEntity);
        
        assertEquals(CaseStatus.DOCUMENT_UPLOADED, caseEntity.getStatus());
    }
}
```

---

## Controller Integration Tests (Full Request/Response)

### Example: CaseControllerIntegrationTest

```java
@DisplayName("Case Controller Integration Tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CaseControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("Should create case via POST /api/cases")
    void testCreateCaseViaAPI() {
        CaseEntity newCase = new CaseEntity();
        newCase.setBusinessName("Acme Corp");
        newCase.setRequestedBy("analyst@company.com");
        
        ResponseEntity<CaseEntity> response = restTemplate.postForEntity(
            "/api/cases",
            newCase,
            CaseEntity.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Acme Corp", response.getBody().getBusinessName());
    }
    
    @Test
    @DisplayName("Should retrieve case via GET /api/cases/{id}")
    void testGetCaseViaAPI() {
        // Create case first
        CaseEntity created = createTestCase();
        
        // Retrieve via API
        ResponseEntity<CaseEntity> response = restTemplate.getForEntity(
            "/api/cases/" + created.getId(),
            CaseEntity.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(created.getId(), response.getBody().getId());
    }
}
```

---

## Test Fixtures

### Real PDF Fixture

```
src/test/resources/fixtures/
└── sample-supplier-document.pdf
    └─ Used for PDF parsing tests
    └─ Valid PDF with supplier info
```

Usage:
```java
@Test
void testParseRealPDF() throws IOException {
    ClassPathResource pdfResource = 
        new ClassPathResource("fixtures/sample-supplier-document.pdf");
    File pdfFile = pdfResource.getFile();
    
    List<PageChunk> chunks = documentParser.parseDocument(pdfFile);
    
    assertFalse(chunks.isEmpty());
}
```

---

## Test Isolation

### H2 In-Memory Database

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    
  jpa:
    hibernate:
      ddl-auto: create-drop  # Fresh DB per test
    show-sql: false
```

**Benefits:**
- ✅ No PostgreSQL required
- ✅ Tests run in <35 seconds
- ✅ Fresh database isolation
- ✅ Can run in parallel

---

## Mock Testing

### Mocking External Services

```java
@Test
@DisplayName("Should handle ABN lookup timeout")
void testABNLookupTimeout() {
    // Mock ABN service to throw exception
    ABNLookupService mockABN = mock(ABNLookupService.class);
    when(mockABN.verifyABN("12345678901"))
        .thenThrow(new RestClientException("Timeout"));
    
    RuleService ruleService = new RuleService(mockABN);
    RuleResult result = ruleService.evaluateABNValidation(case);
    
    assertEquals(RuleOutcome.ERROR, result.getOutcome());
}
```

### Mock ABN Lookup Service

```java
@Component
public class MockABNLookupService implements ABNLookupService {
    
    @Override
    public SupplierInfo verifyABN(String abn) {
        // Return mock data instead of calling real API
        return new SupplierInfo(abn, "Test Company", true);
    }
}
```

---

## Test Execution

### Run All Tests

```bash
cd casework-service
mvn test
```

**Output:**
```
[INFO] Tests run: 50, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 34.5 seconds
```

### Run Specific Test Class

```bash
mvn test -Dtest=CaseServiceTest
```

### Run Specific Test Method

```bash
mvn test -Dtest=CaseServiceTest#testCreateCase
```

---

## Debugging Tests

### Enable SQL Logging

```yaml
logging:
  level:
    org.hibernate.sql: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Print SQL Queries

```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

---

## Test Categories

### Phase 1-4: Core Functionality

```
CaseControllerIntegrationTest       ✅ 2 tests
CaseServiceTest                      ✅ 3 tests
DocumentControllerIntegrationTest   ✅ 5 tests
DocumentServiceTest                  ✅ 3 tests
RuleControllerIntegrationTest       ✅ 5 tests
RuleServiceTest                      ✅ 4 tests
ToolControllerIntegrationTest       ✅ 3 tests

Total Phase 1-4:                     ✅ 25 tests
```

### Phase 5: PDF Parsing & Evidence

```
DocumentEvidenceAgentTest            ✅ 7 tests
DocumentEvidenceControllerTest      ✅ 8 tests
DocumentParserTest                   ✅ 7 tests
SupplierVerificationServiceTest     ✅ 3 tests

Total Phase 5:                       ✅ 25 tests
```

---

## Security Testing

### Filename Sanitization Test

```java
@Test
@DisplayName("Should sanitize filename with path traversal")
void testFilenameSanitization() {
    String malicious = "../../etc/passwd.txt";
    String sanitized = documentService.sanitizeFilename(malicious);
    
    assertEquals("etcpasswd.txt", sanitized);
    assertFalse(sanitized.contains("/"));
    assertFalse(sanitized.contains("\\"));
}
```

### Case Ownership Enforcement Test

```java
@Test
@DisplayName("Should not delete document from different case")
void testDocumentOwnershipEnforcement() {
    DocumentEntity docFromCase1 = createDocument(case1Id);
    
    // Try to delete doc1 using case2
    assertThrows(
        DocumentAccessDeniedException.class,
        () -> documentService.deleteDocumentByIdAndCaseId(docFromCase1.getId(), case2Id)
    );
}
```

---

## Performance Testing

### Load Test Example

```java
@Test
@DisplayName("Should handle 100 concurrent case creations")
void testConcurrentCaseCreation() throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    List<Future<?>> futures = new ArrayList<>();
    
    for (int i = 0; i < 100; i++) {
        futures.add(executor.submit(() -> {
            CaseEntity caseEntity = createTestCase();
            assertNotNull(caseEntity.getId());
        }));
    }
    
    executor.awaitTermination(5, TimeUnit.SECONDS);
    
    long totalCases = caseRepository.count();
    assertEquals(100, totalCases);
}
```

---

## CI/CD Integration

### GitHub Actions Example

```yaml
# .github/workflows/test.yml
name: Test

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '20'
      - run: cd casework-service && mvn test
```

---

## Future Testing (Phase 7-10)

### Phase 6: LLM Integration

```
[ ] DocumentEvidenceAgentRealLLMTest  ⏳ Planned
  └─ Test with real Claude API
  └─ Verify token counting
  └─ Test prompt variations
```

### Phase 7: RAG

```
[ ] VectorSearchTest                   ⏳ Planned
  └─ Test pgvector embeddings
  └─ Test similarity search
  └─ Test hybrid retrieval
```

### Phase 8-10: Agents & UI

```
[ ] AgentOrchestrationTest            ⏳ Planned
[ ] EndToEndWorkflowTest              ⏳ Planned
[ ] UIComponentTests (React)          ⏳ Planned
```

---

## Test Best Practices

### ✅ DO

- Test behavior, not implementation
- Use descriptive test names (@DisplayName)
- Isolate tests (no shared state)
- Mock external services
- Test error paths, not just happy path
- Use fixtures for real-world data
- Run tests frequently (on every commit)

### ❌ DON'T

- Test framework code (Spring, JPA)
- Create coupling between tests
- Leave @Ignore tests
- Mock everything (defeats integration testing)
- Test multiple behaviors per test
- Use Thread.sleep() for waiting
- Skip flaky tests without fixing

---

## Running Tests Locally

```bash
# Build + test
mvn clean package

# Test only
mvn test

# Specific test
mvn test -Dtest=DocumentParserTest

# With coverage report
mvn test jacoco:report
# See: target/site/jacoco/index.html
```

---

## Next Steps

- **Get started:** See [Quick Start](../getting-started/quickstart.md)
- **Run tests:** `mvn test` in casework-service directory
- **Debug failing test:** Add System.out.println, enable SQL logging
