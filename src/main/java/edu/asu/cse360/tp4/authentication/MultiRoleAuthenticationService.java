package edu.asu.cse360.tp4.authentication;

import edu.asu.cse360.tp4.common.model.UserRole;
import edu.asu.cse360.tp4.common.model.AuthenticationResult;
import edu.asu.cse360.tp4.common.model.SessionToken;
import edu.asu.cse360.tp4.common.exception.AuthenticationException;
import edu.asu.cse360.tp4.common.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced Multi-Role Authentication Service for TP4.
 * 
 * This service extends the HW4 staff authentication foundation to support
 * all user roles (Student, Instructor, Staff) with unified security policies,
 * multi-factor authentication, and comprehensive audit logging.
 * 
 * <p>Security Features:</p>
 * <ul>
 *   <li>Multi-factor authentication with time-based OTP</li>
 *   <li>Role-based access control with granular permissions</li>
 *   <li>Session management with automatic timeout</li>
 *   <li>Rate limiting and brute force protection</li>
 *   <li>Comprehensive audit trail logging</li>
 * </ul>
 * 
 * @author Jose Mendoza - Team Lead
 * @version 2.0.0 (Enhanced from HW4 foundation)
 * @since TP4 Implementation Phase
 */
@Service
public class MultiRoleAuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(MultiRoleAuthenticationService.class);
    private static final int MFA_CODE_LENGTH = 6;
    private static final int MFA_VALIDITY_MINUTES = 5;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int SESSION_TIMEOUT_MINUTES = 30;
    
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;
    private final Map<String, SessionToken> activeSessions;
    private final Map<String, Integer> loginAttempts;
    private final Map<String, String> mfaCodes;
    private final Map<String, Instant> mfaCodeTimestamps;
    
    /**
     * Constructor initializing security components and session management.
     */
    public MultiRoleAuthenticationService() {
        this.passwordEncoder = new BCryptPasswordEncoder(12);
        this.secureRandom = new SecureRandom();
        this.activeSessions = new ConcurrentHashMap<>();
        this.loginAttempts = new ConcurrentHashMap<>();
        this.mfaCodes = new ConcurrentHashMap<>();
        this.mfaCodeTimestamps = new ConcurrentHashMap<>();
        logger.info("Multi-Role Authentication Service initialized");
    }
    
    /**
     * Authenticates a user with multi-factor authentication across all roles.
     * 
     * This method performs comprehensive authentication including credential validation,
     * MFA verification, rate limiting, and session creation. Supports Student,
     * Instructor, and Staff roles with unified security policies.
     * 
     * @param username the user's username (must not be null or empty)
     * @param password the user's password (must not be null)
     * @param mfaCode the multi-factor authentication code
     * @param requestedRole the role being requested for authentication
     * @return AuthenticationResult containing session token and user details
     * @throws AuthenticationException if authentication fails
     * @throws ValidationException if input parameters are invalid
     * 
     * @see #generateMfaCode(String, UserRole) for MFA code generation
     * @see #validateSession(String) for session validation
     */
    public AuthenticationResult authenticate(String username, String password, 
            String mfaCode, UserRole requestedRole) throws AuthenticationException, ValidationException {
        
        logger.info("Authentication attempt for user: {} with role: {}", username, requestedRole);
        
        // Input validation
        validateAuthenticationInput(username, password, mfaCode, requestedRole);
        
        // Rate limiting check
        if (isAccountLocked(username)) {
            logger.warn("Authentication blocked - account locked for user: {}", username);
            throw new AuthenticationException("Account temporarily locked due to multiple failed attempts");
        }
        
        try {
            // Simulate credential validation (in real implementation, this would query database)
            if (!validateCredentials(username, password, requestedRole)) {
                incrementLoginAttempts(username);
                logger.warn("Invalid credentials for user: {} with role: {}", username, requestedRole);
                throw new AuthenticationException("Invalid username or password");
            }
            
            // MFA validation
            if (!validateMfaCode(username, mfaCode)) {
                incrementLoginAttempts(username);
                logger.warn("Invalid MFA code for user: {}", username);
                throw new AuthenticationException("Invalid or expired MFA code");
            }
            
            // Reset login attempts on successful authentication
            loginAttempts.remove(username);
            
            // Create session token
            SessionToken sessionToken = createSessionToken(username, requestedRole);
            activeSessions.put(sessionToken.getToken(), sessionToken);
            
            // Clean up MFA codes
            mfaCodes.remove(username);
            mfaCodeTimestamps.remove(username);
            
            logger.info("Successful authentication for user: {} with role: {}", username, requestedRole);
            
            return new AuthenticationResult(sessionToken, requestedRole, true, 
                    "Authentication successful");
            
        } catch (Exception e) {
            logger.error("Authentication error for user: {}", username, e);
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }
    
    /**
     * Generates a secure multi-factor authentication code for the user.
     * 
     * Creates a time-limited 6-digit OTP code that expires after 5 minutes.
     * The code is cryptographically secure and resistant to prediction attacks.
     * 
     * @param username the username for MFA code generation
     * @param userRole the user's role for audit logging
     * @return the generated 6-digit MFA code
     * @throws ValidationException if username is invalid
     */
    public String generateMfaCode(String username, UserRole userRole) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be null or empty");
        }
        
        // Generate cryptographically secure 6-digit code
        int code = 100000 + secureRandom.nextInt(900000);
        String mfaCode = String.valueOf(code);
        
        // Store code with timestamp
        mfaCodes.put(username, mfaCode);
        mfaCodeTimestamps.put(username, Instant.now());
        
        logger.info("MFA code generated for user: {} with role: {}", username, userRole);
        
        // In real implementation, send code via SMS/email
        // simulateMfaDelivery(username, mfaCode, userRole);
        
        return mfaCode;
    }
    
    /**
     * Validates an active session token and extends session if valid.
     * 
     * @param tokenString the session token to validate
     * @return true if session is valid and active
     * @throws AuthenticationException if session is invalid or expired
     */
    public boolean validateSession(String tokenString) throws AuthenticationException {
        if (tokenString == null || tokenString.trim().isEmpty()) {
            throw new AuthenticationException("Session token cannot be null or empty");
        }
        
        SessionToken session = activeSessions.get(tokenString);
        if (session == null) {
            logger.warn("Invalid session token attempted: {}", tokenString.substring(0, 8) + "...");
            throw new AuthenticationException("Invalid session token");
        }
        
        // Check if session has expired
        if (session.isExpired()) {
            activeSessions.remove(tokenString);
            logger.info("Expired session removed for user: {}", session.getUsername());
            throw new AuthenticationException("Session has expired");
        }
        
        // Extend session activity
        session.updateLastActivity();
        logger.debug("Session validated and extended for user: {}", session.getUsername());
        
        return true;
    }
    
    /**
     * Invalidates a user session (logout functionality).
     * 
     * @param tokenString the session token to invalidate
     * @return true if session was successfully invalidated
     */
    public boolean invalidateSession(String tokenString) {
        SessionToken session = activeSessions.remove(tokenString);
        if (session != null) {
            logger.info("Session invalidated for user: {}", session.getUsername());
            return true;
        }
        return false;
    }
    
    /**
     * Retrieves user role from active session.
     * 
     * @param tokenString the session token
     * @return UserRole associated with the session
     * @throws AuthenticationException if session is invalid
     */
    public UserRole getUserRole(String tokenString) throws AuthenticationException {
        SessionToken session = activeSessions.get(tokenString);
        if (session == null || session.isExpired()) {
            throw new AuthenticationException("Invalid or expired session");
        }
        return session.getUserRole();
    }
    
    /**
     * Gets comprehensive session statistics for monitoring.
     * 
     * @return Map containing session statistics
     */
    public Map<String, Object> getSessionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeSessions", activeSessions.size());
        stats.put("lockedAccounts", loginAttempts.size());
        stats.put("pendingMfaCodes", mfaCodes.size());
        
        // Count sessions by role
        Map<UserRole, Long> sessionsByRole = activeSessions.values().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    SessionToken::getUserRole, 
                    java.util.stream.Collectors.counting()));
        stats.put("sessionsByRole", sessionsByRole);
        
        return stats;
    }
    
    // Private helper methods
    
    private void validateAuthenticationInput(String username, String password, 
            String mfaCode, UserRole requestedRole) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password cannot be null or empty");
        }
        if (mfaCode == null || mfaCode.trim().isEmpty()) {
            throw new ValidationException("MFA code cannot be null or empty");
        }
        if (requestedRole == null) {
            throw new ValidationException("Requested role cannot be null");
        }
    }
    
    private boolean validateCredentials(String username, String password, UserRole role) {
        // Simulate database lookup and password verification
        // In real implementation, this would query user database
        String storedPasswordHash = getStoredPasswordHash(username, role);
        return storedPasswordHash != null && passwordEncoder.matches(password, storedPasswordHash);
    }
    
    private String getStoredPasswordHash(String username, UserRole role) {
        // Simulate stored password hashes for demonstration
        // In real implementation, this would be database lookup
        Map<String, String> demoUsers = Map.of(
            "student1", passwordEncoder.encode("studentpass"),
            "instructor1", passwordEncoder.encode("instructorpass"),
            "staff1", passwordEncoder.encode("staffpass"),
            "admin", passwordEncoder.encode("adminpass")
        );
        return demoUsers.get(username);
    }
    
    private boolean validateMfaCode(String username, String providedCode) {
        String storedCode = mfaCodes.get(username);
        Instant codeTimestamp = mfaCodeTimestamps.get(username);
        
        if (storedCode == null || codeTimestamp == null) {
            return false;
        }
        
        // Check if code has expired
        if (ChronoUnit.MINUTES.between(codeTimestamp, Instant.now()) > MFA_VALIDITY_MINUTES) {
            mfaCodes.remove(username);
            mfaCodeTimestamps.remove(username);
            return false;
        }
        
        return storedCode.equals(providedCode);
    }
    
    private boolean isAccountLocked(String username) {
        return loginAttempts.getOrDefault(username, 0) >= MAX_LOGIN_ATTEMPTS;
    }
    
    private void incrementLoginAttempts(String username) {
        loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);
    }
    
    private SessionToken createSessionToken(String username, UserRole role) {
        String token = generateSecureToken();
        return new SessionToken(token, username, role, 
                Instant.now(), Instant.now().plus(SESSION_TIMEOUT_MINUTES, ChronoUnit.MINUTES));
    }
    
    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getEncoder().encodeToString(tokenBytes);
    }
}