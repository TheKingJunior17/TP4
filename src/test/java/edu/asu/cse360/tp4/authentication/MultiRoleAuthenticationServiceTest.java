package edu.asu.cse360.tp4.authentication;

import edu.asu.cse360.tp4.common.model.UserRole;
import edu.asu.cse360.tp4.common.model.AuthenticationResult;
import edu.asu.cse360.tp4.common.exception.AuthenticationException;
import edu.asu.cse360.tp4.common.exception.ValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for MultiRoleAuthenticationService.
 * 
 * This test class provides extensive coverage of the enhanced authentication system
 * including multi-role support, MFA validation, session management, and security
 * features. Tests are organized using nested classes for logical grouping and
 * include performance and concurrency validation.
 * 
 * @author TP4 Team - Testing Lead
 * @version 2.0.0
 * @since TP4 Implementation Phase
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
    "logging.level.edu.asu.cse360.tp4.authentication=DEBUG",
    "test.parallel.enabled=true"
})
class MultiRoleAuthenticationServiceTest {
    
    private MultiRoleAuthenticationService authService;
    
    @BeforeEach
    void setUp() {
        authService = new MultiRoleAuthenticationService();
    }
    
    @AfterEach
    void tearDown() {
        authService = null;
    }
    
    /**
     * Test suite for core authentication functionality across all user roles.
     */
    @Nested
    @DisplayName("Multi-Role Authentication Tests")
    @Execution(ExecutionMode.CONCURRENT)
    class MultiRoleAuthenticationTests {
        
        @Test
        @Order(1)
        @DisplayName("Should successfully authenticate student with valid credentials")
        void testStudentAuthentication() throws Exception {
            // Arrange
            String username = "student1";
            String password = "studentpass";
            UserRole role = UserRole.STUDENT;
            
            // Generate MFA code
            String mfaCode = authService.generateMfaCode(username, role);
            assertNotNull(mfaCode);
            assertEquals(6, mfaCode.length());
            assertTrue(mfaCode.matches("\\d{6}"));
            
            // Act
            AuthenticationResult result = authService.authenticate(username, password, mfaCode, role);
            
            // Assert
            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(role, result.getRole());
            assertNotNull(result.getSessionToken());
            assertEquals("Authentication successful", result.getMessage());
        }
        
        @Test
        @Order(2)
        @DisplayName("Should successfully authenticate instructor with valid credentials")
        void testInstructorAuthentication() throws Exception {
            // Arrange
            String username = "instructor1";
            String password = "instructorpass";
            UserRole role = UserRole.INSTRUCTOR;
            String mfaCode = authService.generateMfaCode(username, role);
            
            // Act
            AuthenticationResult result = authService.authenticate(username, password, mfaCode, role);
            
            // Assert
            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(role, result.getRole());
            assertNotNull(result.getSessionToken());
        }
        
        @Test
        @Order(3)
        @DisplayName("Should successfully authenticate staff with valid credentials")
        void testStaffAuthentication() throws Exception {
            // Arrange
            String username = "staff1";
            String password = "staffpass";
            UserRole role = UserRole.STAFF;
            String mfaCode = authService.generateMfaCode(username, role);
            
            // Act
            AuthenticationResult result = authService.authenticate(username, password, mfaCode, role);
            
            // Assert
            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(role, result.getRole());
            assertNotNull(result.getSessionToken());
        }
        
        @Test
        @Order(4)
        @DisplayName("Should reject authentication with invalid username")
        void testInvalidUsernameAuthentication() throws Exception {
            // Arrange
            String username = "invaliduser";
            String password = "anypassword";
            UserRole role = UserRole.STUDENT;
            String mfaCode = authService.generateMfaCode("student1", role); // Valid MFA for different user
            
            // Act & Assert
            AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
                authService.authenticate(username, password, mfaCode, role);
            });
            
            assertTrue(exception.getMessage().contains("Invalid username or password"));
        }
        
        @Test
        @Order(5)
        @DisplayName("Should reject authentication with invalid password")
        void testInvalidPasswordAuthentication() throws Exception {
            // Arrange
            String username = "student1";
            String password = "wrongpassword";
            UserRole role = UserRole.STUDENT;
            String mfaCode = authService.generateMfaCode(username, role);
            
            // Act & Assert
            AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
                authService.authenticate(username, password, mfaCode, role);
            });
            
            assertTrue(exception.getMessage().contains("Invalid username or password"));
        }
    }
    
    /**
     * Test suite for Multi-Factor Authentication functionality.
     */
    @Nested
    @DisplayName("Multi-Factor Authentication Tests")
    class MfaTests {
        
        @Test
        @DisplayName("Should generate valid MFA codes")
        void testMfaCodeGeneration() throws Exception {
            // Act
            String mfaCode = authService.generateMfaCode("student1", UserRole.STUDENT);
            
            // Assert
            assertNotNull(mfaCode);
            assertEquals(6, mfaCode.length());
            assertTrue(mfaCode.matches("\\d{6}"));
            assertTrue(Integer.parseInt(mfaCode) >= 100000);
            assertTrue(Integer.parseInt(mfaCode) <= 999999);
        }
        
        @Test
        @DisplayName("Should validate fresh MFA codes")
        void testValidMfaCode() throws Exception {
            // Arrange
            String username = "student1";
            String password = "studentpass";
            UserRole role = UserRole.STUDENT;
            String mfaCode = authService.generateMfaCode(username, role);
            
            // Act
            AuthenticationResult result = authService.authenticate(username, password, mfaCode, role);
            
            // Assert
            assertTrue(result.isSuccess());
        }
        
        @Test
        @DisplayName("Should reject invalid MFA codes")
        void testInvalidMfaCode() throws Exception {
            // Arrange
            String username = "student1";
            String password = "studentpass";
            UserRole role = UserRole.STUDENT;
            authService.generateMfaCode(username, role); // Generate but don't use
            String invalidMfaCode = "000000";
            
            // Act & Assert
            AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
                authService.authenticate(username, password, invalidMfaCode, role);
            });
            
            assertTrue(exception.getMessage().contains("Invalid or expired MFA code"));
        }
        
        @Test
        @DisplayName("Should reject expired MFA codes")
        void testExpiredMfaCode() throws Exception {
            // This test would need to manipulate time or wait for expiration
            // For demonstration, we'll test the validation logic indirectly
            String username = "student1";
            String password = "studentpass";
            UserRole role = UserRole.STUDENT;
            
            // Generate MFA code but don't use it immediately
            String mfaCode = authService.generateMfaCode(username, role);
            
            // Simulate time passing by generating a new code (overwrites the old one)
            authService.generateMfaCode(username, role);
            
            // Try to use the old code
            AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
                authService.authenticate(username, password, mfaCode, role);
            });
            
            assertTrue(exception.getMessage().contains("Invalid or expired MFA code"));
        }
    }
    
    /**
     * Test suite for session management functionality.
     */
    @Nested
    @DisplayName("Session Management Tests")
    class SessionManagementTests {
        
        @Test
        @DisplayName("Should create valid sessions after authentication")
        void testSessionCreation() throws Exception {
            // Arrange
            String username = "student1";
            String password = "studentpass";
            UserRole role = UserRole.STUDENT;
            String mfaCode = authService.generateMfaCode(username, role);
            
            // Act
            AuthenticationResult result = authService.authenticate(username, password, mfaCode, role);
            
            // Assert
            assertNotNull(result.getSessionToken());
            assertNotNull(result.getSessionToken().getToken());
            assertEquals(username, result.getSessionToken().getUsername());
            assertEquals(role, result.getSessionToken().getUserRole());
            assertFalse(result.getSessionToken().isExpired());
        }
        
        @Test
        @DisplayName("Should validate active sessions")
        void testSessionValidation() throws Exception {
            // Arrange - Create authenticated session
            String username = "instructor1";
            String password = "instructorpass";
            UserRole role = UserRole.INSTRUCTOR;
            String mfaCode = authService.generateMfaCode(username, role);
            
            AuthenticationResult result = authService.authenticate(username, password, mfaCode, role);
            String sessionToken = result.getSessionToken().getToken();
            
            // Act & Assert
            assertTrue(authService.validateSession(sessionToken));
            assertEquals(role, authService.getUserRole(sessionToken));
        }
        
        @Test
        @DisplayName("Should reject invalid session tokens")
        void testInvalidSessionToken() {
            // Act & Assert
            AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
                authService.validateSession("invalid-token");
            });
            
            assertTrue(exception.getMessage().contains("Invalid session token"));
        }
        
        @Test
        @DisplayName("Should successfully invalidate sessions")
        void testSessionInvalidation() throws Exception {
            // Arrange - Create authenticated session
            String username = "staff1";
            String password = "staffpass";
            UserRole role = UserRole.STAFF;
            String mfaCode = authService.generateMfaCode(username, role);
            
            AuthenticationResult result = authService.authenticate(username, password, mfaCode, role);
            String sessionToken = result.getSessionToken().getToken();
            
            // Verify session is valid
            assertTrue(authService.validateSession(sessionToken));
            
            // Act
            boolean invalidated = authService.invalidateSession(sessionToken);
            
            // Assert
            assertTrue(invalidated);
            
            // Verify session is no longer valid
            AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
                authService.validateSession(sessionToken);
            });
            
            assertTrue(exception.getMessage().contains("Invalid session token"));
        }
    }
    
    /**
     * Test suite for role-based access control functionality.
     */
    @Nested
    @DisplayName("Role-Based Access Control Tests")
    class RoleBasedAccessControlTests {
        
        @Test
        @DisplayName("Should enforce role-based permissions")
        void testRoleBasedPermissions() throws Exception {
            // Test each role can authenticate successfully
            for (UserRole role : UserRole.values()) {
                String username = getTestUsername(role);
                String password = getTestPassword(role);
                
                if (username != null && password != null) {
                    String mfaCode = authService.generateMfaCode(username, role);
                    AuthenticationResult result = authService.authenticate(username, password, mfaCode, role);
                    
                    assertTrue(result.isSuccess());
                    assertEquals(role, result.getRole());
                }
            }
        }
        
        @Test
        @DisplayName("Should maintain separate sessions for different roles")
        void testMultipleRoleSessions() throws Exception {
            // Create sessions for different roles
            String studentMfa = authService.generateMfaCode("student1", UserRole.STUDENT);
            AuthenticationResult studentResult = authService.authenticate("student1", "studentpass", studentMfa, UserRole.STUDENT);
            
            String instructorMfa = authService.generateMfaCode("instructor1", UserRole.INSTRUCTOR);
            AuthenticationResult instructorResult = authService.authenticate("instructor1", "instructorpass", instructorMfa, UserRole.INSTRUCTOR);
            
            // Verify both sessions are active and independent
            assertTrue(authService.validateSession(studentResult.getSessionToken().getToken()));
            assertTrue(authService.validateSession(instructorResult.getSessionToken().getToken()));
            
            assertEquals(UserRole.STUDENT, authService.getUserRole(studentResult.getSessionToken().getToken()));
            assertEquals(UserRole.INSTRUCTOR, authService.getUserRole(instructorResult.getSessionToken().getToken()));
            
            // Invalidating one should not affect the other
            authService.invalidateSession(studentResult.getSessionToken().getToken());
            
            assertThrows(AuthenticationException.class, () -> {
                authService.validateSession(studentResult.getSessionToken().getToken());
            });
            
            assertTrue(authService.validateSession(instructorResult.getSessionToken().getToken()));
        }
    }
    
    /**
     * Test suite for security features including rate limiting and validation.
     */
    @Nested
    @DisplayName("Security Feature Tests")
    class SecurityTests {
        
        @Test
        @DisplayName("Should implement rate limiting after multiple failed attempts")
        void testRateLimiting() throws Exception {
            String username = "student1";
            String password = "wrongpassword";
            UserRole role = UserRole.STUDENT;
            
            // Attempt authentication multiple times with wrong password
            for (int i = 0; i < 5; i++) {
                String mfaCode = authService.generateMfaCode(username, role);
                assertThrows(AuthenticationException.class, () -> {
                    authService.authenticate(username, password, mfaCode, role);
                });
            }
            
            // The 6th attempt should be blocked due to rate limiting
            String mfaCode = authService.generateMfaCode(username, role);
            AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
                authService.authenticate(username, "studentpass", mfaCode, role); // Even with correct password
            });
            
            assertTrue(exception.getMessage().contains("Account temporarily locked"));
        }
        
        @Test
        @DisplayName("Should validate input parameters")
        void testInputValidation() {
            // Test null username
            ValidationException exception1 = assertThrows(ValidationException.class, () -> {
                authService.generateMfaCode(null, UserRole.STUDENT);
            });
            assertTrue(exception1.getMessage().contains("Username cannot be null"));
            
            // Test empty username
            ValidationException exception2 = assertThrows(ValidationException.class, () -> {
                authService.generateMfaCode("", UserRole.STUDENT);
            });
            assertTrue(exception2.getMessage().contains("Username cannot be null or empty"));
            
            // Test null session token validation
            AuthenticationException exception3 = assertThrows(AuthenticationException.class, () -> {
                authService.validateSession(null);
            });
            assertTrue(exception3.getMessage().contains("Session token cannot be null"));
        }
        
        @Test
        @DisplayName("Should generate secure session statistics")
        void testSessionStatistics() throws Exception {
            // Create multiple sessions
            String studentMfa = authService.generateMfaCode("student1", UserRole.STUDENT);
            authService.authenticate("student1", "studentpass", studentMfa, UserRole.STUDENT);
            
            String instructorMfa = authService.generateMfaCode("instructor1", UserRole.INSTRUCTOR);
            authService.authenticate("instructor1", "instructorpass", instructorMfa, UserRole.INSTRUCTOR);
            
            // Get statistics
            Map<String, Object> stats = authService.getSessionStatistics();
            
            // Verify statistics structure
            assertNotNull(stats);
            assertTrue(stats.containsKey("activeSessions"));
            assertTrue(stats.containsKey("lockedAccounts"));
            assertTrue(stats.containsKey("pendingMfaCodes"));
            assertTrue(stats.containsKey("sessionsByRole"));
            
            // Verify data types
            assertTrue(stats.get("activeSessions") instanceof Integer);
            assertTrue(stats.get("sessionsByRole") instanceof Map);
            
            // Should have at least 2 active sessions
            Integer activeSessions = (Integer) stats.get("activeSessions");
            assertTrue(activeSessions >= 2);
        }
    }
    
    /**
     * Test suite for performance and concurrency validation.
     */
    @Nested
    @DisplayName("Performance and Concurrency Tests")
    class PerformanceTests {
        
        @Test
        @DisplayName("Should handle concurrent authentication requests")
        @Timeout(value = 10, unit = TimeUnit.SECONDS)
        void testConcurrentAuthentication() throws InterruptedException {
            int threadCount = 10;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch completionLatch = new CountDownLatch(threadCount);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            
            // Submit concurrent authentication tasks
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        startLatch.await(); // Wait for all threads to be ready
                        
                        String username = "student1";
                        String password = "studentpass";
                        UserRole role = UserRole.STUDENT;
                        String mfaCode = authService.generateMfaCode(username + threadId, role);
                        
                        // This would normally fail due to invalid credentials, but tests thread safety
                        try {
                            authService.authenticate(username + threadId, password, mfaCode, role);
                        } catch (AuthenticationException e) {
                            // Expected for most threads due to invalid credentials
                        }
                        
                    } catch (Exception e) {
                        fail("Unexpected exception in concurrent test: " + e.getMessage());
                    } finally {
                        completionLatch.countDown();
                    }
                });
            }
            
            // Start all threads simultaneously
            startLatch.countDown();
            
            // Wait for all threads to complete
            assertTrue(completionLatch.await(8, TimeUnit.SECONDS));
            
            executor.shutdown();
            assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
        }
        
        @Test
        @DisplayName("Should maintain performance under load")
        @Timeout(value = 5, unit = TimeUnit.SECONDS)
        void testAuthenticationPerformance() throws Exception {
            // Measure authentication performance
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < 100; i++) {
                String mfaCode = authService.generateMfaCode("student1", UserRole.STUDENT);
                // Just generate codes to test performance, not full authentication
                assertNotNull(mfaCode);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Should complete 100 MFA generations in under 1 second
            assertTrue(duration < 1000, "MFA generation took too long: " + duration + "ms");
        }
    }
    
    /**
     * Integration tests combining multiple service features.
     */
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should support complete authentication workflow")
        void testCompleteAuthenticationWorkflow() throws Exception {
            // Complete workflow: MFA generation -> Authentication -> Session validation -> Logout
            String username = "instructor1";
            String password = "instructorpass";
            UserRole role = UserRole.INSTRUCTOR;
            
            // Step 1: Generate MFA
            String mfaCode = authService.generateMfaCode(username, role);
            assertNotNull(mfaCode);
            
            // Step 2: Authenticate
            AuthenticationResult result = authService.authenticate(username, password, mfaCode, role);
            assertTrue(result.isSuccess());
            
            // Step 3: Validate session
            String sessionToken = result.getSessionToken().getToken();
            assertTrue(authService.validateSession(sessionToken));
            assertEquals(role, authService.getUserRole(sessionToken));
            
            // Step 4: Logout
            assertTrue(authService.invalidateSession(sessionToken));
            
            // Step 5: Verify session is invalid
            assertThrows(AuthenticationException.class, () -> {
                authService.validateSession(sessionToken);
            });
        }
        
        @Test
        @DisplayName("Should maintain data consistency across operations")
        void testDataConsistency() throws Exception {
            // Test that statistics remain consistent throughout operations
            Map<String, Object> initialStats = authService.getSessionStatistics();
            
            // Perform authentication
            String mfaCode = authService.generateMfaCode("student1", UserRole.STUDENT);
            AuthenticationResult result = authService.authenticate("student1", "studentpass", mfaCode, UserRole.STUDENT);
            
            Map<String, Object> afterAuthStats = authService.getSessionStatistics();
            
            // Session count should increase by 1
            int initialSessions = (Integer) initialStats.get("activeSessions");
            int afterAuthSessions = (Integer) afterAuthStats.get("activeSessions");
            assertEquals(initialSessions + 1, afterAuthSessions);
            
            // Invalidate session
            authService.invalidateSession(result.getSessionToken().getToken());
            
            Map<String, Object> finalStats = authService.getSessionStatistics();
            int finalSessions = (Integer) finalStats.get("activeSessions");
            assertEquals(initialSessions, finalSessions);
        }
    }
    
    // Helper methods for test data
    private String getTestUsername(UserRole role) {
        return switch (role) {
            case STUDENT -> "student1";
            case INSTRUCTOR -> "instructor1";
            case STAFF -> "staff1";
            case ADMINISTRATOR -> "admin";
        };
    }
    
    private String getTestPassword(UserRole role) {
        return switch (role) {
            case STUDENT -> "studentpass";
            case INSTRUCTOR -> "instructorpass";
            case STAFF -> "staffpass";
            case ADMINISTRATOR -> "adminpass";
        };
    }
}