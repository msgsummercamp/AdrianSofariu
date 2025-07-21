package com.example.userapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;

@Slf4j
public class PersistenceExceptionHandler {

    private static final String NOT_NULL_VIOLATION_SQL_STATE = "23502";
    private static final String UNIQUE_VIOLATION_SQL_STATE = "23505";

    /**
     * Function that handles DataIntegrityViolationException and its cause ConstraintViolationException.
     * It checks the SQL state of the ConstraintViolationException to determine if it is a unique constraint violation or a not-null constraint violation.
     *
     * @param dataIntegrityViolationException the original DataIntegrityViolationException that was thrown
     * @throws ClashingUserException if a unique constraint violation is detected
     * @throws IllegalArgumentException if a not-null constraint violation is detected
     * @throws ConstraintViolationException if the SQL state is not recognized
     * @throws DataIntegrityViolationException if the cause is not a ConstraintViolationException
     */
    public static void handleConstraintViolationExceptions(DataIntegrityViolationException dataIntegrityViolationException) throws ClashingUserException {

        if (dataIntegrityViolationException.getCause() instanceof ConstraintViolationException constraintViolationException) {

            String constraintName = constraintViolationException.getConstraintName();
            String sqlState = constraintViolationException.getSQLState();
            SQLException sqlException = constraintViolationException.getSQLException();

            log.error("Database constraint violation detected. Constraint: {}, SQLState: {}. Original error: {}",
                    constraintName, sqlState, sqlException.getMessage(), dataIntegrityViolationException);

            if (sqlState.equals(UNIQUE_VIOLATION_SQL_STATE)) {

                handleUniqueConstraintViolation(sqlException);
            }
            else if (sqlState.equals(NOT_NULL_VIOLATION_SQL_STATE)) {

                handleNotNullConstraintViolation(sqlException);
            }

            throw constraintViolationException;

        } else {
            throw dataIntegrityViolationException;
        }
    }

    /**
     * Handles unique constraint violations by throwing a ClashingUserException.
     *
     * @param e the SQLException that caused the unique constraint violation
     * @throws ClashingUserException with a message indicating the violation
     */
    private static void handleUniqueConstraintViolation(SQLException e) throws ClashingUserException {
        String message = "A unique constraint was violated: " + e.getMessage();
        throw new ClashingUserException(message, e);
    }

    /**
     * Handles not-null constraint violations by throwing an IllegalArgumentException.
     *
     * @param e the SQLException that caused the not-null constraint violation
     * @throws IllegalArgumentException with a message indicating the violation
     */
    private static void handleNotNullConstraintViolation(SQLException e) throws IllegalArgumentException {
        String message = "A required field cannot be null: " + e.getMessage();
        throw new IllegalArgumentException(message, e);
    }
}
