# Standalone Module Testing

Run individual modules without starting the full Spring Boot application.

## Document Parser

Test PDF parsing and text extraction in isolation.

```bash
# Demo mode (uses sample fixture)
mvn compile exec:java -Dexec.mainClass="com.diligence.extraction.DocumentParserMain"

# Test specific PDF
mvn compile exec:java -Dexec.mainClass="com.diligence.extraction.DocumentParserMain" \
  -Dexec.args="path/to/document.pdf"
```

**Output:**
- Chunk count and sizes
- Token estimation and API cost
- Preview of extracted text

---

## Rule Service

RuleService requires Spring Boot context and database repositories. Use the REST API instead:

```bash
# Start the application
mvn spring-boot:run

# Create a case
curl -X POST http://localhost:8080/api/cases \
  -H "Content-Type: application/json" \
  -d '{"supplierName": "Test Company", "supplierAbn": "12345678901"}'

# Evaluate all rules for a case
curl -X POST http://localhost:8080/api/cases/{caseId}/rules/evaluate-all

# View rule results
curl http://localhost:8080/api/cases/{caseId}/rules/results
```

**Rules Implemented:**
- ABN validation (checksum verification)
- Business registration
- Financial thresholds
- Industry restrictions
- Compliance history
- Beneficial ownership
- Political exposure
- Sanctions checking

---

## ABN Lookup Service

Test Australian Business Register lookups (mock or live).

```bash
# Demo mode (tests sample ABNs)
mvn compile exec:java -Dexec.mainClass="com.diligence.tools.ABNLookupServiceMain"

# Test specific ABN
mvn compile exec:java -Dexec.mainClass="com.diligence.tools.ABNLookupServiceMain" \
  -Dexec.args="50110219460 'Apple Australia Pty Ltd'"
```

**Output:**
- Business name
- ABN validity
- Mock/live data source

**Note:** Uses MockABNLookupService by default. To test with real ABN API, configure GUID in application.yml.

---

## Usage Pattern

Each main class:
1. **No args → Demo mode**: Test with built-in examples
2. **With args → Test mode**: Test specific inputs
3. **No Spring context**: Fast startup, isolated testing
4. **No database**: Works offline

---

## Examples

```bash
# Quick PDF parsing test
mvn compile exec:java -Dexec.mainClass="com.diligence.extraction.DocumentParserMain" \
  -Dexec.args="sample.pdf"

# Test ABN validation logic
mvn compile exec:java -Dexec.mainClass="com.diligence.rules.RuleServiceMain" \
  -Dexec.args="12345678901"

# Verify name matching algorithm
mvn compile exec:java -Dexec.mainClass="com.diligence.tools.ABNLookupServiceMain" \
  -Dexec.args="50110219460"
```

---

## When to Use

✅ **Good for:**
- Debugging individual modules
- Testing parsing/validation logic
- Quick verification without full app
- Learning how services work
- Integration testing components

❌ **Not for:**
- Full end-to-end workflows (use Spring Boot app)
- Testing with real database
- Multi-service coordination
- HTTP API testing

---

## Adding New Mains

To add standalone testing for another module:

1. Create `ServiceNameMain.java` in the service package
2. Add `public static void main(String[] args)` method
3. Support demo mode (no args) and test mode (with args)
4. Print clear output with results

Example structure:
```java
public class MyServiceMain {
    public static void main(String[] args) {
        System.out.println("=== My Service - Standalone Mode ===\n");
        
        MyService service = new MyService();
        
        if (args.length == 0) {
            demoMode(service);
        } else {
            testMode(service, args[0]);
        }
    }
    
    private static void demoMode(MyService service) {
        // Test with built-in examples
    }
    
    private static void testMode(MyService service, String input) {
        // Test with user-provided input
    }
}
```
