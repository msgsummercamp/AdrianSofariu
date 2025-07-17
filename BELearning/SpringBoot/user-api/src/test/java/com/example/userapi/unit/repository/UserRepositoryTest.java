package com.example.userapi.unit.repository;

import org.junit.jupiter.api.Test;
import com.example.userapi.repository.UserRepository;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class UserRepositoryTest {

    @Test
    void findAll_returnsInitialUsers() {
        UserRepository userRepository = new UserRepository();
        assertFalse(userRepository.findAll().isEmpty());
    }

    @Test
    void getUsers_returnsLimitedUsers() {
        UserRepository userRepository = new UserRepository();
        int count = 5;
        assertFalse(userRepository.getUsers(count).isEmpty());
        assertFalse(userRepository.getUsers(count).size() > count);
    }
}
