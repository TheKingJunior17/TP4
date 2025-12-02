package edu.asu.cse360.tp4.common.exception;

/**
 * Exception thrown when authentication fails in the TP4 system.
 * 
 * @author TP4 Team
 * @version 2.0.0
 * @since TP4 Implementation Phase
 */
public class AuthenticationException extends Exception {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}