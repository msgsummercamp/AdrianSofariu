package com.example.userapi.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private Set<String> roles;
}
