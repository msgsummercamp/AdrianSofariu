package com.example.userapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@Validated
public class AuthPageController {

    @GetMapping("/login")
    public String login() {
        log.info("Accessing login page");
        return "login"; // returns login.html from templates
    }
}