package com.example.userapi.unit.repository;

import com.example.userapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.userapi.repository.UserRepository;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;



public class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
    }

    @Test
    void findAll_returnsInitialUsers() {
        List<User> initialUsers = userRepository.findAll();
        assertThat(initialUsers, is(not(empty())));
    }

    @Test
    void getUsers_returnsLimitedUsers() {
        int count = 5;
        List<User> limitedUsers = userRepository.getUsers(count);
        assertThat(limitedUsers, is(not(empty())));
        assertThat(limitedUsers.size(), lessThanOrEqualTo(count));
    }
}
