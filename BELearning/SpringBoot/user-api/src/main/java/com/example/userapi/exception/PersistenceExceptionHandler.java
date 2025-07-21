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
    public static void handleConstraintViolationExceptions(DataIntegrityViolationException dataIntegrityViolationException) throws ClashingUserException, IllegalArgumentException, ConstraintViolationException, DataIntegrityViolationException {
        if (dataIntegrityViolationException.getCause() instanceof ConstraintViolationException constraintViolationException) {
            String constraintName = constraintViolationException.getConstraintName();
            String sqlState = constraintViolationException.getSQLState();
            SQLException sqlException = constraintViolationException.getSQLException();

            log.error("Database constraint violation detected. Constraint: {}, SQLState: {}. Original error: {}",
                    constraintName, sqlState, sqlException.getMessage(), dataIntegrityViolationException);

            if (sqlState.equals(UNIQUE_VIOLATION_SQL_STATE)) {

                String originalErrorMessage = sqlException.getMessage();
                String finalMessage = "A unique constraint was violated. " + originalErrorMessage;
                throw new ClashingUserException(finalMessage, constraintViolationException);
            }
            else if (sqlState.equals(NOT_NULL_VIOLATION_SQL_STATE)) {
                String originalErrorMessage = sqlException.getMessage();
                String finalMessage = "A required field is missing and cannot be null. " + originalErrorMessage;
                throw new IllegalArgumentException(finalMessage, constraintViolationException);
            }

            // If the SQL state is not recognized, re-throw the original exception
            throw constraintViolationException;

        } else {
            // If the cause is not a ConstraintViolationException, re-throw
            throw dataIntegrityViolationException;
        }
    }
}
