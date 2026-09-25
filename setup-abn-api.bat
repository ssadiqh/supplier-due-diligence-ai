@echo off
REM Quick setup script for ABN API credentials (Windows)

setlocal enabledelayedexpansion

echo ===================================
echo ABN API Credentials Setup
echo ===================================
echo.

REM Check if API key already set
if "%ABN_API_KEY%"=="" (
    echo WARNING: ABN_API_KEY environment variable not set
    echo.
    echo Choose an option:
    echo 1) Set a TEST key (for development)
    echo 2) Set a REAL ASIC API key
    echo 3) Skip setup (use mock data)
    echo.

    set /p choice="Enter choice (1-3): "

    if "!choice!"=="1" (
        echo.
        echo Setting TEST key for development...
        set ABN_API_KEY=test_key_development_only
        echo OK: ABN_API_KEY=test_key_development_only (temporary for this session)
        echo.
        echo To make this PERMANENT:
        echo   1. Right-click "This PC" or "Computer"
        echo   2. Click "Properties"
        echo   3. Click "Advanced system settings"
        echo   4. Click "Environment Variables"
        echo   5. Under "User variables", click "New"
        echo   6. Variable name: ABN_API_KEY
        echo   7. Variable value: test_key_development_only
        echo   8. Click OK and restart PowerShell/Command Prompt
    ) else if "!choice!"=="2" (
        echo.
        set /p api_key="Enter your ASIC ABN API key: "
        if "!api_key!"=="" (
            echo ERROR: API key cannot be empty
            exit /b 1
        )
        set ABN_API_KEY=!api_key!
        echo OK: ABN_API_KEY is set
        echo.
        echo To make this PERMANENT (one-time setup):
        echo   setx ABN_API_KEY "!api_key!"
        echo   Then restart PowerShell/Command Prompt
    ) else if "!choice!"=="3" (
        echo Skipping setup. Application will use mock data.
        exit /b 0
    ) else (
        echo ERROR: Invalid choice
        exit /b 1
    )
) else (
    echo OK: ABN_API_KEY is already set
    echo   Value: %ABN_API_KEY:~0,20%... (first 20 chars)
)

echo.
echo ===================================
echo Verifying Setup
echo ===================================
echo.

REM Verify key is set
if "%ABN_API_KEY%"=="" (
    echo ERROR: ABN_API_KEY not set
    exit /b 1
)

echo OK: ABN_API_KEY is configured
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
echo To make ABN_API_KEY permanent, run:
echo   setx ABN_API_KEY "%ABN_API_KEY%"
echo Then restart PowerShell/Command Prompt
