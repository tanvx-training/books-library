package com.library.user.repository.custom;

import com.library.user.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Custom repository interface for User with advanced operations that need logging
 */
public interface UserRepositoryCustom {
    
    /**
     * Find user by email with role information
     * @param email the email address
     * @return user with roles loaded
     */
    Optional<User> findByEmailWithRoles(String email);
    
    /**
     * Find user by username with role information
     * @param username the username
     * @return user with roles loaded
     */
    Optional<User> findByUsernameWithRoles(String username);
    
    /**
     * Check if email is available for registration
     * @param email the email to check
     * @return true if available, false if taken
     */
    boolean isEmailAvailable(String email);
    
    /**
     * Check if username is available for registration
     * @param username the username to check
     * @return true if available, false if taken
     */
    boolean isUsernameAvailable(String username);
    
    /**
     * Find users by role name for admin operations
     * @param roleName the role name
     * @return list of users with that role
     */
    List<User> findUsersByRoleName(String roleName);
} 