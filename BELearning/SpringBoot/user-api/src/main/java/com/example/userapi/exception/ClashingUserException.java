package com.example.userapi.exception;

public class ClashingUserException extends Exception {
    public ClashingUserException(String message) {
        super(message);
    }
    public ClashingUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
