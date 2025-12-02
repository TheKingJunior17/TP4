package edu.asu.cse360.tp4.common.exception;

/**
 * Custom exception for staff management specific errors in the TP4 system.
 * 
 * This exception is thrown when staff-specific operations fail, such as
 * unauthorized access attempts, invalid staff operations, or business
 * rule violations specific to staff functionality.
 * 
 * @author TP4 Team - Exception Handling Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
public class StaffManagementException extends RuntimeException {
    
    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;
    
    /** The staff operation that failed */
    private final String operation;
    
    /** The staff ID involved in the operation */
    private final String staffId;
    
    /** The target entity ID (student, question, etc.) */
    private final String targetId;
    
    /** Error code for programmatic handling */
    private final String errorCode;
    
    /**
     * Constructs a new StaffManagementException with the specified detail message.
     * 
     * @param message the detail message explaining the error
     */
    public StaffManagementException(String message) {
        super(message);
        this.operation = null;
        this.staffId = null;
        this.targetId = null;
        this.errorCode = "STAFF_ERROR";
    }
    
    /**
     * Constructs a new StaffManagementException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the error
     * @param cause the cause of the error
     */
    public StaffManagementException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.staffId = null;
        this.targetId = null;
        this.errorCode = "STAFF_ERROR";
    }
    
    /**
     * Constructs a new StaffManagementException with detailed operation information.
     * 
     * @param message the detail message explaining the error
     * @param operation the staff operation that failed
     * @param staffId the ID of the staff member
     * @param targetId the ID of the target entity
     */
    public StaffManagementException(String message, String operation, String staffId, String targetId) {
        super(message);
        this.operation = operation;
        this.staffId = staffId;
        this.targetId = targetId;
        this.errorCode = "STAFF_OPERATION_ERROR";
    }
    
    /**
     * Constructs a new StaffManagementException with full details and error code.
     * 
     * @param message the detail message explaining the error
     * @param operation the staff operation that failed
     * @param staffId the ID of the staff member
     * @param targetId the ID of the target entity
     * @param errorCode specific error code for programmatic handling
     */
    public StaffManagementException(String message, String operation, String staffId, String targetId, String errorCode) {
        super(message);
        this.operation = operation;
        this.staffId = staffId;
        this.targetId = targetId;
        this.errorCode = errorCode != null ? errorCode : "STAFF_ERROR";
    }
    
    /**
     * Gets the operation that failed.
     * 
     * @return the operation name, or null if not specified
     */
    public String getOperation() {
        return operation;
    }
    
    /**
     * Gets the staff ID involved in the operation.
     * 
     * @return the staff ID, or null if not specified
     */
    public String getStaffId() {
        return staffId;
    }
    
    /**
     * Gets the target entity ID.
     * 
     * @return the target ID, or null if not specified
     */
    public String getTargetId() {
        return targetId;
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
     * Gets a detailed error message including operation and context information.
     * 
     * @return formatted error message with additional details
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder(getMessage());
        
        if (operation != null) {
            sb.append(" (Operation: ").append(operation);
            
            if (staffId != null) {
                sb.append(", Staff: ").append(staffId);
            }
            
            if (targetId != null) {
                sb.append(", Target: ").append(targetId);
            }
            
            sb.append(")");
        }
        
        if (errorCode != null && !errorCode.equals("STAFF_ERROR")) {
            sb.append(" [Error Code: ").append(errorCode).append("]");
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getDetailedMessage();
    }
}