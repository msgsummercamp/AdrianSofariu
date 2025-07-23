package com.example.userapi.controller;

import com.example.userapi.dto.UserDTO;
import com.example.userapi.dto.auth.SignInRequest;
import com.example.userapi.dto.auth.SignInResponse;
import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest signInRequest) throws UserNotFoundException {
        return ResponseEntity.ok(authService.signIn(signInRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<SignInResponse> register(@Valid @RequestBody UserDTO userToRegister) throws ClashingUserException {
        return ResponseEntity.ok(authService.register(userToRegister));
    }
}
