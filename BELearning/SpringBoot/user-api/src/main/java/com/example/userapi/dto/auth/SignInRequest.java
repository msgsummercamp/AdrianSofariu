package com.example.userapi.dto.auth;

import lombok.Data;

@Data
public class SignInRequest {
    private String username;
    private String password;
}
