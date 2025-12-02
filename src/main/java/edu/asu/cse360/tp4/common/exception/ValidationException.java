package edu.asu.cse360.tp4.common.exception;

/**
 * Exception thrown when validation fails in the TP4 system.
 * 
 * @author TP4 Team
 * @version 2.0.0
 * @since TP4 Implementation Phase
 */
public class ValidationException extends Exception {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}