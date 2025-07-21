package com.example.userapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
public class PatchUserDTO {
    @Size(min = 1, max = 50)
    private String username;
    @Email(message = "Email should be valid")
    private String email;
    @Size(min = 1, max = 50)
    private String password;
    private String firstname;
    private String lastname;
    private Set<String> roles;
}