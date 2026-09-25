# ABN API Credentials Setup Guide

## Option 1: Direct ABR API (ASIC)

### Step 1: Apply with ASIC
1. Go to: https://www.asic.gov.au/online-services/access-to-asic-data/asic-download-data/access-the-abr-data-guide/
2. Click "Request ABR API Access"
3. Fill out the application form:
   - Organisation name
   - Use case (supplier verification, due diligence)
   - Expected volume (queries/month)
   - Contact details

### Step 2: Wait for Approval
- ASIC typically approves within 1-2 weeks
- You'll receive an email with:
  - API key / credentials
  - API documentation
  - Rate limits (usually 10-100 calls/second)
  - SLA requirements

### Step 3: Get Your API Key
Once approved, ASIC sends:
```
API Endpoint: https://api.abr.business.gov.au/v1/
API Key: ABC123XYZ789...  (unique to your organisation)
Authentication: Bearer token in Authorization header
```

---

## Option 2: Test with a Mock Key (Development)

If you want to test the system now WITHOUT waiting for ASIC approval:

### Step 1: Create a Test API Key
```bash
# Use any string as a test key (won't call real API, just demonstrates setup)
export ABN_API_KEY="test_key_12345_abcdef"
```

### Step 2: Set Up Environment
```bash
# Linux/Mac
export ABN_API_KEY="test_key_12345_abcdef"
echo $ABN_API_KEY  # Verify it's set

# Windows PowerShell
$env:ABN_API_KEY = "test_key_12345_abcdef"
Write-Output $env:ABN_API_KEY  # Verify
```

### Step 3: Run Application
```bash
mvn spring-boot:run
```

**Note:** With test key, ABN lookups will call the REAL ABR API but will fail (auth error). Our code catches this and returns mock data.

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

## Setting Up Your API Key

### On Your Local Machine

#### Linux/Mac:
```bash
# 1. Add to ~/.bash_profile or ~/.zshrc
echo 'export ABN_API_KEY="your_actual_api_key_here"' >> ~/.bash_profile
source ~/.bash_profile

# 2. Verify
echo $ABN_API_KEY

# 3. Run app
mvn spring-boot:run
```

#### Windows PowerShell:
```powershell
# 1. Set permanent environment variable
[Environment]::SetEnvironmentVariable("ABN_API_KEY", "your_actual_api_key_here", "User")

# 2. Close and reopen PowerShell

# 3. Verify
Write-Output $env:ABN_API_KEY

# 4. Run app
mvn spring-boot:run
```

#### Windows Command Prompt:
```cmd
# 1. Set environment variable
setx ABN_API_KEY "your_actual_api_key_here"

# 2. Close and reopen Command Prompt

# 3. Verify
echo %ABN_API_KEY%

# 4. Run app
mvn spring-boot:run
```

### In Docker
```dockerfile
FROM openjdk:20-slim
WORKDIR /app
COPY target/supplier-due-diligence-ai-0.1.0-SNAPSHOT.jar .
ENV ABN_API_KEY="your_api_key_here"
ENTRYPOINT ["java", "-jar", "supplier-due-diligence-ai-0.1.0-SNAPSHOT.jar"]
```

```bash
# Or pass at runtime
docker run -e ABN_API_KEY="your_api_key" -p 8080:8080 supplier-dd-api
```

### In Kubernetes
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: abr-credentials
type: Opaque
stringData:
  api-key: "your_actual_api_key_here"
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
        - name: ABN_API_KEY
          valueFrom:
            secretKeyRef:
              name: abr-credentials
              key: api-key
        ports:
        - containerPort: 8080
```

### In application.yml
```yaml
abn:
  lookup:
    url: https://api.abr.business.gov.au/v1/
    api-key: ${ABN_API_KEY:}  # Reads from environment variable
    timeout: 5000
    max-retries: 3
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

### Problem: "ABN_API_KEY not configured, using mock data"

**Cause:** Environment variable not set

**Solution:**
```bash
# Verify variable is set
echo $ABN_API_KEY  # Should print your key, not empty

# If empty, set it:
export ABN_API_KEY="your_key_here"

# Restart application:
mvn spring-boot:run
```

### Problem: "401 Unauthorized"

**Cause:** Invalid API key

**Solution:**
```bash
# 1. Verify key is correct (check ASIC email)
# 2. Verify key hasn't expired
# 3. Contact ASIC support if key is valid but rejected
```

### Problem: "403 Forbidden"

**Cause:** API key doesn't have permission for this endpoint

**Solution:**
```bash
# 1. Verify your ASIC approval includes ABR API access
# 2. Check if rate limits exceeded
# 3. Contact ASIC to update permissions
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

## What Happens With/Without API Key

### WITH Real API Key (ABN_API_KEY set):
```
ABN Lookup Request (e.g., 16009661901)
    ↓
Validate: matches \d{11}? ✓
    ↓
Call REAL ABR API: https://api.abr.business.gov.au/v1/organisation/16009661901
    ↓
Authorization header: Bearer {your_real_api_key}
    ↓
ABR Response: {"businessName": "QANTAS AIRWAYS LIMITED", "businessStatus": "Active", ...}
    ↓
ToolResult saved with REAL data
    ↓
Success: true, Evidence: "Name matches registry - VERIFIED"
```

### WITHOUT API Key (DEFAULT):
```
ABN Lookup Request (e.g., 12345678901)
    ↓
Validate: matches \d{11}? ✓
    ↓
Check ABN_API_KEY: empty!
    ↓
Fall back to MOCK data
    ↓
ToolResult saved with MOCK data
    ↓
Success: true, Evidence: "Using mock data for Phase 4 testing"
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
