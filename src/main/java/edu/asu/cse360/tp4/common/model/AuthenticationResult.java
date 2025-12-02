package edu.asu.cse360.tp4.common.model;

/**
 * Represents the result of an authentication attempt.
 * 
 * @author TP4 Team
 * @version 2.0.0
 * @since TP4 Implementation Phase
 */
public class AuthenticationResult {
    
    private final SessionToken sessionToken;
    private final UserRole role;
    private final boolean success;
    private final String message;
    
    public AuthenticationResult(SessionToken sessionToken, UserRole role, 
                              boolean success, String message) {
        this.sessionToken = sessionToken;
        this.role = role;
        this.success = success;
        this.message = message;
    }
    
    public SessionToken getSessionToken() { return sessionToken; }
    public UserRole getRole() { return role; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}