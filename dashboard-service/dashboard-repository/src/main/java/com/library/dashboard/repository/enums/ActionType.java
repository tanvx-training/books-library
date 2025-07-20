package com.library.dashboard.repository.enums;

/**
 * Enumeration representing the types of actions that can be performed on entities
 * for audit logging purposes.
 */
public enum ActionType {
    /**
     * Represents the creation of a new entity
     */
    CREATE,
    
    /**
     * Represents the update/modification of an existing entity
     */
    UPDATE,
    
    /**
     * Represents the deletion of an entity
     */
    DELETE
}