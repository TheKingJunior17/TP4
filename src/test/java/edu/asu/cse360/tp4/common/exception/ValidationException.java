package edu.asu.cse360.tp4.common.exception;

/**
 * Custom exception for validation errors in the TP4 system.
 * 
 * This exception is thrown when input validation fails, such as
 * null or invalid parameters, incorrect data formats, or business
 * rule violations during validation.
 * 
 * @author TP4 Team - Exception Handling Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
public class ValidationException extends RuntimeException {
    
    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;
    
    /** The field or parameter that caused the validation error */
    private final String field;
    
    /** The invalid value that caused the error */
    private final Object invalidValue;
    
    /** Error code for programmatic handling */
    private final String errorCode;
    
    /**
     * Constructs a new ValidationException with the specified detail message.
     * 
     * @param message the detail message explaining the validation error
     */
    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.invalidValue = null;
        this.errorCode = "VALIDATION_ERROR";
    }
    
    /**
     * Constructs a new ValidationException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the validation error
     * @param cause the cause of the validation error
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.invalidValue = null;
        this.errorCode = "VALIDATION_ERROR";
    }
    
    /**
     * Constructs a new ValidationException with detailed field information.
     * 
     * @param message the detail message explaining the validation error
     * @param field the field that failed validation
     * @param invalidValue the invalid value that caused the error
     */
    public ValidationException(String message, String field, Object invalidValue) {
        super(message);
        this.field = field;
        this.invalidValue = invalidValue;
        this.errorCode = "FIELD_VALIDATION_ERROR";
    }
    
    /**
     * Constructs a new ValidationException with detailed field information and error code.
     * 
     * @param message the detail message explaining the validation error
     * @param field the field that failed validation
     * @param invalidValue the invalid value that caused the error
     * @param errorCode specific error code for programmatic handling
     */
    public ValidationException(String message, String field, Object invalidValue, String errorCode) {
        super(message);
        this.field = field;
        this.invalidValue = invalidValue;
        this.errorCode = errorCode != null ? errorCode : "VALIDATION_ERROR";
    }
    
    /**
     * Gets the field that caused the validation error.
     * 
     * @return the field name, or null if not specified
     */
    public String getField() {
        return field;
    }
    
    /**
     * Gets the invalid value that caused the validation error.
     * 
     * @return the invalid value, or null if not specified
     */
    public Object getInvalidValue() {
        return invalidValue;
    }
    
    /**
     * Gets the error code for programmatic handling.
     * 
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Gets a detailed error message including field and value information.
     * 
     * @return formatted error message with additional details
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder(getMessage());
        
        if (field != null) {
            sb.append(" (Field: ").append(field);
            if (invalidValue != null) {
                sb.append(", Value: ").append(invalidValue);
            }
            sb.append(")");
        }
        
        if (errorCode != null && !errorCode.equals("VALIDATION_ERROR")) {
            sb.append(" [Error Code: ").append(errorCode).append("]");
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getDetailedMessage();
    }
}