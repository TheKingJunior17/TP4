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
 * Comprehensive test suite for StaffManagementService covering all staff functionality
 * including request filtering, decision history, content flagging, student search,
 * and reviewer role management.
 * 
 * @author TP4 Team - Testing Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StaffManagementServiceTest {
    
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
     * Test suite for reviewer request filtering functionality.
     */
    @Nested
    @DisplayName("Reviewer Request Filtering Tests")
    @Execution(ExecutionMode.CONCURRENT)
    class ReviewerRequestFilteringTests {
        
        @Test
        @Order(1)
        @DisplayName("testFilterRequestsRangeReturnsCorrect - Validates that only requests with dates inside the chosen range are included")
        void testFilterRequestsRangeReturnsCorrect() {
            // Arrange
            LocalDateTime startDate = LocalDateTime.now().minusDays(4);
            LocalDateTime endDate = LocalDateTime.now().minusDays(2);
            
            // Act
            List<ReviewerRequest> results = staffService.filterRequestsByDateRange(startDate, endDate);
            
            // Assert
            assertNotNull(results);
            assertTrue(results.size() > 0);
            
            // Verify all returned requests are within the date range
            for (ReviewerRequest request : results) {
                assertTrue(request.isWithinDateRange(startDate, endDate),
                    "Request " + request.getRequestId() + " is not within the specified date range");
            }
        }
        
        @Test
        @Order(2)
        @DisplayName("testFilterRequestsEmptyWhenNoneInRange - Ensures that if no requests fall within the range, the method returns an empty list")
        void testFilterRequestsEmptyWhenNoneInRange() {
            // Arrange - Use a date range that has no requests
            LocalDateTime startDate = LocalDateTime.now().minusYears(1);
            LocalDateTime endDate = LocalDateTime.now().minusMonths(11);
            
            // Act
            List<ReviewerRequest> results = staffService.filterRequestsByDateRange(startDate, endDate);
            
            // Assert
            assertNotNull(results);
            assertTrue(results.isEmpty(), "Expected empty list when no requests fall within the date range");
        }
        
        @Test
        @Order(3)
        @DisplayName("testFilterRequestsInclusiveBounds - Confirms that requests made exactly on the boundary dates are included")
        void testFilterRequestsInclusiveBounds() {
            // Arrange - Create a request with a specific date
            LocalDateTime requestDate = LocalDateTime.now().minusDays(2);
            ReviewerRequest boundaryRequest = new ReviewerRequest("test-student", "Test Student", "test@asu.edu", "Test request");
            boundaryRequest.setSubmissionDate(requestDate);
            staffRepository.addReviewerRequest(boundaryRequest);
            
            // Test with exact boundary dates
            LocalDateTime startDate = requestDate;
            LocalDateTime endDate = requestDate;
            
            // Act
            List<ReviewerRequest> results = staffService.filterRequestsByDateRange(startDate, endDate);
            
            // Assert
            assertNotNull(results);
            assertTrue(results.stream().anyMatch(r -> r.getStudentId().equals("test-student")),
                "Request on boundary date should be included");
        }
        
        @Test
        @Order(4)
        @DisplayName("testFilterRequestsSortedDesc - Ensures returned requests are automatically sorted from newest to oldest")
        void testFilterRequestsSortedDesc() {
            // Arrange - Get all requests
            LocalDateTime startDate = LocalDateTime.now().minusDays(10);
            LocalDateTime endDate = LocalDateTime.now();
            
            // Act
            List<ReviewerRequest> results = staffService.filterRequestsByDateRange(startDate, endDate);
            
            // Assert
            assertNotNull(results);
            assertTrue(results.size() > 1, "Need at least 2 requests to test sorting");
            
            // Verify sorting (newest first)
            for (int i = 1; i < results.size(); i++) {
                LocalDateTime previous = results.get(i - 1).getSubmissionDate();
                LocalDateTime current = results.get(i).getSubmissionDate();
                assertFalse(previous.isBefore(current),
                    "Requests should be sorted newest to oldest");
            }
        }
        
        @Test
        @Order(5)
        @DisplayName("testFilterRequestsInvalidRangeReturnsEmpty - If the date range is reversed or invalid, the system must return an empty result")
        void testFilterRequestsInvalidRangeReturnsEmpty() {
            // Arrange - Invalid range (start date after end date)
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.now().minusDays(5);
            
            // Act
            List<ReviewerRequest> results = staffService.filterRequestsByDateRange(startDate, endDate);
            
            // Assert
            assertNotNull(results);
            assertTrue(results.isEmpty(), "Invalid date range should return empty list");
        }
        
        @Test
        @Order(6)
        @DisplayName("testFilterRequestsMultipleEntries - Verifies that multiple valid requests in the range all appear in the output")
        void testFilterRequestsMultipleEntries() {
            // Arrange - Add multiple requests in the same date range
            LocalDateTime baseDate = LocalDateTime.now().minusDays(3);
            
            ReviewerRequest request1 = new ReviewerRequest("multi1", "Multi Student 1", "multi1@asu.edu", "Request 1");
            request1.setSubmissionDate(baseDate);
            staffRepository.addReviewerRequest(request1);
            
            ReviewerRequest request2 = new ReviewerRequest("multi2", "Multi Student 2", "multi2@asu.edu", "Request 2");
            request2.setSubmissionDate(baseDate.plusHours(1));
            staffRepository.addReviewerRequest(request2);
            
            ReviewerRequest request3 = new ReviewerRequest("multi3", "Multi Student 3", "multi3@asu.edu", "Request 3");
            request3.setSubmissionDate(baseDate.plusHours(2));
            staffRepository.addReviewerRequest(request3);
            
            // Set date range to include all test requests
            LocalDateTime startDate = baseDate.minusHours(1);
            LocalDateTime endDate = baseDate.plusHours(3);
            
            // Act
            List<ReviewerRequest> results = staffService.filterRequestsByDateRange(startDate, endDate);
            
            // Assert
            assertNotNull(results);
            assertTrue(results.size() >= 3, "Should include at least the 3 test requests");
            
            // Verify all test requests are included
            assertTrue(results.stream().anyMatch(r -> r.getStudentId().equals("multi1")));
            assertTrue(results.stream().anyMatch(r -> r.getStudentId().equals("multi2")));
            assertTrue(results.stream().anyMatch(r -> r.getStudentId().equals("multi3")));
        }
    }
    
    /**
     * Test suite for reviewer decision history functionality.
     */
    @Nested
    @DisplayName("Reviewer History Tests")
    class ReviewerHistoryTests {
        
        @Test
        @DisplayName("testHistorySingleApproval - Checks that one approval or denial record is stored and retrievable")
        void testHistorySingleApproval() {
            // Arrange - Process a single request to create history
            List<ReviewerRequest> requests = staffService.filterRequestsByDateRange(null, null);
            assertTrue(requests.size() > 0, "Need at least one request for testing");
            
            ReviewerRequest request = requests.get(0);
            
            // Act - Process the request
            boolean processed = staffService.processReviewerRequest(request.getRequestId(), true, "staff1", "Approved for testing");
            
            // Assert processing was successful
            assertTrue(processed);
            
            // Get history and verify
            List<ReviewerDecision> history = staffService.getReviewerDecisionHistory();
            assertNotNull(history);
            assertTrue(history.size() > 0, "History should contain at least one decision");
            
            // Find our decision
            ReviewerDecision decision = history.stream()
                .filter(d -> d.getRequestId().equals(request.getRequestId()))
                .findFirst()
                .orElse(null);
            
            assertNotNull(decision, "Should find the processed decision in history");
            assertEquals(ReviewerRequest.RequestStatus.APPROVED, decision.getDecision());
            assertEquals("staff1", decision.getStaffId());
            assertEquals("Approved for testing", decision.getDecisionRationale());
        }
        
        @Test
        @DisplayName("testHistoryEmptyWhenNone - Ensures an empty history returns an empty collection, not null")
        void testHistoryEmptyWhenNone() {
            // Arrange - Clear all data to ensure empty history
            staffRepository.clearAllData();
            
            // Act
            List<ReviewerDecision> history = staffService.getReviewerDecisionHistory();
            
            // Assert
            assertNotNull(history, "History should never be null");
            assertTrue(history.isEmpty(), "Empty history should return empty list");
        }
        
        @Test
        @DisplayName("testHistorySortedDesc - History entries must appear from most recent to oldest")
        void testHistorySortedDesc() {
            // Arrange - Create multiple decisions with different timestamps
            LocalDateTime baseTime = LocalDateTime.now().minusDays(1);
            
            ReviewerDecision decision1 = new ReviewerDecision(1L, "student1", "Student One", 
                ReviewerRequest.RequestStatus.APPROVED, "staff1", "Staff One", "First decision");
            decision1.setDecisionDate(baseTime);
            
            ReviewerDecision decision2 = new ReviewerDecision(2L, "student2", "Student Two", 
                ReviewerRequest.RequestStatus.DENIED, "staff2", "Staff Two", "Second decision");
            decision2.setDecisionDate(baseTime.plusHours(1));
            
            ReviewerDecision decision3 = new ReviewerDecision(3L, "student3", "Student Three", 
                ReviewerRequest.RequestStatus.APPROVED, "staff1", "Staff One", "Third decision");
            decision3.setDecisionDate(baseTime.plusHours(2));
            
            // Save decisions directly to repository
            try {
                staffRepository.saveReviewerDecision(decision1);
                staffRepository.saveReviewerDecision(decision2);
                staffRepository.saveReviewerDecision(decision3);
            } catch (SQLException e) {
                fail("Failed to save test decisions: " + e.getMessage());
            }
            
            // Act
            List<ReviewerDecision> history = staffService.getReviewerDecisionHistory();
            
            // Assert
            assertNotNull(history);
            assertTrue(history.size() >= 3, "Should have at least 3 decisions");
            
            // Verify sorting (newest first)
            for (int i = 1; i < history.size(); i++) {
                LocalDateTime previous = history.get(i - 1).getDecisionDate();
                LocalDateTime current = history.get(i).getDecisionDate();
                assertFalse(previous.isBefore(current), 
                    "History should be sorted newest to oldest");
            }
        }
        
        @Test
        @DisplayName("testHistoryMultipleReasons - Confirms that every approval/denial reason is properly saved and returned")
        void testHistoryMultipleReasons() {
            // Arrange - Process multiple requests with different reasons
            List<ReviewerRequest> requests = staffService.filterRequestsByDateRange(null, null);
            assertTrue(requests.size() >= 2, "Need at least 2 requests for testing");
            
            String approvalReason = "Student demonstrates excellent coding skills";
            String denialReason = "Student needs more experience before reviewing";
            
            // Act - Process requests with different outcomes and reasons
            boolean approved = staffService.processReviewerRequest(requests.get(0).getRequestId(), true, "staff1", approvalReason);
            boolean denied = staffService.processReviewerRequest(requests.get(1).getRequestId(), false, "staff2", denialReason);
            
            // Assert processing was successful
            assertTrue(approved);
            assertTrue(denied);
            
            // Get history and verify reasons
            List<ReviewerDecision> history = staffService.getReviewerDecisionHistory();
            assertNotNull(history);
            
            // Find our decisions and verify reasons are preserved
            boolean foundApproval = false;
            boolean foundDenial = false;
            
            for (ReviewerDecision decision : history) {
                if (decision.getRequestId().equals(requests.get(0).getRequestId())) {
                    assertEquals(approvalReason, decision.getDecisionRationale());
                    foundApproval = true;
                }
                if (decision.getRequestId().equals(requests.get(1).getRequestId())) {
                    assertEquals(denialReason, decision.getDecisionRationale());
                    foundDenial = true;
                }
            }
            
            assertTrue(foundApproval, "Should find approval decision with correct reason");
            assertTrue(foundDenial, "Should find denial decision with correct reason");
        }
        
        @Test
        @DisplayName("testServiceWrapsSQLException - Ensures SQLExceptions are caught and rethrown as RuntimeExceptions")
        void testServiceWrapsSQLException() {
            // Arrange - Enable failure simulation
            staffRepository.setSimulateFailure(true);
            
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                staffService.getReviewerDecisionHistory();
            });
            
            assertTrue(exception.getMessage().contains("Database error"), 
                "Exception should indicate database error");
            assertNotNull(exception.getCause(), "Should have an underlying cause");
            assertTrue(exception.getCause() instanceof SQLException, 
                "Underlying cause should be SQLException");
        }
        
        @Test
        @DisplayName("testHistoryDatabaseError - Simulates a failed table and verifies that attempting access results in an SQLException")
        void testHistoryDatabaseError() {
            // Arrange - Enable failure simulation
            staffRepository.setSimulateFailure(true);
            
            // Act & Assert - Should wrap SQLException in RuntimeException
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                staffService.getReviewerDecisionHistory();
            });
            
            // Verify the exception chain
            assertNotNull(exception.getCause());
            assertTrue(exception.getCause() instanceof SQLException);
            assertEquals("Simulated database failure for testing", exception.getCause().getMessage());
        }
    }
    
    /**
     * Test suite for content flagging functionality.
     */
    @Nested
    @DisplayName("Flagging Tests")
    class FlaggingTests {
        
        @Test
        @DisplayName("testFlagQuestionTrue - Ensures that marking a question as inappropriate succeeds")
        void testFlagQuestionTrue() {
            // Arrange - Get a question to flag
            Long questionId = 1L; // Test data includes question with ID 1
            String staffId = "staff1";
            
            // Act
            boolean result = staffService.flagQuestion(questionId, staffId, true);
            
            // Assert
            assertTrue(result, "Flagging question should succeed");
            
            // Verify the question is actually flagged
            try {
                Question question = staffRepository.getQuestionById(questionId);
                assertNotNull(question);
                assertTrue(question.isFlagged(), "Question should be flagged");
                assertEquals(staffId, question.getFlaggedByStaffId());
            } catch (SQLException e) {
                fail("Failed to verify question flag: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("testFlagQuestionFalse - Confirms the system can remove a flag from a previously flagged question")
        void testFlagQuestionFalse() {
            // Arrange - First flag a question
            Long questionId = 2L;
            String staffId = "staff1";
            
            boolean flagged = staffService.flagQuestion(questionId, staffId, true);
            assertTrue(flagged, "Initial flagging should succeed");
            
            // Act - Remove the flag
            boolean unflagged = staffService.flagQuestion(questionId, staffId, false);
            
            // Assert
            assertTrue(unflagged, "Unflagging question should succeed");
            
            // Verify the question is no longer flagged
            try {
                Question question = staffRepository.getQuestionById(questionId);
                assertNotNull(question);
                assertFalse(question.isFlagged(), "Question should not be flagged");
                assertNull(question.getFlaggedByStaffId());
            } catch (SQLException e) {
                fail("Failed to verify question unflag: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("testFlagAnswerTrue - Ensures answers can be flagged as inappropriate")
        void testFlagAnswerTrue() {
            // Arrange - Get an answer to flag
            Long answerId = 1L; // Test data includes answer with ID 1
            String staffId = "staff2";
            
            // Act
            boolean result = staffService.flagAnswer(answerId, staffId, true);
            
            // Assert
            assertTrue(result, "Flagging answer should succeed");
            
            // Verify the answer is actually flagged
            try {
                Answer answer = staffRepository.getAnswerById(answerId);
                assertNotNull(answer);
                assertTrue(answer.isFlagged(), "Answer should be flagged");
                assertEquals(staffId, answer.getFlaggedByStaffId());
            } catch (SQLException e) {
                fail("Failed to verify answer flag: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("testFlagAnswerFalse - Confirms unflagging works correctly for answers")
        void testFlagAnswerFalse() {
            // Arrange - First flag an answer
            Long answerId = 2L;
            String staffId = "staff2";
            
            boolean flagged = staffService.flagAnswer(answerId, staffId, true);
            assertTrue(flagged, "Initial flagging should succeed");
            
            // Act - Remove the flag
            boolean unflagged = staffService.flagAnswer(answerId, staffId, false);
            
            // Assert
            assertTrue(unflagged, "Unflagging answer should succeed");
            
            // Verify the answer is no longer flagged
            try {
                Answer answer = staffRepository.getAnswerById(answerId);
                assertNotNull(answer);
                assertFalse(answer.isFlagged(), "Answer should not be flagged");
                assertNull(answer.getFlaggedByStaffId());
            } catch (SQLException e) {
                fail("Failed to verify answer unflag: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("testFlagNonexistentQuestionDoesNothing - Verifies that flagging a missing question does not throw errors")
        void testFlagNonexistentQuestionDoesNothing() {
            // Arrange - Use a question ID that doesn't exist
            Long nonexistentQuestionId = 99999L;
            String staffId = "staff1";
            
            // Act & Assert - Should return false but not throw exception
            boolean result = staffService.flagQuestion(nonexistentQuestionId, staffId, true);
            assertFalse(result, "Flagging nonexistent question should return false");
            
            // Test with null ID as well
            boolean nullResult = staffService.flagQuestion(null, staffId, true);
            assertFalse(nullResult, "Flagging null question ID should return false");
        }
        
        @Test
        @DisplayName("testFlagNonexistentAnswerDoesNothing - Same as above, but for missing answers")
        void testFlagNonexistentAnswerDoesNothing() {
            // Arrange - Use an answer ID that doesn't exist
            Long nonexistentAnswerId = 99999L;
            String staffId = "staff1";
            
            // Act & Assert - Should return false but not throw exception
            boolean result = staffService.flagAnswer(nonexistentAnswerId, staffId, true);
            assertFalse(result, "Flagging nonexistent answer should return false");
            
            // Test with null ID as well
            boolean nullResult = staffService.flagAnswer(null, staffId, true);
            assertFalse(nullResult, "Flagging null answer ID should return false");
        }
    }
}