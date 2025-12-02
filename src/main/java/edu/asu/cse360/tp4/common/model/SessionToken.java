package edu.asu.cse360.tp4.common.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents an authenticated session token in the TP4 system.
 * 
 * Session tokens are used to maintain user authentication state across
 * multiple requests and provide secure access to role-specific resources
 * with automatic timeout and activity tracking.
 * 
 * @author TP4 Team
 * @version 2.0.0
 * @since TP4 Implementation Phase
 */
public class SessionToken {
    
    private final String token;
    private final String username;
    private final UserRole userRole;
    private final Instant createdAt;
    private final Instant expiresAt;
    private Instant lastActivity;
    
    /**
     * Creates a new session token.
     * 
     * @param token the unique token string
     * @param username the authenticated username
     * @param userRole the user's role
     * @param createdAt when the session was created
     * @param expiresAt when the session expires
     */
    public SessionToken(String token, String username, UserRole userRole, 
                       Instant createdAt, Instant expiresAt) {
        this.token = Objects.requireNonNull(token, "Token cannot be null");
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.userRole = Objects.requireNonNull(userRole, "User role cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created timestamp cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expiration timestamp cannot be null");
        this.lastActivity = createdAt;
    }
    
    /**
     * Updates the last activity timestamp to extend the session.
     */
    public void updateLastActivity() {
        this.lastActivity = Instant.now();
    }
    
    /**
     * Checks if the session has expired.
     * 
     * @return true if the session is expired
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    /**
     * Gets the time since last activity in minutes.
     * 
     * @return minutes since last activity
     */
    public long getMinutesSinceLastActivity() {
        return ChronoUnit.MINUTES.between(lastActivity, Instant.now());
    }
    
    /**
     * Gets the remaining session duration in minutes.
     * 
     * @return minutes until expiration (negative if expired)
     */
    public long getRemainingMinutes() {
        return ChronoUnit.MINUTES.between(Instant.now(), expiresAt);
    }
    
    // Getters
    public String getToken() { return token; }
    public String getUsername() { return username; }
    public UserRole getUserRole() { return userRole; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getLastActivity() { return lastActivity; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionToken that = (SessionToken) o;
        return Objects.equals(token, that.token);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
    
    @Override
    public String toString() {
        return "SessionToken{" +
                "username='" + username + '\'' +
                ", userRole=" + userRole +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", isExpired=" + isExpired() +
                '}';
    }
}