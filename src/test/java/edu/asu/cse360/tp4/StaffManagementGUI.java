package edu.asu.cse360.tp4;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Interactive GUI Application for TP4 Staff Management System
 * Click-able interface demonstrating all 5 user stories
 */
public class StaffManagementGUI extends Application {
    
    // Data storage
    private ObservableList<ReviewerRequest> requests = FXCollections.observableArrayList();
    private ObservableList<ReviewerDecision> decisions = FXCollections.observableArrayList();
    private ObservableList<Student> students = FXCollections.observableArrayList();
    private ObservableList<Question> questions = FXCollections.observableArrayList();
    private ObservableList<Answer> answers = FXCollections.observableArrayList();
    
    // UI Components
    private TabPane tabPane;
    private TextArea outputArea;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TP4 Staff Management System - Interactive GUI");
        
        // Initialize test data
        initializeTestData();
        
        // Create main layout
        BorderPane mainLayout = new BorderPane();
        
        // Create header
        Label header = new Label("🏫 TP4 Staff Management System");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        VBox headerBox = new VBox(header);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));
        headerBox.setStyle("-fx-background-color: #ecf0f1;");
        
        // Create tab pane for different user stories
        tabPane = new TabPane();
        tabPane.getTabs().addAll(
            createRequestFilterTab(),
            createDecisionHistoryTab(),
            createContentFlaggingTab(),
            createStudentSearchTab(),
            createRoleManagementTab()
        );
        
        // Create output area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(8);
        outputArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        outputArea.setText("🚀 TP4 Staff Management System Ready!\nClick on any tab above to interact with the system.\n");
        
        VBox outputBox = new VBox(new Label("📋 System Output:"), outputArea);
        outputBox.setPadding(new Insets(10));
        
        // Assemble layout
        mainLayout.setTop(headerBox);
        mainLayout.setCenter(tabPane);
        mainLayout.setBottom(outputBox);
        
        Scene scene = new Scene(mainLayout, 1000, 700);
        scene.getStylesheets().add("data:text/css," + getCSS());
        primaryStage.setScene(scene);
        primaryStage.show();
        
        log("Application started successfully! 🎉");
    }
    
    // User Story 1: Sort Reviewer Requests by Date
    private Tab createRequestFilterTab() {
        Tab tab = new Tab("📅 Filter Requests");
        tab.setClosable(false);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label title = new Label("User Story 1: Sort Reviewer Requests by Date");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Filter options
        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll(
            "Last 7 Days",
            "Last 30 Days", 
            "All Time",
            "Custom Range"
        );
        filterCombo.setValue("Last 7 Days");
        
        Button filterButton = new Button("🔍 Filter Requests");
        filterButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
        
        // Results table
        TableView<ReviewerRequest> requestTable = new TableView<>(requests);
        setupRequestTable(requestTable);
        
        filterButton.setOnAction(e -> {
            String filter = filterCombo.getValue();
            log("🔍 Filtering requests: " + filter);
            
            LocalDateTime startDate = null;
            LocalDateTime endDate = LocalDateTime.now();
            
            switch (filter) {
                case "Last 7 Days":
                    startDate = LocalDateTime.now().minusDays(7);
                    break;
                case "Last 30 Days":
                    startDate = LocalDateTime.now().minusDays(30);
                    break;
                case "All Time":
                    startDate = null;
                    endDate = null;
                    break;
            }
            
            ObservableList<ReviewerRequest> filtered = filterRequests(startDate, endDate);
            requestTable.setItems(filtered);
            
            log("✅ Found " + filtered.size() + " requests matching criteria");
            log("📊 Results sorted by date (newest first)");
        });
        
        HBox controls = new HBox(10, new Label("Filter:"), filterCombo, filterButton);
        controls.setAlignment(Pos.CENTER_LEFT);
        
        content.getChildren().addAll(title, controls, requestTable);
        tab.setContent(content);
        
        return tab;
    }
    
    // User Story 2: Maintain a Reviewer Decision Log
    private Tab createDecisionHistoryTab() {
        Tab tab = new Tab("📊 Decision History");
        tab.setClosable(false);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label title = new Label("User Story 2: Maintain a Reviewer Decision Log");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Add new decision form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        
        TextField studentField = new TextField();
        TextField staffField = new TextField("staff1");
        ComboBox<String> decisionCombo = new ComboBox<>();
        decisionCombo.getItems().addAll("APPROVED", "DENIED");
        decisionCombo.setValue("APPROVED");
        TextField reasonField = new TextField();
        
        Button addDecisionButton = new Button("➕ Add Decision");
        addDecisionButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        
        form.add(new Label("Student ID:"), 0, 0);
        form.add(studentField, 1, 0);
        form.add(new Label("Staff ID:"), 0, 1);
        form.add(staffField, 1, 1);
        form.add(new Label("Decision:"), 0, 2);
        form.add(decisionCombo, 1, 2);
        form.add(new Label("Reason:"), 0, 3);
        form.add(reasonField, 1, 3);
        form.add(addDecisionButton, 1, 4);
        
        // Decision history table
        TableView<ReviewerDecision> decisionTable = new TableView<>(decisions);
        setupDecisionTable(decisionTable);
        
        addDecisionButton.setOnAction(e -> {
            if (!studentField.getText().trim().isEmpty() && !reasonField.getText().trim().isEmpty()) {
                ReviewerDecision decision = new ReviewerDecision(
                    studentField.getText().trim(),
                    staffField.getText().trim(),
                    decisionCombo.getValue(),
                    reasonField.getText().trim(),
                    LocalDateTime.now()
                );
                
                decisions.add(0, decision); // Add at top (newest first)
                log("✅ Added new decision: " + decisionCombo.getValue() + " for " + studentField.getText());
                
                // Clear form
                studentField.clear();
                reasonField.clear();
            } else {
                log("❌ Please fill in Student ID and Reason fields");
            }
        });
        
        content.getChildren().addAll(title, form, new Separator(), decisionTable);
        tab.setContent(content);
        
        return tab;
    }
    
    // User Story 3: Report Inappropriate Questions or Responses
    private Tab createContentFlaggingTab() {
        Tab tab = new Tab("🚩 Flag Content");
        tab.setClosable(false);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label title = new Label("User Story 3: Report Inappropriate Questions or Responses");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Content display
        ListView<String> questionList = new ListView<>();
        ListView<String> answerList = new ListView<>();
        
        updateContentLists(questionList, answerList);
        
        // Flagging controls
        HBox questionControls = new HBox(10);
        TextField questionIdField = new TextField();
        questionIdField.setPromptText("Question ID");
        TextField questionReasonField = new TextField();
        questionReasonField.setPromptText("Flag reason");
        Button flagQuestionButton = new Button("🚩 Flag Question");
        Button unflagQuestionButton = new Button("✅ Unflag Question");
        
        flagQuestionButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        unflagQuestionButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        
        questionControls.getChildren().addAll(
            questionIdField, questionReasonField, flagQuestionButton, unflagQuestionButton
        );
        
        HBox answerControls = new HBox(10);
        TextField answerIdField = new TextField();
        answerIdField.setPromptText("Answer ID");
        TextField answerReasonField = new TextField();
        answerReasonField.setPromptText("Flag reason");
        Button flagAnswerButton = new Button("🚩 Flag Answer");
        Button unflagAnswerButton = new Button("✅ Unflag Answer");
        
        flagAnswerButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        unflagAnswerButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        
        answerControls.getChildren().addAll(
            answerIdField, answerReasonField, flagAnswerButton, unflagAnswerButton
        );
        
        // Event handlers
        flagQuestionButton.setOnAction(e -> {
            try {
                Long id = Long.parseLong(questionIdField.getText().trim());
                String reason = questionReasonField.getText().trim();
                boolean success = flagQuestion(id, reason, true);
                log(success ? "✅ Question " + id + " flagged successfully" 
                           : "❌ Question " + id + " not found");
                updateContentLists(questionList, answerList);
                questionIdField.clear();
                questionReasonField.clear();
            } catch (Exception ex) {
                log("❌ Invalid question ID");
            }
        });
        
        unflagQuestionButton.setOnAction(e -> {
            try {
                Long id = Long.parseLong(questionIdField.getText().trim());
                String reason = questionReasonField.getText().trim();
                boolean success = flagQuestion(id, reason, false);
                log(success ? "✅ Question " + id + " unflagged successfully"
                           : "❌ Question " + id + " not found");
                updateContentLists(questionList, answerList);
                questionIdField.clear();
                questionReasonField.clear();
            } catch (Exception ex) {
                log("❌ Invalid question ID");
            }
        });
        
        flagAnswerButton.setOnAction(e -> {
            try {
                Long id = Long.parseLong(answerIdField.getText().trim());
                String reason = answerReasonField.getText().trim();
                boolean success = flagAnswer(id, reason, true);
                log(success ? "✅ Answer " + id + " flagged successfully"
                           : "❌ Answer " + id + " not found");
                updateContentLists(questionList, answerList);
                answerIdField.clear();
                answerReasonField.clear();
            } catch (Exception ex) {
                log("❌ Invalid answer ID");
            }
        });
        
        unflagAnswerButton.setOnAction(e -> {
            try {
                Long id = Long.parseLong(answerIdField.getText().trim());
                String reason = answerReasonField.getText().trim();
                boolean success = flagAnswer(id, reason, false);
                log(success ? "✅ Answer " + id + " unflagged successfully"
                           : "❌ Answer " + id + " not found");
                updateContentLists(questionList, answerList);
                answerIdField.clear();
                answerReasonField.clear();
            } catch (Exception ex) {
                log("❌ Invalid answer ID");
            }
        });
        
        VBox questionSection = new VBox(5, new Label("📝 Questions:"), questionList, questionControls);
        VBox answerSection = new VBox(5, new Label("💬 Answers:"), answerList, answerControls);
        
        content.getChildren().addAll(title, questionSection, answerSection);
        tab.setContent(content);
        
        return tab;
    }
    
    // User Story 4: Locate Students by Name or Email
    private Tab createStudentSearchTab() {
        Tab tab = new Tab("🔍 Search Students");
        tab.setClosable(false);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label title = new Label("User Story 4: Locate Students by Name or Email");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Search controls
        HBox searchControls = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Enter name or email to search...");
        searchField.setPrefWidth(300);
        
        Button searchButton = new Button("🔍 Search");
        Button clearButton = new Button("🔄 Show All");
        
        searchButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        clearButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        
        searchControls.getChildren().addAll(searchField, searchButton, clearButton);
        
        // Results table
        TableView<Student> studentTable = new TableView<>(students);
        setupStudentTable(studentTable);
        
        // Search functionality
        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText().trim();
            log("🔍 Searching for: '" + searchTerm + "'");
            
            ObservableList<Student> results = searchStudents(searchTerm);
            studentTable.setItems(results);
            log("✅ Found " + results.size() + " matching students");
        });
        
        clearButton.setOnAction(e -> {
            searchField.clear();
            studentTable.setItems(students);
            log("🔄 Showing all " + students.size() + " students");
        });
        
        // Allow Enter key to search
        searchField.setOnAction(e -> searchButton.fire());
        
        content.getChildren().addAll(title, searchControls, studentTable);
        tab.setContent(content);
        
        return tab;
    }
    
    // User Story 5: Adjust Reviewer Role Manually
    private Tab createRoleManagementTab() {
        Tab tab = new Tab("👥 Manage Roles");
        tab.setClosable(false);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label title = new Label("User Story 5: Adjust Reviewer Role Manually");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Current roles display
        TableView<Student> roleTable = new TableView<>(students);
        setupRoleTable(roleTable);
        
        // Role management controls
        GridPane roleControls = new GridPane();
        roleControls.setHgap(10);
        roleControls.setVgap(10);
        
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student ID (e.g., student1)");
        TextField staffIdField = new TextField("staff1");
        TextField reasonField = new TextField();
        reasonField.setPromptText("Reason for role change");
        
        Button assignButton = new Button("➕ Assign Reviewer Role");
        Button removeButton = new Button("➖ Remove Reviewer Role");
        
        assignButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        removeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        roleControls.add(new Label("Student ID:"), 0, 0);
        roleControls.add(studentIdField, 1, 0);
        roleControls.add(new Label("Staff ID:"), 0, 1);
        roleControls.add(staffIdField, 1, 1);
        roleControls.add(new Label("Reason:"), 0, 2);
        roleControls.add(reasonField, 1, 2);
        
        HBox buttonBox = new HBox(10, assignButton, removeButton);
        roleControls.add(buttonBox, 1, 3);
        
        // Event handlers
        assignButton.setOnAction(e -> {
            String studentId = studentIdField.getText().trim();
            String staffId = staffIdField.getText().trim();
            String reason = reasonField.getText().trim();
            
            if (!studentId.isEmpty()) {
                boolean success = assignReviewerRole(studentId, staffId);
                if (success) {
                    log("✅ Assigned reviewer role to " + studentId + " by " + staffId);
                    log("📝 Reason: " + (reason.isEmpty() ? "No reason provided" : reason));
                    roleTable.refresh(); // Refresh table display
                } else {
                    log("❌ Failed to assign role (student not found or already has role)");
                }
                studentIdField.clear();
                reasonField.clear();
            }
        });
        
        removeButton.setOnAction(e -> {
            String studentId = studentIdField.getText().trim();
            String staffId = staffIdField.getText().trim();
            String reason = reasonField.getText().trim();
            
            if (!studentId.isEmpty()) {
                boolean success = removeReviewerRole(studentId, staffId);
                if (success) {
                    log("✅ Removed reviewer role from " + studentId + " by " + staffId);
                    log("📝 Reason: " + (reason.isEmpty() ? "No reason provided" : reason));
                    roleTable.refresh(); // Refresh table display
                } else {
                    log("❌ Failed to remove role (student not found or doesn't have role)");
                }
                studentIdField.clear();
                reasonField.clear();
            }
        });
        
        content.getChildren().addAll(title, roleTable, new Separator(), roleControls);
        tab.setContent(content);
        
        return tab;
    }
    
    // Helper methods for business logic
    
    private ObservableList<ReviewerRequest> filterRequests(LocalDateTime start, LocalDateTime end) {
        ObservableList<ReviewerRequest> filtered = FXCollections.observableArrayList();
        
        for (ReviewerRequest request : requests) {
            boolean include = true;
            
            if (start != null && request.getSubmissionDate().isBefore(start)) {
                include = false;
            }
            if (end != null && request.getSubmissionDate().isAfter(end)) {
                include = false;
            }
            
            if (include) {
                filtered.add(request);
            }
        }
        
        // Sort by date (newest first)
        filtered.sort((r1, r2) -> r2.getSubmissionDate().compareTo(r1.getSubmissionDate()));
        
        return filtered;
    }
    
    private boolean flagQuestion(Long questionId, String reason, boolean flag) {
        for (Question question : questions) {
            if (question.getId().equals(questionId)) {
                question.setFlagged(flag);
                question.setFlagReason(reason);
                return true;
            }
        }
        return false;
    }
    
    private boolean flagAnswer(Long answerId, String reason, boolean flag) {
        for (Answer answer : answers) {
            if (answer.getId().equals(answerId)) {
                answer.setFlagged(flag);
                answer.setFlagReason(reason);
                return true;
            }
        }
        return false;
    }
    
    private ObservableList<Student> searchStudents(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return FXCollections.observableArrayList(students);
        }
        
        ObservableList<Student> results = FXCollections.observableArrayList();
        String lowerSearch = searchTerm.toLowerCase();
        
        for (Student student : students) {
            if (student.getFirstName().toLowerCase().contains(lowerSearch) ||
                student.getLastName().toLowerCase().contains(lowerSearch) ||
                student.getFullName().toLowerCase().contains(lowerSearch) ||
                student.getEmail().toLowerCase().contains(lowerSearch)) {
                results.add(student);
            }
        }
        return results;
    }
    
    private boolean assignReviewerRole(String studentId, String staffId) {
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                if (student.hasReviewerRole()) {
                    return false; // Already has role
                }
                student.setHasReviewerRole(true);
                return true;
            }
        }
        return false; // Student not found
    }
    
    private boolean removeReviewerRole(String studentId, String staffId) {
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                if (!student.hasReviewerRole()) {
                    return false; // Doesn't have role
                }
                student.setHasReviewerRole(false);
                return true;
            }
        }
        return false; // Student not found
    }
    
    // UI Setup Methods
    
    private void setupRequestTable(TableView<ReviewerRequest> table) {
        TableColumn<ReviewerRequest, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentName()));
        nameCol.setPrefWidth(150);
        
        TableColumn<ReviewerRequest, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentEmail()));
        emailCol.setPrefWidth(200);
        
        TableColumn<ReviewerRequest, String> dateCol = new TableColumn<>("Submission Date");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getSubmissionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        dateCol.setPrefWidth(150);
        
        TableColumn<ReviewerRequest, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getReason()));
        reasonCol.setPrefWidth(300);
        
        table.getColumns().addAll(nameCol, emailCol, dateCol, reasonCol);
    }
    
    private void setupDecisionTable(TableView<ReviewerDecision> table) {
        TableColumn<ReviewerDecision, String> studentCol = new TableColumn<>("Student ID");
        studentCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentId()));
        
        TableColumn<ReviewerDecision, String> staffCol = new TableColumn<>("Staff ID");
        staffCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStaffId()));
        
        TableColumn<ReviewerDecision, String> decisionCol = new TableColumn<>("Decision");
        decisionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDecisionType()));
        
        TableColumn<ReviewerDecision, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getReason()));
        
        TableColumn<ReviewerDecision, String> dateCol = new TableColumn<>("Decision Date");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getDecisionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        
        table.getColumns().addAll(studentCol, staffCol, decisionCol, reasonCol, dateCol);
    }
    
    private void setupStudentTable(TableView<Student> table) {
        TableColumn<Student, String> idCol = new TableColumn<>("Student ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentId()));
        
        TableColumn<Student, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));
        
        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        
        TableColumn<Student, String> majorCol = new TableColumn<>("Major");
        majorCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMajor()));
        
        TableColumn<Student, String> reviewerCol = new TableColumn<>("Is Reviewer");
        reviewerCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().hasReviewerRole() ? "Yes ✓" : "No"));
        
        table.getColumns().addAll(idCol, nameCol, emailCol, majorCol, reviewerCol);
    }
    
    private void setupRoleTable(TableView<Student> table) {
        TableColumn<Student, String> idCol = new TableColumn<>("Student ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentId()));
        idCol.setPrefWidth(100);
        
        TableColumn<Student, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));
        nameCol.setPrefWidth(150);
        
        TableColumn<Student, String> roleCol = new TableColumn<>("Current Role");
        roleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().hasReviewerRole() ? "REVIEWER ✓" : "STUDENT"));
        roleCol.setPrefWidth(120);
        
        TableColumn<Student, String> majorCol = new TableColumn<>("Major");
        majorCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMajor()));
        majorCol.setPrefWidth(200);
        
        table.getColumns().addAll(idCol, nameCol, roleCol, majorCol);
    }
    
    private void updateContentLists(ListView<String> questionList, ListView<String> answerList) {
        ObservableList<String> questionItems = FXCollections.observableArrayList();
        ObservableList<String> answerItems = FXCollections.observableArrayList();
        
        for (Question q : questions) {
            String status = q.isFlagged() ? " 🚩 FLAGGED" : "";
            questionItems.add("ID " + q.getId() + ": " + q.getContent() + status);
        }
        
        for (Answer a : answers) {
            String status = a.isFlagged() ? " 🚩 FLAGGED" : "";
            answerItems.add("ID " + a.getId() + " (Q" + a.getQuestionId() + "): " + a.getContent() + status);
        }
        
        questionList.setItems(questionItems);
        answerList.setItems(answerItems);
    }
    
    private void log(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        outputArea.appendText("[" + timestamp + "] " + message + "\n");
        outputArea.setScrollTop(Double.MAX_VALUE);
    }
    
    private void initializeTestData() {
        // Initialize students
        students.addAll(
            new Student("student1", "Alice", "Johnson", "alice.johnson@asu.edu", "Computer Science"),
            new Student("student2", "Bob", "Smith", "bob.smith@asu.edu", "Software Engineering"),
            new Student("student3", "Carol", "Davis", "carol.davis@asu.edu", "Information Systems"),
            new Student("student4", "David", "Brown", "david.brown@asu.edu", "Computer Science"),
            new Student("student5", "Emma", "Wilson", "emma.wilson@asu.edu", "Cybersecurity")
        );
        
        // Set initial reviewer role
        students.get(1).setHasReviewerRole(true); // Bob Smith
        
        // Initialize reviewer requests
        requests.addAll(
            new ReviewerRequest("student1", "Alice Johnson", "alice.johnson@asu.edu", 
                "I have experience in Java programming", LocalDateTime.now().minusDays(5)),
            new ReviewerRequest("student3", "Carol Davis", "carol.davis@asu.edu", 
                "Interested in helping with peer reviews", LocalDateTime.now().minusDays(3)),
            new ReviewerRequest("student4", "David Brown", "david.brown@asu.edu", 
                "Want to contribute to the community", LocalDateTime.now().minusDays(1)),
            new ReviewerRequest("student5", "Emma Wilson", "emma.wilson@asu.edu", 
                "Have cybersecurity background", LocalDateTime.now().minusHours(12))
        );
        
        // Initialize decisions
        decisions.addAll(
            new ReviewerDecision("student2", "staff1", "APPROVED", "Good qualifications", LocalDateTime.now().minusDays(2)),
            new ReviewerDecision("student1", "staff2", "DENIED", "Needs more experience", LocalDateTime.now().minusDays(1))
        );
        
        // Initialize questions and answers
        questions.addAll(
            new Question(1L, "How do I implement a binary search tree?", "student1", "Alice Johnson"),
            new Question(2L, "What is the best sorting algorithm for large datasets?", "student3", "Carol Davis"),
            new Question(3L, "This content might be inappropriate for some users", "student4", "David Brown")
        );
        
        answers.addAll(
            new Answer(1L, 1L, "You can implement BST using recursive methods", "student2", "Bob Smith"),
            new Answer(2L, 2L, "For large datasets, consider merge sort or quicksort", "student5", "Emma Wilson"),
            new Answer(3L, 1L, "This answer contains questionable content", "student4", "David Brown")
        );
    }
    
    private String getCSS() {
        return ".tab-pane .tab-header-area .tab-header-background { -fx-background-color: #34495e; }" +
               ".tab-pane .tab { -fx-background-color: #34495e; -fx-text-fill: white; }" +
               ".tab-pane .tab:selected { -fx-background-color: #2980b9; }" +
               ".button { -fx-padding: 8px 16px; -fx-font-size: 12px; }" +
               ".table-view { -fx-font-size: 12px; }";
    }
    
    // Model classes (same as before, but with JavaFX property support)
    
    public static class ReviewerRequest {
        private String studentId, studentName, studentEmail, reason;
        private LocalDateTime submissionDate;
        
        public ReviewerRequest(String studentId, String studentName, String studentEmail, String reason, LocalDateTime date) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.studentEmail = studentEmail;
            this.reason = reason;
            this.submissionDate = date;
        }
        
        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getStudentEmail() { return studentEmail; }
        public String getReason() { return reason; }
        public LocalDateTime getSubmissionDate() { return submissionDate; }
    }
    
    public static class ReviewerDecision {
        private String studentId, staffId, decisionType, reason;
        private LocalDateTime decisionDate;
        
        public ReviewerDecision(String studentId, String staffId, String decisionType, String reason, LocalDateTime date) {
            this.studentId = studentId;
            this.staffId = staffId;
            this.decisionType = decisionType;
            this.reason = reason;
            this.decisionDate = date;
        }
        
        public String getStudentId() { return studentId; }
        public String getStaffId() { return staffId; }
        public String getDecisionType() { return decisionType; }
        public String getReason() { return reason; }
        public LocalDateTime getDecisionDate() { return decisionDate; }
    }
    
    public static class Student {
        private String studentId, firstName, lastName, email, major;
        private boolean hasReviewerRole = false;
        
        public Student(String studentId, String firstName, String lastName, String email, String major) {
            this.studentId = studentId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.major = major;
        }
        
        public String getStudentId() { return studentId; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
        public String getMajor() { return major; }
        public String getFullName() { return firstName + " " + lastName; }
        public boolean hasReviewerRole() { return hasReviewerRole; }
        public void setHasReviewerRole(boolean hasReviewerRole) { this.hasReviewerRole = hasReviewerRole; }
    }
    
    public static class Question {
        private Long id;
        private String content, authorId, authorName;
        private boolean flagged = false;
        private String flagReason;
        
        public Question(Long id, String content, String authorId, String authorName) {
            this.id = id;
            this.content = content;
            this.authorId = authorId;
            this.authorName = authorName;
        }
        
        public Long getId() { return id; }
        public String getContent() { return content; }
        public boolean isFlagged() { return flagged; }
        public void setFlagged(boolean flagged) { this.flagged = flagged; }
        public void setFlagReason(String reason) { this.flagReason = reason; }
    }
    
    public static class Answer {
        private Long id, questionId;
        private String content, authorId, authorName;
        private boolean flagged = false;
        private String flagReason;
        
        public Answer(Long id, Long questionId, String content, String authorId, String authorName) {
            this.id = id;
            this.questionId = questionId;
            this.content = content;
            this.authorId = authorId;
            this.authorName = authorName;
        }
        
        public Long getId() { return id; }
        public Long getQuestionId() { return questionId; }
        public String getContent() { return content; }
        public boolean isFlagged() { return flagged; }
        public void setFlagged(boolean flagged) { this.flagged = flagged; }
        public void setFlagReason(String reason) { this.flagReason = reason; }
    }
}