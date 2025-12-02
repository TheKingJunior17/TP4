package edu.asu.cse360.tp4;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 🎯 TP4 Staff Management System - Interactive Swing GUI
 * A complete clickable interface for all 5 staff management user stories
 * 
 * Features:
 * ✅ User Story 1: Sort Reviewer Requests by Date
 * ✅ User Story 2: Maintain a Reviewer Decision Log  
 * ✅ User Story 3: Report Inappropriate Questions or Responses
 * ✅ User Story 4: Locate Students by Name or Email
 * ✅ User Story 5: Adjust Reviewer Role Manually
 */
public class StaffManagementSwingGUI extends JFrame {
    
    // Data storage
    private List<ReviewerRequest> requests = new ArrayList<>();
    private List<ReviewerDecision> decisions = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private List<Answer> answers = new ArrayList<>();
    
    // GUI Components
    private JTabbedPane tabbedPane;
    private JTextArea outputArea;
    private DefaultTableModel requestTableModel;
    private DefaultTableModel decisionTableModel;
    private DefaultTableModel studentTableModel;
    
    // Formatters
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Use default look and feel
            new StaffManagementSwingGUI().setVisible(true);
        });
    }
    
    public StaffManagementSwingGUI() {
        initializeData();
        initializeGUI();
        setupEventHandlers();
        log("✅ Staff Management System initialized successfully!");
        log("🎯 Ready for interactive testing of all 5 user stories.");
    }
    
    private void initializeGUI() {
        setTitle("🎯 TP4 Staff Management System - Interactive GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create header
        JLabel header = new JLabel("🎯 TP4 Staff Management System", SwingConstants.CENTER);
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(header, BorderLayout.NORTH);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("🔍 Filter Requests", createRequestFilterPanel());
        tabbedPane.addTab("📋 Decision History", createDecisionHistoryPanel());
        tabbedPane.addTab("🚩 Flag Content", createContentFlaggingPanel());
        tabbedPane.addTab("👥 Search Students", createStudentSearchPanel());
        tabbedPane.addTab("⚙️ Manage Roles", createRoleManagementPanel());
        add(tabbedPane, BorderLayout.CENTER);
        
        // Create output area
        outputArea = new JTextArea(8, 0);
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.GREEN);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(new TitledBorder("📊 System Output"));
        add(outputScroll, BorderLayout.SOUTH);
        
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }
    
    // User Story 1: Sort Reviewer Requests by Date
    private JPanel createRequestFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel title = new JLabel("User Story 1: Sort Reviewer Requests by Date");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);
        
        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{
            "All Requests", "Last 24 Hours", "Last Week", "Last Month", "Pending Only", "Approved Only"
        });
        JButton filterButton = new JButton("🔍 Filter Requests");
        JButton sortByDateButton = new JButton("📅 Sort by Date");
        JButton sortByStatusButton = new JButton("📊 Sort by Status");
        
        controls.add(new JLabel("Filter:"));
        controls.add(filterCombo);
        controls.add(filterButton);
        controls.add(sortByDateButton);
        controls.add(sortByStatusButton);
        panel.add(controls, BorderLayout.CENTER);
        
        // Table
        String[] columns = {"Request ID", "Student ID", "Requested Date", "Status", "Subject Area", "Priority"};
        requestTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable requestTable = new JTable(requestTableModel);
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refreshRequestTable();
        
        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        // Event handlers
        filterButton.addActionListener(e -> {
            String filter = (String) filterCombo.getSelectedItem();
            filterAndDisplayRequests(filter);
        });
        
        sortByDateButton.addActionListener(e -> {
            requests.sort(Comparator.comparing(ReviewerRequest::getRequestedDate));
            refreshRequestTable();
            log("✅ Requests sorted by date (oldest first)");
        });
        
        sortByStatusButton.addActionListener(e -> {
            requests.sort(Comparator.comparing(ReviewerRequest::getStatus));
            refreshRequestTable();
            log("✅ Requests sorted by status (alphabetical)");
        });
        
        return panel;
    }
    
    // User Story 2: Maintain a Reviewer Decision Log
    private JPanel createDecisionHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel title = new JLabel("User Story 2: Maintain a Reviewer Decision Log");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);
        
        // Form for adding decisions
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Add New Decision"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField studentField = new JTextField(15);
        JTextField staffField = new JTextField("staff1", 15);
        JComboBox<String> decisionCombo = new JComboBox<>(new String[]{"APPROVED", "REJECTED", "PENDING"});
        JTextField reasonField = new JTextField(20);
        JButton addDecisionButton = new JButton("📋 Add Decision");
        
        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1; form.add(studentField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Staff ID:"), gbc);
        gbc.gridx = 1; form.add(staffField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Decision:"), gbc);
        gbc.gridx = 1; form.add(decisionCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1; form.add(reasonField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; form.add(addDecisionButton, gbc);
        
        panel.add(form, BorderLayout.CENTER);
        
        // Decision history table
        String[] columns = {"Decision ID", "Student ID", "Staff ID", "Decision", "Reason", "Timestamp"};
        decisionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable decisionTable = new JTable(decisionTableModel);
        refreshDecisionTable();
        
        JScrollPane scrollPane = new JScrollPane(decisionTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        scrollPane.setBorder(new TitledBorder("Decision History (Chronological)"));
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        // Event handler
        addDecisionButton.addActionListener(e -> {
            String studentId = studentField.getText().trim();
            String staffId = staffField.getText().trim();
            String decision = (String) decisionCombo.getSelectedItem();
            String reason = reasonField.getText().trim();
            
            if (studentId.isEmpty() || reason.isEmpty()) {
                log("❌ Please fill in Student ID and Reason");
                return;
            }
            
            ReviewerDecision newDecision = new ReviewerDecision(
                "DEC" + (decisions.size() + 1), studentId, staffId, decision, reason);
            decisions.add(newDecision);
            refreshDecisionTable();
            
            // Clear form
            studentField.setText("");
            reasonField.setText("");
            
            log("✅ Decision logged: " + decision + " for student " + studentId);
        });
        
        return panel;
    }
    
    // User Story 3: Report Inappropriate Questions or Responses
    private JPanel createContentFlaggingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel title = new JLabel("User Story 3: Report Inappropriate Questions or Responses");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);
        
        // Main content area
        JPanel content = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Questions panel
        JPanel questionsPanel = new JPanel(new BorderLayout());
        questionsPanel.setBorder(new TitledBorder("Questions"));
        DefaultListModel<String> questionListModel = new DefaultListModel<>();
        JList<String> questionList = new JList<>(questionListModel);
        JButton flagQuestionButton = new JButton("🚩 Flag Selected Question");
        
        questionsPanel.add(new JScrollPane(questionList), BorderLayout.CENTER);
        questionsPanel.add(flagQuestionButton, BorderLayout.SOUTH);
        content.add(questionsPanel);
        
        // Answers panel
        JPanel answersPanel = new JPanel(new BorderLayout());
        answersPanel.setBorder(new TitledBorder("Answers"));
        DefaultListModel<String> answerListModel = new DefaultListModel<>();
        JList<String> answerList = new JList<>(answerListModel);
        JButton flagAnswerButton = new JButton("🚩 Flag Selected Answer");
        
        answersPanel.add(new JScrollPane(answerList), BorderLayout.CENTER);
        answersPanel.add(flagAnswerButton, BorderLayout.SOUTH);
        content.add(answersPanel);
        
        panel.add(content, BorderLayout.CENTER);
        
        // Refresh button
        JButton refreshButton = new JButton("🔄 Refresh Content");
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        // Load initial content
        updateContentLists(questionListModel, answerListModel);
        
        // Event handlers
        flagQuestionButton.addActionListener(e -> {
            String selected = questionList.getSelectedValue();
            if (selected != null) {
                String questionId = selected.split(":")[0];
                Question question = questions.stream()
                    .filter(q -> q.getQuestionId().equals(questionId))
                    .findFirst().orElse(null);
                if (question != null) {
                    question.setFlagged(true);
                    updateContentLists(questionListModel, answerListModel);
                    log("🚩 Question flagged: " + questionId + " - " + question.getQuestionText());
                }
            }
        });
        
        flagAnswerButton.addActionListener(e -> {
            String selected = answerList.getSelectedValue();
            if (selected != null) {
                String answerId = selected.split(":")[0];
                Answer answer = answers.stream()
                    .filter(a -> a.getAnswerId().equals(answerId))
                    .findFirst().orElse(null);
                if (answer != null) {
                    answer.setFlagged(true);
                    updateContentLists(questionListModel, answerListModel);
                    log("🚩 Answer flagged: " + answerId + " - " + answer.getAnswerText());
                }
            }
        });
        
        refreshButton.addActionListener(e -> {
            updateContentLists(questionListModel, answerListModel);
            log("🔄 Content lists refreshed");
        });
        
        return panel;
    }
    
    // User Story 4: Locate Students by Name or Email
    private JPanel createStudentSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel title = new JLabel("User Story 4: Locate Students by Name or Email");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);
        
        // Search controls
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("🔍 Search Students");
        JButton clearButton = new JButton("🗑️ Clear Results");
        
        searchPanel.add(new JLabel("Search (Name or Email):"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        panel.add(searchPanel, BorderLayout.CENTER);
        
        // Results table
        String[] columns = {"Student ID", "Name", "Email", "Role", "Registration Date"};
        studentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable studentTable = new JTable(studentTableModel);
        refreshStudentTable();
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        scrollPane.setBorder(new TitledBorder("Search Results"));
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        // Event handlers
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (searchTerm.isEmpty()) {
                refreshStudentTable();
                log("📋 Showing all students");
                return;
            }
            
            List<Student> results = students.stream()
                .filter(s -> s.getName().toLowerCase().contains(searchTerm) ||
                           s.getEmail().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
            
            updateStudentTable(results);
            log("🔍 Search completed: " + results.size() + " students found for '" + searchTerm + "'");
        });
        
        clearButton.addActionListener(e -> {
            searchField.setText("");
            refreshStudentTable();
            log("🗑️ Search results cleared, showing all students");
        });
        
        // Real-time search
        searchField.addActionListener(e -> searchButton.doClick());
        
        return panel;
    }
    
    // User Story 5: Adjust Reviewer Role Manually
    private JPanel createRoleManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel title = new JLabel("User Story 5: Adjust Reviewer Role Manually");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);
        
        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField studentIdField = new JTextField(15);
        JButton assignButton = new JButton("👤 Assign Reviewer Role");
        JButton removeButton = new JButton("❌ Remove Reviewer Role");
        JButton refreshRolesButton = new JButton("🔄 Refresh");
        
        controls.add(new JLabel("Student ID:"));
        controls.add(studentIdField);
        controls.add(assignButton);
        controls.add(removeButton);
        controls.add(refreshRolesButton);
        panel.add(controls, BorderLayout.CENTER);
        
        // Role table (same as student table but focused on roles)
        String[] columns = {"Student ID", "Name", "Email", "Current Role", "Last Modified"};
        DefaultTableModel roleTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable roleTable = new JTable(roleTableModel);
        refreshRoleTable(roleTableModel);
        
        JScrollPane scrollPane = new JScrollPane(roleTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        scrollPane.setBorder(new TitledBorder("Student Roles"));
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        // Event handlers
        assignButton.addActionListener(e -> {
            String studentId = studentIdField.getText().trim();
            if (studentId.isEmpty()) {
                log("❌ Please enter a Student ID");
                return;
            }
            
            Student student = students.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst().orElse(null);
            
            if (student == null) {
                log("❌ Student not found: " + studentId);
                return;
            }
            
            if ("REVIEWER".equals(student.getRole())) {
                log("ℹ️ Student " + studentId + " already has REVIEWER role");
            } else {
                student.setRole("REVIEWER");
                refreshRoleTable(roleTableModel);
                log("✅ Reviewer role assigned to student: " + studentId + " (" + student.getName() + ")");
            }
            studentIdField.setText("");
        });
        
        removeButton.addActionListener(e -> {
            String studentId = studentIdField.getText().trim();
            if (studentId.isEmpty()) {
                log("❌ Please enter a Student ID");
                return;
            }
            
            Student student = students.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst().orElse(null);
            
            if (student == null) {
                log("❌ Student not found: " + studentId);
                return;
            }
            
            if (!"REVIEWER".equals(student.getRole())) {
                log("ℹ️ Student " + studentId + " does not have REVIEWER role");
            } else {
                student.setRole("STUDENT");
                refreshRoleTable(roleTableModel);
                log("❌ Reviewer role removed from student: " + studentId + " (" + student.getName() + ")");
            }
            studentIdField.setText("");
        });
        
        refreshRolesButton.addActionListener(e -> {
            refreshRoleTable(roleTableModel);
            log("🔄 Role table refreshed");
        });
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Tab change events
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String[] tabNames = {"Filter Requests", "Decision History", "Flag Content", "Search Students", "Manage Roles"};
            log("📂 Switched to tab: " + tabNames[selectedIndex]);
        });
    }
    
    // Helper methods for table updates
    private void refreshRequestTable() {
        requestTableModel.setRowCount(0);
        for (ReviewerRequest request : requests) {
            requestTableModel.addRow(new Object[]{
                request.getRequestId(),
                request.getStudentId(),
                request.getRequestedDate().format(formatter),
                request.getStatus(),
                request.getSubjectArea(),
                request.getPriority()
            });
        }
    }
    
    private void refreshDecisionTable() {
        decisionTableModel.setRowCount(0);
        // Sort decisions by timestamp (most recent first)
        decisions.stream()
            .sorted(Comparator.comparing(ReviewerDecision::getDecisionDate).reversed())
            .forEach(decision -> decisionTableModel.addRow(new Object[]{
                decision.getDecisionId(),
                decision.getStudentId(),
                decision.getStaffId(),
                decision.getDecision(),
                decision.getReason(),
                decision.getDecisionDate().format(formatter)
            }));
    }
    
    private void refreshStudentTable() {
        updateStudentTable(students);
    }
    
    private void updateStudentTable(List<Student> studentList) {
        studentTableModel.setRowCount(0);
        for (Student student : studentList) {
            studentTableModel.addRow(new Object[]{
                student.getStudentId(),
                student.getName(),
                student.getEmail(),
                student.getRole(),
                student.getRegistrationDate().format(formatter)
            });
        }
    }
    
    private void refreshRoleTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Student student : students) {
            model.addRow(new Object[]{
                student.getStudentId(),
                student.getName(),
                student.getEmail(),
                student.getRole(),
                student.getRegistrationDate().format(formatter)
            });
        }
    }
    
    private void filterAndDisplayRequests(String filter) {
        LocalDateTime now = LocalDateTime.now();
        List<ReviewerRequest> filtered = requests;
        
        switch (filter) {
            case "Last 24 Hours":
                filtered = requests.stream()
                    .filter(r -> r.getRequestedDate().isAfter(now.minusDays(1)))
                    .collect(Collectors.toList());
                break;
            case "Last Week":
                filtered = requests.stream()
                    .filter(r -> r.getRequestedDate().isAfter(now.minusDays(7)))
                    .collect(Collectors.toList());
                break;
            case "Last Month":
                filtered = requests.stream()
                    .filter(r -> r.getRequestedDate().isAfter(now.minusDays(30)))
                    .collect(Collectors.toList());
                break;
            case "Pending Only":
                filtered = requests.stream()
                    .filter(r -> "PENDING".equals(r.getStatus()))
                    .collect(Collectors.toList());
                break;
            case "Approved Only":
                filtered = requests.stream()
                    .filter(r -> "APPROVED".equals(r.getStatus()))
                    .collect(Collectors.toList());
                break;
        }
        
        // Update the requests list and refresh table
        requests = new ArrayList<>(filtered);
        refreshRequestTable();
        log("🔍 Filter applied: " + filter + " (" + filtered.size() + " results)");
    }
    
    private void updateContentLists(DefaultListModel<String> questionModel, DefaultListModel<String> answerModel) {
        questionModel.clear();
        for (Question q : questions) {
            String status = q.isFlagged() ? " [🚩 FLAGGED]" : " [✅ Clean]";
            questionModel.addElement(q.getQuestionId() + ": " + q.getQuestionText() + status);
        }
        
        answerModel.clear();
        for (Answer a : answers) {
            String status = a.isFlagged() ? " [🚩 FLAGGED]" : " [✅ Clean]";
            answerModel.addElement(a.getAnswerId() + ": " + a.getAnswerText() + status);
        }
    }
    
    private void log(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        outputArea.append("[" + timestamp + "] " + message + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    // Initialize test data
    private void initializeData() {
        LocalDateTime now = LocalDateTime.now();
        
        // Sample students
        students.add(new Student("STU001", "Alice Johnson", "alice.johnson@asu.edu", "STUDENT", now.minusDays(30)));
        students.add(new Student("STU002", "Bob Smith", "bob.smith@asu.edu", "REVIEWER", now.minusDays(25)));
        students.add(new Student("STU003", "Carol Davis", "carol.davis@asu.edu", "STUDENT", now.minusDays(20)));
        students.add(new Student("STU004", "David Wilson", "david.wilson@asu.edu", "REVIEWER", now.minusDays(15)));
        students.add(new Student("STU005", "Eva Brown", "eva.brown@asu.edu", "STUDENT", now.minusDays(10)));
        
        // Sample reviewer requests
        requests.add(new ReviewerRequest("REQ001", "STU001", now.minusDays(5), "PENDING", "Mathematics", "HIGH"));
        requests.add(new ReviewerRequest("REQ002", "STU003", now.minusDays(3), "APPROVED", "Computer Science", "MEDIUM"));
        requests.add(new ReviewerRequest("REQ003", "STU005", now.minusDays(1), "PENDING", "Physics", "LOW"));
        requests.add(new ReviewerRequest("REQ004", "STU001", now.minusHours(12), "REJECTED", "Chemistry", "HIGH"));
        
        // Sample decisions
        decisions.add(new ReviewerDecision("DEC001", "STU002", "STAFF001", "APPROVED", "Meets all requirements", now.minusDays(2)));
        decisions.add(new ReviewerDecision("DEC002", "STU004", "STAFF002", "REJECTED", "Insufficient experience", now.minusDays(1)));
        
        // Sample questions and answers
        questions.add(new Question("Q001", "What is the capital of Arizona?", "STU001", false));
        questions.add(new Question("Q002", "This question contains inappropriate content", "STU002", false));
        questions.add(new Question("Q003", "How do you solve quadratic equations?", "STU003", false));
        
        answers.add(new Answer("A001", "Phoenix is the capital of Arizona", "STU002", "Q001", false));
        answers.add(new Answer("A002", "This answer is inappropriate and offensive", "STU001", "Q002", false));
        answers.add(new Answer("A003", "Use the quadratic formula: x = (-b ± √(b²-4ac))/2a", "STU004", "Q003", false));
    }
    
    // Inner classes for data models
    static class ReviewerRequest {
        private String requestId, studentId, status, subjectArea, priority;
        private LocalDateTime requestedDate;
        
        public ReviewerRequest(String requestId, String studentId, LocalDateTime requestedDate, 
                             String status, String subjectArea, String priority) {
            this.requestId = requestId;
            this.studentId = studentId;
            this.requestedDate = requestedDate;
            this.status = status;
            this.subjectArea = subjectArea;
            this.priority = priority;
        }
        
        // Getters
        public String getRequestId() { return requestId; }
        public String getStudentId() { return studentId; }
        public LocalDateTime getRequestedDate() { return requestedDate; }
        public String getStatus() { return status; }
        public String getSubjectArea() { return subjectArea; }
        public String getPriority() { return priority; }
    }
    
    static class ReviewerDecision {
        private String decisionId, studentId, staffId, decision, reason;
        private LocalDateTime decisionDate;
        
        public ReviewerDecision(String decisionId, String studentId, String staffId, 
                              String decision, String reason) {
            this.decisionId = decisionId;
            this.studentId = studentId;
            this.staffId = staffId;
            this.decision = decision;
            this.reason = reason;
            this.decisionDate = LocalDateTime.now();
        }
        
        public ReviewerDecision(String decisionId, String studentId, String staffId, 
                              String decision, String reason, LocalDateTime decisionDate) {
            this.decisionId = decisionId;
            this.studentId = studentId;
            this.staffId = staffId;
            this.decision = decision;
            this.reason = reason;
            this.decisionDate = decisionDate;
        }
        
        // Getters
        public String getDecisionId() { return decisionId; }
        public String getStudentId() { return studentId; }
        public String getStaffId() { return staffId; }
        public String getDecision() { return decision; }
        public String getReason() { return reason; }
        public LocalDateTime getDecisionDate() { return decisionDate; }
    }
    
    static class Student {
        private String studentId, name, email, role;
        private LocalDateTime registrationDate;
        
        public Student(String studentId, String name, String email, String role, LocalDateTime registrationDate) {
            this.studentId = studentId;
            this.name = name;
            this.email = email;
            this.role = role;
            this.registrationDate = registrationDate;
        }
        
        // Getters and setters
        public String getStudentId() { return studentId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public LocalDateTime getRegistrationDate() { return registrationDate; }
    }
    
    static class Question {
        private String questionId, questionText, authorId;
        private boolean flagged;
        
        public Question(String questionId, String questionText, String authorId, boolean flagged) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.authorId = authorId;
            this.flagged = flagged;
        }
        
        // Getters and setters
        public String getQuestionId() { return questionId; }
        public String getQuestionText() { return questionText; }
        public String getAuthorId() { return authorId; }
        public boolean isFlagged() { return flagged; }
        public void setFlagged(boolean flagged) { this.flagged = flagged; }
    }
    
    static class Answer {
        private String answerId, answerText, authorId, questionId;
        private boolean flagged;
        
        public Answer(String answerId, String answerText, String authorId, String questionId, boolean flagged) {
            this.answerId = answerId;
            this.answerText = answerText;
            this.authorId = authorId;
            this.questionId = questionId;
            this.flagged = flagged;
        }
        
        // Getters and setters
        public String getAnswerId() { return answerId; }
        public String getAnswerText() { return answerText; }
        public String getAuthorId() { return authorId; }
        public String getQuestionId() { return questionId; }
        public boolean isFlagged() { return flagged; }
        public void setFlagged(boolean flagged) { this.flagged = flagged; }
    }
}