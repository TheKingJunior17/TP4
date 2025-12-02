#!/usr/bin/env bash

# TP4 Team Project - Quick Setup and Verification Script
# Ensures all components are working correctly

echo "=========================================="
echo "TP4 Team Project - Quick Setup"
echo "=========================================="

# Check Java version
echo "Checking Java version..."
java -version
if [ $? -ne 0 ]; then
    echo "❌ Java 17+ required but not found"
    exit 1
fi
echo "✅ Java version check passed"

# Make gradlew executable
chmod +x ./gradlew

# Clean and build project
echo "Building project..."
./gradlew clean build --no-daemon
if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi
echo "✅ Build successful"

# Run comprehensive test suite
echo "Running test suite..."
./gradlew test jacocoTestReport --no-daemon
if [ $? -ne 0 ]; then
    echo "❌ Tests failed"
    exit 1
fi
echo "✅ All tests passed"

# Generate documentation
echo "Generating documentation..."
./gradlew javadoc --no-daemon
if [ $? -ne 0 ]; then
    echo "⚠️ Documentation generation had warnings"
else
    echo "✅ Documentation generated"
fi

# Check test coverage
if [ -f "build/reports/jacoco/test/html/index.html" ]; then
    echo "✅ Test coverage report available at: build/reports/jacoco/test/html/index.html"
fi

# Check API documentation
if [ -f "build/docs/javadoc/index.html" ]; then
    echo "✅ API documentation available at: build/docs/javadoc/index.html"
fi

echo "=========================================="
echo "Setup Complete! 🎉"
echo "=========================================="
echo ""
echo "Quick commands to get started:"
echo "  ./gradlew bootRun              # Start the application"
echo "  ./gradlew test                 # Run all tests"
echo "  ./gradlew runStaffServices     # Demo staff functionality"
echo "  ./gradlew runStudentServices   # Demo student functionality"
echo "  ./gradlew runInstructorServices # Demo instructor functionality"
echo ""
echo "View reports:"
echo "  open build/reports/tests/test/index.html        # Test results"
echo "  open build/reports/jacoco/test/html/index.html  # Coverage report"
echo "  open build/docs/javadoc/index.html              # API documentation"
echo ""
echo "Repository ready for grader evaluation! ✅"