package edu.asu.cse360.tp4.staff.service;

import edu.asu.cse360.tp4.common.model.*;
import edu.asu.cse360.tp4.common.exception.ValidationException;
import edu.asu.cse360.tp4.staff.repository.StaffRepositoryImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.LocalDateTime;
import java.util.List;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional test suites for StaffManagementService covering student search 
 * and reviewer role assignment functionality.
 * 
 * This class continues the comprehensive testing of the StaffManagementService
 * with focus on student search capabilities and reviewer role management.
 * 
 * @author TP4 Team - Testing Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StaffManagementServiceAdditionalTest {
    
    private StaffManagementService staffService;
    private StaffRepositoryImpl staffRepository;
    
    @BeforeEach
    void setUp() {
        staffRepository = new StaffRepositoryImpl();
        staffService = new StaffManagementService(staffRepository);
    }
    
    @AfterEach
    void tearDown() {
        staffRepository.clearAllData();
        staffRepository.setSimulateFailure(false);
    }
    
    /**
     * Test suite for student search functionality.
     */
    @Nested
    @DisplayName("Student Search Tests")
    @Execution(ExecutionMode.CONCURRENT)
    class StudentSearchTests {
        
        @Test
        @DisplayName("testSearchStudentsNameExact - Checks that a full name search returns exactly matching students")
        void testSearchStudentsNameExact() {
            // Arrange
            String exactName = "Alice Johnson";
            
            // Act
            List<Student> results = staffService.searchStudents(exactName);
            
            // Assert
            assertNotNull(results);
            assertTrue(results.size() > 0, "Should find students with exact name match");
            
            // Verify that all results contain the search term
            for (Student student : results) {
                assertTrue(student.matchesSearchTerm(exactName), 
                    "Student " + student.getFullName() + " should match search term");
            }
            
            // Verify we found the expected student
            assertTrue(results.stream().anyMatch(s -> s.getFullName().equals("Alice Johnson")));
        }
        
        @Test
        @DisplayName("testSearchStudentsNamePartial - Validates that partial name searches return all relevant matches")
        void testSearchStudentsNamePartial() {
            // Arrange
            String partialName = "Johnson"; // Should match "Alice Johnson"
            
            // Act
            List<Student> results = staffService.searchStudents(partialName);
            
            // Assert
            assertNotNull(results);
            assertTrue(results.size() > 0, "Should find students with partial name match");
            
            // Verify all results contain the partial name
            for (Student student : results) {
                assertTrue(student.getFullName().toLowerCase().contains(partialName.toLowerCase()) ||
                          student.getEmail().toLowerCase().contains(partialName.toLowerCase()) ||
                          student.getStudentId().toLowerCase().contains(partialName.toLowerCase()),
                    "Student should match partial search term");
            }
        }
        
        @Test
        @DisplayName("testSearchStudentsEmailExact - Ensures that exact email searches match correctly")
        void testSearchStudentsEmailExact() {
            // Arrange
            String exactEmail = "alice.johnson@asu.edu";
            
            // Act
            List<Student> results = staffService.searchStudents(exactEmail);
            
            // Assert
            assertNotNull(results);
            assertTrue(results.size() > 0, "Should find students with exact email match");
            
            // Verify we found the expected student
            assertTrue(results.stream().anyMatch(s -> s.getEmail().equals(exactEmail)));
        }
        
        @Test
        @DisplayName("testSearchStudentsEmailPartial - Confirms partial email searches return matching entries")
        void testSearchStudentsEmailPartial() {
            // Arrange
            String partialEmail = "@asu.edu"; // Should match all ASU students
            
            // Act
            List<Student> results = staffService.searchStudents(partialEmail);
            
            // Assert
            assertNotNull(results);
            assertTrue(results.size() > 0, "Should find students with partial email match");
            
            // Verify all results have ASU email addresses
            for (Student student : results) {
                assertTrue(student.getEmail().contains("@asu.edu"), 
                    "All results should have ASU email addresses");
            }
        }
        
        @Test
        @DisplayName("testSearchStudentsCaseInsensitive - Ensures search results ignore uppercase/lowercase differences")
        void testSearchStudentsCaseInsensitive() {
            // Arrange
            String searchTermLower = "alice";
            String searchTermUpper = "ALICE";
            String searchTermMixed = "AlIcE";
            
            // Act
            List<Student> resultsLower = staffService.searchStudents(searchTermLower);
            List<Student> resultsUpper = staffService.searchStudents(searchTermUpper);
            List<Student> resultsMixed = staffService.searchStudents(searchTermMixed);
            
            // Assert
            assertNotNull(resultsLower);
            assertNotNull(resultsUpper);
            assertNotNull(resultsMixed);
            
            // All searches should return the same results
            assertEquals(resultsLower.size(), resultsUpper.size(), 
                "Case variations should return same number of results");
            assertEquals(resultsLower.size(), resultsMixed.size(), 
                "Case variations should return same number of results");
            
            // Verify all contain Alice Johnson
            assertTrue(resultsLower.stream().anyMatch(s -> s.getFirstName().equals("Alice")));
            assertTrue(resultsUpper.stream().anyMatch(s -> s.getFirstName().equals("Alice")));
            assertTrue(resultsMixed.stream().anyMatch(s -> s.getFirstName().equals("Alice")));
        }
        
        @Test
        @DisplayName("testSearchStudentsEmptyTermReturnsAll - An empty search should return the entire student list")
        void testSearchStudentsEmptyTermReturnsAll() {
            // Arrange
            List<Student> allStudents;
            try {
                allStudents = staffRepository.getAllStudents();
            } catch (SQLException e) {
                fail("Failed to get all students: " + e.getMessage());
                return;
            }
            
            // Act - Test with null, empty string, and whitespace-only string
            List<Student> nullResults = staffService.searchStudents(null);
            List<Student> emptyResults = staffService.searchStudents("");
            List<Student> whitespaceResults = staffService.searchStudents("   ");
            
            // Assert
            assertNotNull(nullResults);
            assertNotNull(emptyResults);
            assertNotNull(whitespaceResults);
            
            assertEquals(allStudents.size(), nullResults.size(), 
                "Null search should return all students");
            assertEquals(allStudents.size(), emptyResults.size(), 
                "Empty search should return all students");
            assertEquals(allStudents.size(), whitespaceResults.size(), 
                "Whitespace-only search should return all students");
        }
    }
    
    /**
     * Test suite for reviewer role assignment functionality.
     */
    @Nested
    @DisplayName("Reviewer Role Assignment Tests")
    class ReviewerRoleAssignmentTests {
        
        @Test
        @DisplayName("testAssignReviewerFirstTime - Confirms that granting reviewer permissions to a new user is successful")
        void testAssignReviewerFirstTime() {
            // Arrange - Get a student who doesn't have reviewer role
            String studentId = "student1"; // Alice Johnson doesn't have reviewer role initially
            String staffId = "staff1";
            
            // Verify initial state
            try {
                Student student = staffRepository.getStudentById(studentId);
                assertNotNull(student);
                assertFalse(student.isHasReviewerRole(), "Student should not have reviewer role initially");
            } catch (SQLException e) {
                fail("Failed to get student: " + e.getMessage());
            }
            
            // Act
            boolean result = staffService.assignReviewerRole(studentId, staffId);
            
            // Assert
            assertTrue(result, "Assigning reviewer role should succeed");
            
            // Verify the student now has reviewer role
            try {
                Student student = staffRepository.getStudentById(studentId);
                assertNotNull(student);
                assertTrue(student.isHasReviewerRole(), "Student should now have reviewer role");
            } catch (SQLException e) {
                fail("Failed to verify student role: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("testAssignReviewerAlreadyHasRole - Prevents reassigning the reviewer role if the user already has it")
        void testAssignReviewerAlreadyHasRole() {
            // Arrange - Use a student who already has reviewer role
            String studentId = "student2"; // Bob Smith already has reviewer role
            String staffId = "staff1";
            
            // Verify initial state
            try {
                Student student = staffRepository.getStudentById(studentId);
                assertNotNull(student);
                assertTrue(student.isHasReviewerRole(), "Student should already have reviewer role");
            } catch (SQLException e) {
                fail("Failed to get student: " + e.getMessage());
            }
            
            // Act
            boolean result = staffService.assignReviewerRole(studentId, staffId);
            
            // Assert
            assertFalse(result, "Assigning reviewer role to student who already has it should return false");
            
            // Verify the student still has reviewer role (no change)
            try {
                Student student = staffRepository.getStudentById(studentId);
                assertNotNull(student);
                assertTrue(student.isHasReviewerRole(), "Student should still have reviewer role");
            } catch (SQLException e) {
                fail("Failed to verify student role: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("testRemoveReviewerWhenPresent - Successfully removes reviewer permissions from an eligible user")
        void testRemoveReviewerWhenPresent() {
            // Arrange - Use a student who has reviewer role
            String studentId = "student2"; // Bob Smith has reviewer role
            String staffId = "staff1";
            
            // Verify initial state
            try {
                Student student = staffRepository.getStudentById(studentId);
                assertNotNull(student);
                assertTrue(student.isHasReviewerRole(), "Student should have reviewer role initially");
            } catch (SQLException e) {
                fail("Failed to get student: " + e.getMessage());
            }
            
            // Act
            boolean result = staffService.removeReviewerRole(studentId, staffId);
            
            // Assert
            assertTrue(result, "Removing reviewer role should succeed");
            
            // Verify the student no longer has reviewer role
            try {
                Student student = staffRepository.getStudentById(studentId);
                assertNotNull(student);
                assertFalse(student.isHasReviewerRole(), "Student should no longer have reviewer role");
            } catch (SQLException e) {
                fail("Failed to verify student role: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("testRemoveReviewerWhenAbsent - Ensures no changes occur when removing a role a user doesn't have")
        void testRemoveReviewerWhenAbsent() {
            // Arrange - Use a student who doesn't have reviewer role
            String studentId = "student1"; // Alice Johnson doesn't have reviewer role
            String staffId = "staff1";
            
            // Verify initial state
            try {
                Student student = staffRepository.getStudentById(studentId);
                assertNotNull(student);
                assertFalse(student.isHasReviewerRole(), "Student should not have reviewer role initially");
            } catch (SQLException e) {
                fail("Failed to get student: " + e.getMessage());
            }
            
            // Act
            boolean result = staffService.removeReviewerRole(studentId, staffId);
            
            // Assert
            assertFalse(result, "Removing reviewer role from student who doesn't have it should return false");
            
            // Verify the student still doesn't have reviewer role (no change)
            try {
                Student student = staffRepository.getStudentById(studentId);
                assertNotNull(student);
                assertFalse(student.isHasReviewerRole(), "Student should still not have reviewer role");
            } catch (SQLException e) {
                fail("Failed to verify student role: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("testAssignReviewerExceptionWrapped - Wraps SQLExceptions in RuntimeExceptions during assignment failures")
        void testAssignReviewerExceptionWrapped() {
            // Arrange - Enable failure simulation
            staffRepository.setSimulateFailure(true);
            String studentId = "student1";
            String staffId = "staff1";
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                staffService.assignReviewerRole(studentId, staffId);
            });
            
            assertTrue(exception.getMessage().contains("Database error"), 
                "Exception should indicate database error");
            assertNotNull(exception.getCause(), "Should have an underlying cause");
            assertTrue(exception.getCause() instanceof SQLException, 
                "Underlying cause should be SQLException");
        }
        
        @Test
        @DisplayName("testRemoveReviewerExceptionWrapped - Wraps SQLExceptions in RuntimeExceptions during removal failures")
        void testRemoveReviewerExceptionWrapped() {
            // Arrange - Enable failure simulation
            staffRepository.setSimulateFailure(true);
            String studentId = "student2";
            String staffId = "staff1";
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                staffService.removeReviewerRole(studentId, staffId);
            });
            
            assertTrue(exception.getMessage().contains("Database error"), 
                "Exception should indicate database error");
            assertNotNull(exception.getCause(), "Should have an underlying cause");
            assertTrue(exception.getCause() instanceof SQLException, 
                "Underlying cause should be SQLException");
        }
        
        @Test
        @DisplayName("Test validation of input parameters for role assignment")
        void testRoleAssignmentInputValidation() {
            String staffId = "staff1";
            
            // Test null student ID
            ValidationException exception1 = assertThrows(ValidationException.class, () -> {
                staffService.assignReviewerRole(null, staffId);
            });
            assertTrue(exception1.getMessage().contains("Student ID cannot be null"));
            
            // Test empty student ID
            ValidationException exception2 = assertThrows(ValidationException.class, () -> {
                staffService.assignReviewerRole("", staffId);
            });
            assertTrue(exception2.getMessage().contains("Student ID cannot be null or empty"));
            
            // Test null staff ID
            ValidationException exception3 = assertThrows(ValidationException.class, () -> {
                staffService.assignReviewerRole("student1", null);
            });
            assertTrue(exception3.getMessage().contains("Staff ID cannot be null"));
            
            // Test nonexistent student
            ValidationException exception4 = assertThrows(ValidationException.class, () -> {
                staffService.assignReviewerRole("nonexistent", staffId);
            });
            assertTrue(exception4.getMessage().contains("Student not found"));
        }
    }
    
    /**
     * Test suite for additional service functionality and edge cases.
     */
    @Nested
    @DisplayName("Additional Service Tests")
    class AdditionalServiceTests {
        
        @Test
        @DisplayName("Test reviewer statistics calculation")
        void testReviewerStatistics() {
            // Arrange - Process some requests to create statistics
            List<ReviewerRequest> requests = staffService.filterRequestsByDateRange(null, null);
            if (requests.size() >= 2) {
                staffService.processReviewerRequest(requests.get(0).getRequestId(), true, "staff1", "Approved");
                staffService.processReviewerRequest(requests.get(1).getRequestId(), false, "staff2", "Denied");
            }
            
            // Act
            StaffManagementService.ReviewerStatistics stats = staffService.getReviewerStatistics();
            
            // Assert
            assertNotNull(stats);
            assertTrue(stats.getTotalRequests() > 0);
            assertTrue(stats.getTotalDecisions() >= 0);
            assertTrue(stats.getApprovedRequests() >= 0);
            assertTrue(stats.getDeniedRequests() >= 0);
            assertTrue(stats.getPendingRequests() >= 0);
            
            // Verify totals add up correctly
            assertEquals(stats.getTotalRequests(), 
                       stats.getApprovedRequests() + stats.getDeniedRequests() + stats.getPendingRequests());
        }
        
        @Test
        @DisplayName("Test complete reviewer request processing workflow")
        void testCompleteReviewerRequestProcessing() {
            // Arrange
            List<ReviewerRequest> requests = staffService.filterRequestsByDateRange(null, null);
            assertTrue(requests.size() > 0, "Need at least one request for testing");
            
            ReviewerRequest request = requests.stream()
                .filter(ReviewerRequest::isPending)
                .findFirst()
                .orElse(null);
            assertNotNull(request, "Need a pending request for testing");
            
            Long requestId = request.getRequestId();
            String studentId = request.getStudentId();
            String staffId = "staff1";
            String comments = "Request processed successfully";
            
            // Act - Process the request as approved
            boolean processed = staffService.processReviewerRequest(requestId, true, staffId, comments);
            
            // Assert
            assertTrue(processed, "Request processing should succeed");
            
            // Verify request status is updated
            List<ReviewerRequest> updatedRequests = staffService.filterRequestsByDateRange(null, null);
            ReviewerRequest updatedRequest = updatedRequests.stream()
                .filter(r -> r.getRequestId().equals(requestId))
                .findFirst()
                .orElse(null);
            
            assertNotNull(updatedRequest);
            assertEquals(ReviewerRequest.RequestStatus.APPROVED, updatedRequest.getStatus());
            assertEquals(staffId, updatedRequest.getProcessedByStaffId());
            assertEquals(comments, updatedRequest.getStaffComments());
            
            // Verify student was granted reviewer role
            try {
                Student student = staffRepository.getStudentById(studentId);
                assertNotNull(student);
                assertTrue(student.isHasReviewerRole(), "Student should have reviewer role after approval");
            } catch (SQLException e) {
                fail("Failed to verify student role: " + e.getMessage());
            }
            
            // Verify decision was logged
            List<ReviewerDecision> history = staffService.getReviewerDecisionHistory();
            assertTrue(history.stream().anyMatch(d -> d.getRequestId().equals(requestId)));
        }
        
        @Test
        @DisplayName("Test concurrent access to service methods")
        void testConcurrentAccess() throws InterruptedException {
            // This test verifies thread safety of the service
            // Not a formal concurrency test, but ensures basic thread safety
            
            final int threadCount = 5;
            final Thread[] threads = new Thread[threadCount];
            final boolean[] results = new boolean[threadCount];
            
            // Create threads that perform different operations
            for (int i = 0; i < threadCount; i++) {
                final int threadIndex = i;
                threads[i] = new Thread(() -> {
                    try {
                        // Each thread performs a different operation
                        switch (threadIndex % 4) {
                            case 0:
                                List<Student> students = staffService.searchStudents("Alice");
                                results[threadIndex] = students != null;
                                break;
                            case 1:
                                List<ReviewerRequest> requests = staffService.filterRequestsByDateRange(null, null);
                                results[threadIndex] = requests != null;
                                break;
                            case 2:
                                List<ReviewerDecision> history = staffService.getReviewerDecisionHistory();
                                results[threadIndex] = history != null;
                                break;
                            case 3:
                                StaffManagementService.ReviewerStatistics stats = staffService.getReviewerStatistics();
                                results[threadIndex] = stats != null;
                                break;
                        }
                    } catch (Exception e) {
                        results[threadIndex] = false;
                    }
                });
            }
            
            // Start all threads
            for (Thread thread : threads) {
                thread.start();
            }
            
            // Wait for all threads to complete
            for (Thread thread : threads) {
                thread.join(1000); // Wait up to 1 second per thread
            }
            
            // Verify all operations completed successfully
            for (int i = 0; i < threadCount; i++) {
                assertTrue(results[i], "Thread " + i + " should complete successfully");
            }
        }
    }
}