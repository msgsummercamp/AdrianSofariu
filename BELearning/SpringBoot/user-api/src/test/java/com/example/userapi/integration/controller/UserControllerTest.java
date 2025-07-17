package com.example.userapi.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUsers_returnsUserList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$[0].username").value("Alice"))
                .andExpect(jsonPath("$[1].username").value("Bob"));
    }

    @Test
    void getUsers_withValidCount_returnsLimitedUsers() throws Exception {
        mockMvc.perform(get("/users").param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("Alice"));
    }

    @Test
    void getUsers_withInvalidCount_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/users").param("count", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Count must be a positive integer"));

        mockMvc.perform(get("/users").param("count", "-5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Count must be a positive integer"));
    }
}