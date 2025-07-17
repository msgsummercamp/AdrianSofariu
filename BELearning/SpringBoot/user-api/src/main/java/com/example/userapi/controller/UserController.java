package com.example.userapi.controller;

import com.example.userapi.service.IUserService;
import com.example.userapi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        logger.info("Received GET request for /users");
        List<User> userList = userService.getAllUsers();
        logger.debug("Returning {} users", userList.size());
        return userList;
    }
}
