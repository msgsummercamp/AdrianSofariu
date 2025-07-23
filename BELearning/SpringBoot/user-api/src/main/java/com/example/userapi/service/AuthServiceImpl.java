package com.example.userapi.service;

import com.example.userapi.dto.UserDTO;
import com.example.userapi.dto.auth.SignInRequest;
import com.example.userapi.dto.auth.SignInResponse;
import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.InvalidCredentialsException;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.model.Role;
import com.example.userapi.model.User;
import com.example.userapi.repository.RoleRepository;
import com.example.userapi.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Key jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long JWT_EXPIRATION_MS = 3600000; // 1 hour in milliseconds

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public SignInResponse signIn(SignInRequest signInRequest) throws UserNotFoundException {
        log.info("Signing in user with username: {}", signInRequest.getUsername());
        Optional<User> ouser = userRepository.findByUsername(signInRequest.getUsername());
        if (ouser.isEmpty()) {
            throw new UserNotFoundException("User not found with username: " + signInRequest.getUsername());
        }
        User user = ouser.get();
        if(!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password for user: " + signInRequest.getUsername());
        }
        List<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String token = generateToken(user);
        return new SignInResponse(token, roleNames);
    }

    @Override
    public SignInResponse register(UserDTO userDTO) throws ClashingUserException {
        log.info("Registering user with username: {}", userDTO.getUsername());
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ClashingUserException("Username already exists: " + userDTO.getUsername());
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new InvalidCredentialsException("Email already exists: " + userDTO.getEmail());
        }

        Set<Role> userRoles = userDTO.getRoles().stream()
                .map(roleName -> roleRepository.findById(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role Not Found: " + roleName)))
                .collect(Collectors.toSet());
        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .roles(userRoles) // Assuming role is set correctly in UserDTO
                .build();

        User savedUser = userRepository.save(user);
        List<String> roleNames = savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String token = generateToken(savedUser);
        return new SignInResponse(token, roleNames);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("JwtException --" + e.getMessage());
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Generates a JWT token for the user.
     *
     * @param user the user for whom the token is generated
     * @return the generated JWT token
     */
    private String generateToken(User user) {
        List<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", roleNames)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(jwtSecret)
                .compact();
    }
}
