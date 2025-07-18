package com.example.userapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Optional;

@Data
public class PatchUserDTO {
    private String username;
    @Email(message = "Email should be valid")
    private String email;
    private String password;
    private String firstname;
    private String lastname;
}