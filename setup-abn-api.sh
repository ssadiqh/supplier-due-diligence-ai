#!/bin/bash
# Quick setup script for ABN API credentials

set -e

echo "==================================="
echo "ABN API Credentials Setup"
echo "==================================="
echo ""

# Check if API key already set
if [ -z "$ABN_API_KEY" ]; then
    echo "⚠️  ABN_API_KEY environment variable not set"
    echo ""
    echo "Do you want to:"
    echo "1) Set a TEST key (for development)"
    echo "2) Set a REAL ASIC API key"
    echo "3) Skip setup (use mock data)"
    echo ""
    read -p "Enter choice (1-3): " choice

    case $choice in
        1)
            echo ""
            echo "Setting TEST key for development..."
            export ABN_API_KEY="test_key_development_only"
            echo "✓ ABN_API_KEY=test_key_development_only (temporary)"
            echo ""
            echo "To make this permanent, add to ~/.bash_profile or ~/.zshrc:"
            echo "  export ABN_API_KEY='test_key_development_only'"
            ;;
        2)
            echo ""
            read -p "Enter your ASIC ABN API key: " api_key
            if [ -z "$api_key" ]; then
                echo "❌ API key cannot be empty"
                exit 1
            fi
            export ABN_API_KEY="$api_key"
            echo "✓ ABN_API_KEY is set (${#api_key} characters)"
            echo ""
            echo "To make this permanent, run:"
            echo "  echo \"export ABN_API_KEY='$api_key'\" >> ~/.bash_profile"
            echo "  source ~/.bash_profile"
            ;;
        3)
            echo "Skipping setup. Application will use mock data."
            exit 0
            ;;
        *)
            echo "Invalid choice"
            exit 1
            ;;
    esac
else
    echo "✓ ABN_API_KEY is already set"
    echo "  Value: ${ABN_API_KEY:0:20}... (first 20 chars)"
fi

echo ""
echo "==================================="
echo "Verifying Setup"
echo "==================================="
echo ""

# Verify key is set
if [ -z "$ABN_API_KEY" ]; then
    echo "❌ ABN_API_KEY not set"
    exit 1
fi

echo "✓ ABN_API_KEY is configured"
echo ""

# Check if application.yml exists
if [ ! -f "casework-service/src/main/resources/application.yml" ]; then
    echo "❌ application.yml not found"
    exit 1
fi

echo "✓ application.yml found"
echo ""

# Check if ABNLookupService exists
if [ ! -f "casework-service/src/main/java/com/diligence/tools/ABNLookupService.java" ]; then
    echo "❌ ABNLookupService not found"
    exit 1
fi

echo "✓ ABNLookupService found"
echo ""

echo "==================================="
echo "Next Steps"
echo "==================================="
echo ""
echo "1. Start the application:"
echo "   cd casework-service"
echo "   mvn spring-boot:run"
echo ""
echo "2. Create a test case:"
echo "   curl -X POST http://localhost:8080/api/cases \\"
echo "     -H 'Content-Type: application/json' \\"
echo "     -d '{\"supplierName\":\"Qantas Airways\",\"requestedBy\":\"analyst@company.com\",\"supplierAbn\":\"16009661901\"}'"
echo ""
echo "3. Verify supplier (ABN lookup):"
echo "   curl -X POST http://localhost:8080/api/cases/{caseId}/verify-supplier"
echo ""
echo "4. Check results:"
echo "   curl http://localhost:8080/api/cases/{caseId}/tool-results"
echo ""
echo "==================================="
echo "✓ Setup complete!"
echo "==================================="
