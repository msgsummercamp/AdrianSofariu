package com.example.userapi.service;

import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.repository.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.example.userapi.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Service - Fetching all users from the repository");
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        logger.info("Service - Fetching user with ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        logger.info("Service - Fetching user with username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        logger.info("Service - Fetching user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User addUser(User user) throws ClashingUserException {
        logger.info("Service - Attempting to save new user: {}", user.getUsername());
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // Centralized handling for unique and possibly non-null violations
            handleDataIntegrityViolation(e, user.getUsername(), user.getEmail());
            // The line below won't be reached if handleDataIntegrityViolation throws, but good as a fallback
            throw e;
        }
    }

    @Override
    @Transactional
    public User updateUser(User user) throws ClashingUserException, UserNotFoundException {
        // First, check if the user to update actually exists.
        User userToUpdate = userRepository.findById(user.getId())
                .orElseThrow(() -> {
                    logger.warn("User with ID: {} not found for update", user.getId());
                    return new UserNotFoundException("User with ID: " + user.getId() + " not found for update.");
                });

        // Copy relevant fields from the input 'user' to the 'userToUpdate' (fetched from DB)
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setPassword(user.getPassword());
        userToUpdate.setFirstname(user.getFirstname());
        userToUpdate.setLastname(user.getLastname());

        logger.info("Service - Attempting to update user with ID: {}", user.getId());
        try {
            return userRepository.save(userToUpdate);
        } catch (DataIntegrityViolationException e) {
            // Centralized handling for unique and possibly non-null violations
            handleDataIntegrityViolation(e, user.getUsername(), user.getEmail());
            throw e; // Fallback
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            logger.warn("User with ID: {} not found for deletion", id);
            throw new UserNotFoundException("User with ID: " + id + " not found for deletion.");
        }
        logger.info("Service - Deleting user with ID: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public long countUsers() {
        logger.info("Counting total number of users in the repository");
        return userRepository.countUsers();
    }

    /**
     * Handles DataIntegrityViolationException specifically, identifying unique and NOT NULL constraint violations.
     * Re-throws a more specific application-level exception.
     *
     * @param e The DataIntegrityViolationException thrown by Spring Data JPA.
     * @param username The username from the attempted operation (for custom error messages).
     * @param email The email from the attempted operation (for custom error messages).
     * @throws ClashingUserException if a unique constraint (username/email) is violated.
     * @throws IllegalArgumentException if a NOT NULL constraint is violated (for required fields).
     * @throws RuntimeException for any other unhandled DataIntegrityViolationException causes.
     */
    private void handleDataIntegrityViolation(DataIntegrityViolationException e, String username, String email) throws ClashingUserException {
        if (e.getCause() instanceof ConstraintViolationException cve) {
            String constraintName = cve.getConstraintName();
            String sqlState = cve.getSQLState();

            logger.error("Database constraint violation detected. Constraint: {}, SQLState: {}. Original error: {}",
                    constraintName, sqlState, cve.getSQLException().getMessage(), e);

            // --- Handle Unique Constraint Violations (SQLSTATE "23505" in PostgreSQL) ---
            if ("23505".equals(sqlState)) { // Standard SQLSTATE for unique violation
                if (constraintName != null) {
                    if (constraintName.contains("username") || constraintName.contains("users_username_key")) {
                        throw new ClashingUserException("Username '" + username + "' already exists.");
                    } else if (constraintName.contains("email") || constraintName.contains("users_email_key")) {
                        throw new ClashingUserException("Email '" + email + "' is already registered.");
                    }
                }
                // Fallback for unique constraint violations if name doesn't match known patterns
                throw new ClashingUserException("A unique field constraint was violated.", e);
            }

            // --- Handle NOT NULL Constraint Violations (SQLSTATE "23502" in PostgreSQL) ---
            else if ("23502".equals(sqlState)) { // Standard SQLSTATE for not-null violation
                // The ConstraintViolationException might not directly give the column name for NOT NULL.
                String originalErrorMessage = cve.getSQLException().getMessage();
                if (originalErrorMessage != null) {
                    if (originalErrorMessage.contains("username")) {
                        throw new IllegalArgumentException("Username cannot be null.");
                    } else if (originalErrorMessage.contains("email")) {
                        throw new IllegalArgumentException("Email cannot be null.");
                    } else if (originalErrorMessage.contains("password")) {
                        throw new IllegalArgumentException("Password cannot be null.");
                    }
                }
                // Generic fallback for NOT NULL violations
                throw new IllegalArgumentException("A required field is missing and cannot be null.", e);
            }

            // Fallback for any other ConstraintViolationException types not specifically handled
            throw new RuntimeException("A database constraint was violated: " + cve.getSQLException().getMessage(), e);

        } else {
            // If the cause is not a ConstraintViolationException (e.g., connection error, syntax error),
            // re-throw or wrap in a generic RuntimeException.
            throw new RuntimeException("An unexpected data integrity issue occurred: " + e.getMessage(), e);
        }
    }
}
