package com.example.userapi.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class SignInResponse {
    private String token;
    private List<String> roles;
}
