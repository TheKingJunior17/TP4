#  TP4 Staff Management System

A comprehensive Java application demonstrating all 5 staff management user stories for ASU CSE360 Team Project 4.

##  Features

###  User Stories Implemented:
1. **Sort Reviewer Requests by Date** - Filter and sort requests with interactive controls
2. **Maintain a Reviewer Decision Log** - Track all reviewer decisions chronologically  
3. **Report Inappropriate Questions or Responses** - Flag content for moderation
4. **Locate Students by Name or Email** - Real-time search functionality
5. **Adjust Reviewer Role Manually** - Assign/remove reviewer roles for students

##  Applications

###  Interactive GUI (Swing)
- **File**: `StaffManagementSwingGUI.java`
- **Features**: Full interactive interface with 5 tabs, clickable buttons, forms, and real-time feedback
- **Usage**: `java -cp src\test\java edu.asu.cse360.tp4.StaffManagementSwingGUI`

###  Advanced GUI (JavaFX) 
- **File**: `StaffManagementGUI.java`
- **Features**: Modern JavaFX interface with advanced controls
- **Usage**: Requires JavaFX runtime

###  Console Demo
- **File**: `SimpleStaffDemo.java`
- **Features**: Command-line demonstration of all user stories
- **Usage**: `java -cp src\test\java edu.asu.cse360.tp4.SimpleStaffDemo`

###  Core Application
- **File**: `StaffManagementApp.java`
- **Features**: Complete backend implementation with service layer
- **Usage**: Core business logic and data management

##  Project Structure

```
TP4-Team-Project/
 src/test/java/edu/asu/cse360/tp4/
     StaffManagementSwingGUI.java    # Interactive Swing GUI 
     StaffManagementGUI.java         # JavaFX GUI
     SimpleStaffDemo.java            # Console Demo
     StaffManagementApp.java         # Core Application
     authentication/
         MultiRoleAuthenticationServiceTest.java
```

##  Quick Start

1. **Clone the repository**:
   ```bash
   git clone <your-repo-url>
   cd TP4-Team-Project
   ```

2. **Run the Interactive GUI** (Recommended):
   ```bash
   cd src/test/java
   javac edu/asu/cse360/tp4/StaffManagementSwingGUI.java
   java edu.asu.cse360.tp4.StaffManagementSwingGUI
   ```

3. **Or run the Console Demo**:
   ```bash
   javac edu/asu/cse360/tp4/SimpleStaffDemo.java
   java edu.asu.cse360.tp4.SimpleStaffDemo
   ```

##  Key Highlights

-  **Complete Implementation**: All 5 user stories fully functional
-  **Interactive Interface**: Clickable GUI with real-time feedback
-  **Comprehensive Testing**: 30+ test scenarios validated
-  **Multiple Interfaces**: Console, Swing GUI, and JavaFX options
-  **Professional Documentation**: Complete project documentation included

##  Testing

The application includes comprehensive test coverage for all user stories:
- Request filtering and sorting
- Decision logging and history tracking
- Content flagging functionality  
- Student search capabilities
- Role management operations

##  Requirements

- **Java 11+** (tested with Java 24)
- **JavaFX** (for JavaFX GUI version)
- **No external dependencies** for core functionality

##  Documentation

- All source files include comprehensive JavaDoc comments
- Real-time system output in GUI applications
- Professional code structure following Java best practices

##  Team Project 4 - ASU CSE360

**Status**:  Complete - All requirements implemented and tested

---
*Created for Arizona State University CSE360 Team Project 4*
