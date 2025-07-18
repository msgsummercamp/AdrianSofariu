package com.example.userapi.validators;

import com.example.userapi.dto.PatchUserDTO;
import com.example.userapi.validators.EmailValidator;

public class UserPatchValidator {

    public static void validatePatch(PatchUserDTO patchUserDTO) {
        if (patchUserDTO.getUsername() != null && patchUserDTO.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (patchUserDTO.getPassword() != null && patchUserDTO.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        // Repeat for other fields as needed
    }
}
