package edu.asu.cse360.tp4.common.model;

/**
 * Represents the result of an authentication attempt in the system.
 * 
 * This class encapsulates all information returned from an authentication
 * operation including success status, user role, session information,
 * and any relevant messages or error details.
 * 
 * @author TP4 Team - Authentication Model Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
public class AuthenticationResult {
    
    /** Whether the authentication was successful */
    private final boolean success;
    
    /** Message describing the result */
    private final String message;
    
    /** The authenticated user's role */
    private final UserRole role;
    
    /** Session token for successful authentication */
    private final SessionToken sessionToken;
    
    /** Error code for failed authentication */
    private final String errorCode;
    
    /** Additional context or details */
    private final String details;
    
    /**
     * Constructor for successful authentication result.
     * 
     * @param success whether authentication succeeded
     * @param message descriptive message
     * @param role the authenticated user's role
     * @param sessionToken the created session token
     */
    public AuthenticationResult(boolean success, String message, UserRole role, SessionToken sessionToken) {
        this.success = success;
        this.message = message;
        this.role = role;
        this.sessionToken = sessionToken;
        this.errorCode = null;
        this.details = null;
    }
    
    /**
     * Constructor for failed authentication result.
     * 
     * @param success whether authentication succeeded (should be false)
     * @param message descriptive error message
     * @param errorCode specific error code
     * @param details additional error details
     */
    public AuthenticationResult(boolean success, String message, String errorCode, String details) {
        this.success = success;
        this.message = message;
        this.role = null;
        this.sessionToken = null;
        this.errorCode = errorCode;
        this.details = details;
    }
    
    /**
     * Creates a successful authentication result.
     * 
     * @param role the authenticated user's role
     * @param sessionToken the created session token
     * @return successful authentication result
     */
    public static AuthenticationResult success(UserRole role, SessionToken sessionToken) {
        return new AuthenticationResult(true, "Authentication successful", role, sessionToken);
    }
    
    /**
     * Creates a failed authentication result with error details.
     * 
     * @param message error message
     * @param errorCode specific error code
     * @return failed authentication result
     */
    public static AuthenticationResult failure(String message, String errorCode) {
        return new AuthenticationResult(false, message, errorCode, null);
    }
    
    /**
     * Creates a failed authentication result with message only.
     * 
     * @param message error message
     * @return failed authentication result
     */
    public static AuthenticationResult failure(String message) {
        return new AuthenticationResult(false, message, "AUTH_FAILED", null);
    }
    
    /**
     * Checks if authentication was successful.
     * 
     * @return true if authentication succeeded
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Checks if authentication failed.
     * 
     * @return true if authentication failed
     */
    public boolean isFailure() {
        return !success;
    }
    
    /**
     * Gets the descriptive message.
     * 
     * @return result message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the authenticated user's role.
     * Only available for successful authentication.
     * 
     * @return user role, or null if authentication failed
     */
    public UserRole getRole() {
        return role;
    }
    
    /**
     * Gets the session token for successful authentication.
     * 
     * @return session token, or null if authentication failed
     */
    public SessionToken getSessionToken() {
        return sessionToken;
    }
    
    /**
     * Gets the error code for failed authentication.
     * 
     * @return error code, or null if authentication succeeded
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Gets additional error details.
     * 
     * @return error details, or null if not available
     */
    public String getDetails() {
        return details;
    }
    
    /**
     * Gets a comprehensive status summary.
     * 
     * @return formatted status string
     */
    public String getStatusSummary() {
        if (success) {
            return String.format("SUCCESS: %s (Role: %s, Token: %s)", 
                               message, 
                               role, 
                               sessionToken != null ? sessionToken.getToken().substring(0, 10) + "..." : "null");
        } else {
            return String.format("FAILURE: %s (Code: %s)", 
                               message, 
                               errorCode != null ? errorCode : "UNKNOWN");
        }
    }
    
    /**
     * Checks if the user has a specific role.
     * 
     * @param expectedRole the role to check for
     * @return true if user has the expected role
     */
    public boolean hasRole(UserRole expectedRole) {
        return success && role != null && role.equals(expectedRole);
    }
    
    /**
     * Checks if the user has any of the specified roles.
     * 
     * @param allowedRoles roles to check against
     * @return true if user has any of the allowed roles
     */
    public boolean hasAnyRole(UserRole... allowedRoles) {
        if (!success || role == null) {
            return false;
        }
        
        for (UserRole allowedRole : allowedRoles) {
            if (role.equals(allowedRole)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return getStatusSummary();
    }
}