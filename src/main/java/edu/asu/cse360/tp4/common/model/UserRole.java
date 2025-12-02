package edu.asu.cse360.tp4.common.model;

/**
 * Enumeration of user roles in the TP4 Multi-Role Educational Platform.
 * 
 * Defines the hierarchy and permissions structure for the three primary
 * user types supported by the system with role-based access control.
 * 
 * @author TP4 Team
 * @version 2.0.0
 * @since TP4 Implementation Phase
 */
public enum UserRole {
    
    /**
     * Student role with access to personal dashboard, progress tracking,
     * and help-seeking functionality.
     */
    STUDENT(1, "Student", "Basic user with access to personal academic data"),
    
    /**
     * Instructor role with access to class management, grading workflows,
     * and student analytics.
     */
    INSTRUCTOR(2, "Instructor", "Educator with class management and grading capabilities"),
    
    /**
     * Staff role with administrative access to system management,
     * user oversight, and comprehensive reporting.
     */
    STAFF(3, "Staff", "Administrative user with system-wide access and management"),
    
    /**
     * Administrator role with full system access and configuration capabilities.
     */
    ADMINISTRATOR(4, "Administrator", "Super user with complete system access");
    
    private final int level;
    private final String displayName;
    private final String description;
    
    UserRole(int level, String displayName, String description) {
        this.level = level;
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Gets the permission level of the role (higher number = more permissions).
     * 
     * @return the numeric permission level
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Gets the human-readable display name of the role.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of the role's capabilities.
     * 
     * @return the role description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if this role has permission level equal to or higher than the specified role.
     * 
     * @param requiredRole the role to check against
     * @return true if this role has sufficient permissions
     */
    public boolean hasPermissionFor(UserRole requiredRole) {
        return this.level >= requiredRole.level;
    }
    
    /**
     * Checks if this role can access resources belonging to the target role.
     * 
     * @param targetRole the role whose resources are being accessed
     * @return true if access is permitted
     */
    public boolean canAccess(UserRole targetRole) {
        // Users can access their own resources or lower-level resources
        return this.level >= targetRole.level;
    }
}