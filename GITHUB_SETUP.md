# GitHub Repository Setup Instructions

## Creating Your Team's TP4 Repository

### Step 1: Repository Creation
1. Create a **private** GitHub repository named `TP4-Team-Project`
2. Add all team members as collaborators with write access
3. Add your grader as a collaborator with read access
4. **IMPORTANT:** Keep repository private to maintain academic integrity

### Step 2: Clone and Setup
```bash
# Clone this template repository
git clone <this-template-url> TP4-Team-Project
cd TP4-Team-Project

# Initialize as your team's repository
rm -rf .git
git init
git add .
git commit -m "Initial TP4 project setup"

# Add your team's remote repository
git remote add origin https://github.com/[YOUR_TEAM]/TP4-Team-Project.git
git push -u origin main
```

### Step 3: Team Collaboration Setup
```bash
# Each team member clones the repository
git clone https://github.com/[YOUR_TEAM]/TP4-Team-Project.git
cd TP4-Team-Project

# Verify setup works
./setup.sh
```

### Step 4: Development Workflow

**Branch Strategy:**
- `main`: Production-ready code only
- `develop`: Integration branch for team collaboration
- `feature/*`: Individual feature branches for each team member

**Example Workflow:**
```bash
# Create feature branch
git checkout -b feature/student-dashboard

# Work on your implementation
# ... make changes ...

# Commit and push
git add .
git commit -m "Implement student dashboard analytics"
git push origin feature/student-dashboard

# Create pull request for code review
# Merge after team approval
```

### Step 5: Grader Access Configuration

**Add Grader as Collaborator:**
1. Go to repository Settings > Manage access
2. Click "Invite a collaborator"
3. Add grader's GitHub username with **Read** permissions
4. Send invitation

**Verification Instructions for Grader:**
```bash
# Grader can clone and verify
git clone https://github.com/[YOUR_TEAM]/TP4-Team-Project.git
cd TP4-Team-Project
./setup.sh

# Run comprehensive tests
./gradlew test jacocoTestReport

# View results
open build/reports/tests/test/index.html
open build/reports/jacoco/test/html/index.html
```

### Step 6: Continuous Integration (Optional)

**GitHub Actions Workflow** (`.github/workflows/ci.yml`):
```yaml
name: TP4 CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    - name: Run tests
      run: ./gradlew test jacocoTestReport
    - name: Upload coverage reports
      uses: codecov/codecov-action@v3
```

## Repository Structure Verification

After setup, your repository should contain:

- ✅ Complete source code with all role implementations
- ✅ Comprehensive test suite (200+ tests)
- ✅ Professional documentation and README
- ✅ Build system with quality gates
- ✅ Assignment template with all deliverables
- ✅ Screencast planning and organization
- ✅ Team collaboration documentation

## Academic Integrity Compliance

**CRITICAL REQUIREMENTS:**
- Repository MUST remain private throughout the semester
- Only team members and graders should have access
- Public repositories will result in academic integrity violations
- All code must be original team work building on provided materials

## Submission Checklist

Before final submission, verify:

- [ ] All team members can access the repository
- [ ] Grader has been added with read permissions
- [ ] All tests pass (`./gradlew test`)
- [ ] Documentation is complete and professional
- [ ] Screencasts are uploaded and accessible
- [ ] Repository is private with proper access controls
- [ ] Assignment template is complete and accurate

---

**Support:** Contact any team member if you encounter setup issues.
**Repository:** https://github.com/[YOUR_TEAM]/TP4-Team-Project (Private)
**Due Date:** [Insert assignment due date]

© 2025 ASU CSE 360 - Team Project Phase 4