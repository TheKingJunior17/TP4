package edu.asu.cse360.tp4.common.exception;

/**
 * Exception thrown when data processing fails in the TP4 system.
 * 
 * @author TP4 Team
 * @version 2.0.0
 * @since TP4 Implementation Phase
 */
public class DataProcessingException extends Exception {
    
    public DataProcessingException(String message) {
        super(message);
    }
    
    public DataProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}