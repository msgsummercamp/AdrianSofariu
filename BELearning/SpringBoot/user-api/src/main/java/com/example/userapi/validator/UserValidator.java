package com.example.userapi.validator;

import com.example.userapi.exception.UserValidationException;
import com.example.userapi.model.User;

public class UserValidator {

    public static void validate(User user) throws UserValidationException {

        if (user == null) {
            throw new UserValidationException("User cannot be null");
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new UserValidationException("Username cannot be null or empty");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new UserValidationException("Email cannot be null or empty");
        }

    }
}
