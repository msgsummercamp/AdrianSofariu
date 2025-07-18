package com.example.userapi.controller;

import com.example.userapi.service.IUserService;
import com.example.userapi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * Handles GET requests to fetch users.
     * If a count parameter is provided, it returns that many users.
     * Otherwise, it returns all users.
     *
     * @param count the maximum number of users to return (optional)
     * @return a list of users
     */
    @GetMapping
    public List<User> getUsers(@RequestParam(required = false) Integer count) {
        logger.info("Received GET request for /users with count={}", count);
        if (count != null) {
            if (count <= 0) {
                logger.warn("Count parameter is non-positive: {}", count);
                throw new IllegalArgumentException("Count must be a positive integer");
            }
            return userService.getUsers(count);
        }
        List<User> userList = userService.getAllUsers();
        logger.debug("Returning {} users", userList.size());
        return userList;
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
