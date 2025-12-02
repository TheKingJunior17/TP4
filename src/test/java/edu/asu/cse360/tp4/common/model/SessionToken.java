package edu.asu.cse360.tp4.common.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a session token for authenticated users in the system.
 * 
 * This class manages session information including the token value,
 * user details, expiration time, and security features for maintaining
 * secure user sessions across the application.
 * 
 * @author TP4 Team - Security Model Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
public class SessionToken {
    
    /** The actual token string */
    private final String token;
    
    /** Username of the authenticated user */
    private final String username;
    
    /** Role of the authenticated user */
    private final UserRole userRole;
    
    /** When this session was created */
    private final LocalDateTime creationTime;
    
    /** When this session expires */
    private final LocalDateTime expirationTime;
    
    /** Last time this session was accessed */
    private LocalDateTime lastAccessTime;
    
    /** Whether this session is currently active */
    private boolean active;
    
    /** IP address from which the session was created (optional) */
    private String originIpAddress;
    
    /** User agent string from the client (optional) */
    private String userAgent;
    
    /**
     * Constructor for creating a new session token.
     * 
     * @param token the unique token string
     * @param username the authenticated username
     * @param userRole the user's role
     * @param expirationTime when the session expires
     */
    public SessionToken(String token, String username, UserRole userRole, LocalDateTime expirationTime) {
        this.token = Objects.requireNonNull(token, "Token cannot be null");
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.userRole = Objects.requireNonNull(userRole, "User role cannot be null");
        this.expirationTime = Objects.requireNonNull(expirationTime, "Expiration time cannot be null");
        this.creationTime = LocalDateTime.now();
        this.lastAccessTime = this.creationTime;
        this.active = true;
    }
    
    /**
     * Checks if this session token has expired.
     * 
     * @return true if the session is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }
    
    /**
     * Updates the last access time to the current time.
     * This extends the session's effective lifetime for activity-based timeouts.
     */
    public void updateLastAccess() {
        this.lastAccessTime = LocalDateTime.now();
    }
    
    /**
     * Invalidates this session token.
     */
    public void invalidate() {
        this.active = false;
    }
    
    /**
     * Checks if this session is currently active and valid.
     * 
     * @return true if active and not expired
     */
    public boolean isValid() {
        return active && !isExpired();
    }
    
    /**
     * Gets the remaining time until expiration.
     * 
     * @return duration until expiration, or null if already expired
     */
    public long getMinutesUntilExpiration() {
        if (isExpired()) {
            return 0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        return java.time.Duration.between(now, expirationTime).toMinutes();
    }
    
    /**
     * Gets the session duration so far.
     * 
     * @return minutes since session creation
     */
    public long getSessionDurationMinutes() {
        LocalDateTime now = LocalDateTime.now();
        return java.time.Duration.between(creationTime, now).toMinutes();
    }
    
    /**
     * Sets additional security information for the session.
     * 
     * @param ipAddress the client's IP address
     * @param userAgent the client's user agent string
     */
    public void setSecurityInfo(String ipAddress, String userAgent) {
        this.originIpAddress = ipAddress;
        this.userAgent = userAgent;
    }
    
    /**
     * Validates that the session matches the expected security context.
     * 
     * @param ipAddress the current client's IP address
     * @param userAgent the current client's user agent
     * @return true if security context matches
     */
    public boolean validateSecurityContext(String ipAddress, String userAgent) {
        // If no security info was set, allow access
        if (originIpAddress == null && this.userAgent == null) {
            return true;
        }
        
        // Check IP address match (if available)
        boolean ipMatch = originIpAddress == null || Objects.equals(originIpAddress, ipAddress);
        
        // Check user agent match (if available)
        boolean userAgentMatch = this.userAgent == null || Objects.equals(this.userAgent, userAgent);
        
        return ipMatch && userAgentMatch;
    }
    
    // Getters
    
    public String getToken() {
        return token;
    }
    
    public String getUsername() {
        return username;
    }
    
    public UserRole getUserRole() {
        return userRole;
    }
    
    public LocalDateTime getCreationTime() {
        return creationTime;
    }
    
    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }
    
    public LocalDateTime getLastAccessTime() {
        return lastAccessTime;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public String getOriginIpAddress() {
        return originIpAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SessionToken that = (SessionToken) obj;
        return Objects.equals(token, that.token);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
    
    @Override
    public String toString() {
        return String.format(
            "SessionToken{token='%s', username='%s', role=%s, active=%s, expired=%s}",
            token.substring(0, Math.min(10, token.length())) + "...",
            username,
            userRole,
            active,
            isExpired()
        );
    }
}