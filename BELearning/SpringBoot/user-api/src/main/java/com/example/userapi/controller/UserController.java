package com.example.userapi.controller;

import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.exception.UserValidationException;
import com.example.userapi.service.IUserService;
import com.example.userapi.model.User;
import com.example.userapi.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers(){
        logger.info("Controller - Received GET request for /users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        logger.info("Controller - Received GET request for /users/{}", id);
        try {
            Optional<User> user = userService.getUserById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        logger.info("Controller - Received GET request for /users/username/{}", username);
        try {
            Optional<User> user = userService.getUserByUsername(username);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        logger.info("Controller - Received GET request for /users/email/{}", email);
        try {
            Optional<User> user = userService.getUserByEmail(email);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        logger.info("Controller - Received POST request for /users with user: {}", user);
        try {
            UserValidator.validate(user);
            if (user.getId() != null) {
                return ResponseEntity.badRequest().body("New user should not have an ID");
            }
            User savedUser = userService.addUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (ClashingUserException | UserValidationException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        logger.info("Controller - Received PUT request for /users/{} with user: {}", id, user);
        try {
            UserValidator.validate(user);
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (ClashingUserException | UserValidationException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Controller - Received DELETE request for /users/{}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @ExceptionHandler(ClashingUserException.class)
    public ResponseEntity<String> handleClashingUser(ClashingUserException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(500).body("Internal server error:" + ex.getMessage());
    }
}
