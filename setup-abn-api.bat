@echo off
REM Quick setup script for ABN API credentials (Windows)

setlocal enabledelayedexpansion

echo ===================================
echo ABN API Credentials Setup
echo ===================================
echo.

REM Check if GUID already set
if "%ABN_LOOKUP_GUID%"=="" (
    echo WARNING: ABN_LOOKUP_GUID environment variable not set
    echo.
    echo Choose an option:
    echo 1) Set a TEST key (for development)
    echo 2) Set a REAL ASIC API key
    echo 3) Skip setup (use mock data)
    echo.

    set /p choice="Enter choice (1-3): "

    if "!choice!"=="1" (
        echo.
        echo Skipping GUID setup. Application will use mock data for testing.
        echo To add real credentials later:
        echo   setx ABN_LOOKUP_GUID "your-guid-from-abr-email"
        exit /b 0
    ) else if "!choice!"=="2" (
        echo.
        set /p guid="Enter your ABN Lookup GUID (from abr.business.gov.au): "
        if "!guid!"=="" (
            echo ERROR: GUID cannot be empty
            exit /b 1
        )
        set ABN_LOOKUP_GUID=!guid!
        echo OK: ABN_LOOKUP_GUID is set
        echo.
        echo To make this PERMANENT (one-time setup):
        echo   setx ABN_LOOKUP_GUID "!guid!"
        echo   Then restart PowerShell/Command Prompt
    ) else if "!choice!"=="3" (
        echo Skipping setup. Application will use mock data.
        exit /b 0
    ) else (
        echo ERROR: Invalid choice
        exit /b 1
    )
) else (
    echo OK: ABN_LOOKUP_GUID is already set
    echo   Value: %ABN_LOOKUP_GUID:~0,20%... (first 20 chars)
)

echo.
echo ===================================
echo Verifying Setup
echo ===================================
echo.

REM Verify GUID is set (if user chose option 2)
REM Skip this check - GUID is optional, mock will be used if not set
REM if "%ABN_LOOKUP_GUID%"=="" (
REM    echo WARNING: ABN_LOOKUP_GUID not set - will use mock data
REM )

if not "%ABN_LOOKUP_GUID%"=="" (
    echo OK: ABN_LOOKUP_GUID is configured
) else (
    echo INFO: ABN_LOOKUP_GUID not set - application will use mock data
)
echo.

REM Check if application.yml exists
if not exist "casework-service\src\main\resources\application.yml" (
    echo ERROR: application.yml not found
    exit /b 1
)

echo OK: application.yml found
echo.

REM Check if ABNLookupService exists
if not exist "casework-service\src\main\java\com\diligence\tools\ABNLookupService.java" (
    echo ERROR: ABNLookupService not found
    exit /b 1
)

echo OK: ABNLookupService found
echo.

echo ===================================
echo Next Steps
echo ===================================
echo.
echo 1. Start the application:
echo    cd casework-service
echo    mvn spring-boot:run
echo.
echo 2. Create a test case:
echo    curl -X POST http://localhost:8080/api/cases ^
echo      -H "Content-Type: application/json" ^
echo      -d "{\"supplierName\":\"Qantas Airways\",\"requestedBy\":\"analyst@company.com\",\"supplierAbn\":\"16009661901\"}"
echo.
echo 3. Verify supplier (ABN lookup):
echo    curl -X POST http://localhost:8080/api/cases/{caseId}/verify-supplier
echo.
echo 4. Check results:
echo    curl http://localhost:8080/api/cases/{caseId}/tool-results
echo.
echo ===================================
echo OK: Setup complete!
echo ===================================
echo.
if not "%ABN_LOOKUP_GUID%"=="" (
    echo To make ABN_LOOKUP_GUID permanent, run:
    echo   setx ABN_LOOKUP_GUID "%ABN_LOOKUP_GUID%"
    echo Then restart PowerShell/Command Prompt
)
