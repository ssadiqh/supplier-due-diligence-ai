# ABN Lookup API Credentials Setup Guide

## Official ABN Lookup API (Recommended)

### Step 1: Register for Free GUID
1. Go to: https://abr.business.gov.au/Documentation/WebServiceRegistration
2. Review the **Web Services Agreement**: https://abr.business.gov.au/Tools/WebServicesAgreement
3. Accept the agreement
4. Complete the registration form:
   - Organisation name
   - Use case: "Australian Supplier Due Diligence verification"
   - Intended use: "Validating supplier ABNs and retrieving publicly available business registration details"
   - Contact email

### Step 2: Receive GUID via Email
- ABN Lookup processes applications quickly (usually within 1-2 days)
- You'll receive an email with:
  - **GUID**: Your free authentication credential (no API key, no password)
  - API endpoints and documentation
  - No rate limits published (free service)
  - JSON and XML endpoint options

### Step 3: Get Your GUID
Once approved, ABN Lookup sends:
```
GUID: 12345678-abcd-ef00-1234-567890abcdef
(Example - your GUID will be unique)

Endpoint: https://abr.business.gov.au/
JSON endpoint: /json/AbnDetails.aspx?abn={abn}&guid={guid}
XML endpoint: /abrxmlsearch/AbrXmlSearch.asmx/SearchByABNv202001?searchString={abn}&authenticationGuid={guid}
```

---

## Option 2: Test with Mock Data (Development)

If you want to test the system now WITHOUT waiting for ABN Lookup registration:

### Step 1: Run Without GUID
```bash
# Simply don't set ABN_LOOKUP_GUID environment variable

# Verify it's NOT set:
echo $ABN_LOOKUP_GUID  # Should be empty
```

### Step 2: Run Application
```bash
cd casework-service
mvn spring-boot:run
```

### Step 3: Observe Behavior
When ABN_LOOKUP_GUID is not configured:
- Application logs: `"ABN_LOOKUP_GUID not configured, using mock data"`
- ABN lookups return mock data (test companies)
- All tests pass (26/26) using mock fallback
- No network calls to ABN Lookup API

**When you get your GUID**, simply set it and application will call the real API.

---

## Option 3: Third-Party ABR Data Providers

If you don't want to wait for ASIC approval, use:

### 1. Data.gov.au API
- Managed by Australian Data Commissioner
- Free tier available
- URL: https://data.gov.au/

### 2. Commercial Providers
- **Dun & Bradstreet** - ABN lookup service
- **Creditsafe** - Business intelligence
- **Equifax** - Credit/compliance data
- **Mycorp** - Company data

These provide:
- Faster approval (days vs weeks)
- Higher rate limits
- Additional data fields
- Premium support

---

## Setting Up Your GUID

### On Your Local Machine

#### Linux/Mac:
```bash
# 1. Add to ~/.bash_profile or ~/.zshrc
echo 'export ABN_LOOKUP_GUID="your-guid-from-abr-email"' >> ~/.bash_profile
source ~/.bash_profile

# 2. Verify
echo $ABN_LOOKUP_GUID

# 3. Run app
cd casework-service
mvn spring-boot:run
```

#### Windows PowerShell:
```powershell
# 1. Set permanent environment variable
[Environment]::SetEnvironmentVariable("ABN_LOOKUP_GUID", "your-guid-from-abr-email", "User")

# 2. Close and reopen PowerShell

# 3. Verify
Write-Output $env:ABN_LOOKUP_GUID

# 4. Run app
cd casework-service
mvn spring-boot:run
```

#### Windows Command Prompt:
```cmd
# 1. Set environment variable
setx ABN_LOOKUP_GUID "your-guid-from-abr-email"

# 2. Close and reopen Command Prompt

# 3. Verify
echo %ABN_LOOKUP_GUID%

# 4. Run app
cd casework-service
mvn spring-boot:run
```

### In Docker
```dockerfile
FROM openjdk:20-slim
WORKDIR /app
COPY target/supplier-due-diligence-ai-0.1.0-SNAPSHOT.jar .
# GUID can be passed at runtime, don't hardcode in image
ENTRYPOINT ["java", "-jar", "supplier-due-diligence-ai-0.1.0-SNAPSHOT.jar"]
```

```bash
# Pass GUID at runtime (recommended - never commit GUID to image)
docker run -e ABN_LOOKUP_GUID="your-guid-from-abr-email" \
  -p 8080:8080 supplier-dd-api
```

### In Kubernetes
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: abr-credentials
type: Opaque
stringData:
  guid: "your-guid-from-abr-email"  # Store GUID securely in secret
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: supplier-dd-api
spec:
  template:
    spec:
      containers:
      - name: api
        image: supplier-dd-api:latest
        env:
        - name: ABN_LOOKUP_GUID
          valueFrom:
            secretKeyRef:
              name: abr-credentials
              key: guid
        ports:
        - containerPort: 8080
```

### In application.yml
```yaml
abn:
  lookup:
    base-url: https://abr.business.gov.au  # Official ABN Lookup service
    guid: ${ABN_LOOKUP_GUID:}  # Reads from environment variable (empty = mock fallback)
```

---

## Testing Your Setup

### 1. Verify Environment Variable is Set
```bash
# Linux/Mac
echo $ABN_API_KEY

# Windows
echo %ABN_API_KEY%
```

Should output your API key, not empty.

### 2. Run the Application
```bash
mvn spring-boot:run
```

Look for log output:
```
[main] c.d.tools.ABNLookupService     : Looking up ABN: 12345678901
```

### 3. Test with Real ABN
Get a real ABN to test. Examples:
- **Qantas**: 16 009 661 901
- **ANZ Bank**: 11 005 357 522
- **Woolworths**: 88 000 014 675

```bash
# Call the API
curl -X POST http://localhost:8080/api/cases/{caseId}/verify-supplier

# Expected response (with real credentials):
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "toolName": "ABN_LOOKUP",
  "toolType": "SUPPLIER_VERIFICATION",
  "success": true,
  "evidence": "ABN Lookup Tool Execution:\n- ABN: 16009661901\n...",
  "executedAt": "2026-09-25T21:30:00"
}
```

### 4. Check Logs for Errors
```bash
# Look for:
# - "Looking up ABN: 16009661901"
# - "ABN lookup successful: 16009661901"
# - OR "Error calling ABN API for ..."

# If you see: "ABN_API_KEY not configured, using mock data"
# Then your environment variable is NOT set
```

---

## Debugging

### Problem: "ABN_LOOKUP_GUID not configured, using mock data"

**Cause:** Environment variable not set (or not in PATH for application)

**Solution:**
```bash
# Verify variable is set
echo $ABN_LOOKUP_GUID  # Should print your GUID, not empty

# If empty, set it:
export ABN_LOOKUP_GUID="your-guid-from-abr-email"

# Restart application:
mvn spring-boot:run

# On Windows, use:
setx ABN_LOOKUP_GUID "your-guid-from-abr-email"
# Then close and reopen PowerShell/Command Prompt
```

### Problem: "Invalid GUID" or API returns error

**Cause:** Incorrect GUID or GUID doesn't have access

**Solution:**
```bash
# 1. Verify GUID is correct (check ABN Lookup registration email)
# 2. Copy/paste GUID exactly (watch for extra spaces)
# 3. Visit https://abr.business.gov.au/ to test manually
# 4. If GUID is correct but API still fails, contact ABN Lookup support
```

### Problem: "Connection refused" or timeout

**Cause:** Network issue or ABN Lookup service down

**Solution:**
```bash
# 1. Check internet connection
# 2. Test ABN Lookup manually: https://abr.business.gov.au/json/AbnDetails.aspx?abn=51835430479&guid=YOUR_GUID
# 3. Check firewall isn't blocking outbound HTTPS
# 4. Wait and retry (ABN Lookup might have brief outages)
```

### Problem: "Connection timeout"

**Cause:** Network issue or ASIC API down

**Solution:**
```bash
# 1. Check internet connection
# 2. Verify ABR API is up: curl https://api.abr.business.gov.au/v1/
# 3. Check firewall isn't blocking requests
# 4. Wait and retry (ASIC might have brief outages)
```

---

## What Happens With/Without GUID

### WITH Real GUID (ABN_LOOKUP_GUID set):
```
ABN Lookup Request (e.g., 16009661901)
    ↓
Validate: matches \d{11}? ✓
    ↓
Call REAL ABN Lookup API: https://abr.business.gov.au/json/AbnDetails.aspx?abn=16009661901&guid={guid}
    ↓
GUID authentication: Query parameter guid={your_real_guid}
    ↓
Response: {"EntityName": "QANTAS AIRWAYS LIMITED", "EntityStatus": "Active", ...}
    ↓
ToolResult saved with REAL data
    ↓
Success: true, Evidence: "Name matches registry - VERIFIED"
```

### WITHOUT GUID (DEFAULT):
```
ABN Lookup Request (e.g., 12345678901)
    ↓
Validate: matches \d{11}? ✓
    ↓
Check ABN_LOOKUP_GUID: empty!
    ↓
Fall back to MOCK data
    ↓
ToolResult saved with MOCK data
    ↓
Success: true, Evidence: "Test Company Pty Ltd"
```

---

## Production Checklist

- [ ] Applied for ABN API access with ASIC
- [ ] Received API key and credentials
- [ ] Set `ABN_API_KEY` environment variable
- [ ] Tested with real ABN (Qantas, ANZ, Woolworths)
- [ ] Verified logs show "ABN lookup successful"
- [ ] Verified ToolResult shows real business names
- [ ] Checked rate limits (ABR API may have limits)
- [ ] Implemented monitoring/alerting for API failures
- [ ] Set up circuit breaker for API outages
- [ ] Documented API key rotation process

---

## Support

### For ASIC API Issues:
- Email: data@asic.gov.au
- Portal: https://www.asic.gov.au/online-services/

### For Our Application:
- Check logs: `mvn spring-boot:run 2>&1 | grep ABN`
- Verify config: `mvn spring-boot:run 2>&1 | grep "abn.lookup"`
- Test endpoint: `curl http://localhost:8080/api/cases`

---

## Next Steps

1. **Now:** Set up mock/test key for development
2. **Week 1-2:** Apply for real ASIC credentials
3. **After Approval:** Swap mock key for real key
4. **Production:** Use real credentials in secure vault (AWS Secrets Manager, Azure KeyVault, etc.)
