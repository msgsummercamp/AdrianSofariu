package com.example.userapi.service;

import com.example.userapi.dto.UserDTO;
import com.example.userapi.dto.auth.SignInRequest;
import com.example.userapi.dto.auth.SignInResponse;
import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.UserNotFoundException;

public interface AuthService {

    /**
     * Signs in a user with the provided credentials.
     *
     * @param signInRequest the sign-in request containing username and password
     * @return SignInResponse containing the JWT token and user roles
     * @throws UserNotFoundException if the user is not found
     */
    SignInResponse signIn(SignInRequest signInRequest) throws UserNotFoundException;

    /**
     * Registers a new user with the provided user details.
     *
     * @param userDTO the user details for registration
     * @return SignInResponse containing the JWT token and user roles
     * @throws ClashingUserException if a user with the same username or email already exists
     */
    SignInResponse register(UserDTO userDTO) throws ClashingUserException;

    /**
     * Validates the provided JWT token.
     *
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Extracts the username from the provided JWT token.
     *
     * @param token the JWT token from which to extract the username
     * @return the username extracted from the token
     */
    String getUsernameFromToken(String token);
}
