package com.example.userapi.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ClashingUserException.class)
    public ResponseEntity<String> handleClashingUser(ClashingUserException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Invalid input");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
    }

    /**
     * Handles DataIntegrityViolationException, specifically for unique constraint violations.
     * @param e the DataIntegrityViolationException thrown by the service layer
     * @return ResponseEntity with appropriate status and message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        Throwable cause = e.getCause();
        if (cause instanceof ConstraintViolationException cve) {
            String constraintName = cve.getConstraintName();
            String sqlState = cve.getSQLState();
            String originalErrorMessage = cve.getSQLException().getMessage();

            // Unique constraint violation
            if ("23505".equals(sqlState)) {
                if (constraintName != null) {
                    if (constraintName.contains("username") || constraintName.contains("users_username_key")) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
                    } else if (constraintName.contains("email") || constraintName.contains("users_email_key")) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already registered.");
                    }
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body("A unique field constraint was violated.");
            }

            // Other constraint violations
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("A database constraint was violated: " + originalErrorMessage);
        } else if (cause instanceof org.hibernate.PropertyValueException pve) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(pve.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected data integrity issue occurred: " + e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
    }
}