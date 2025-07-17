package com.example.userapi.unit.service;

import com.example.userapi.model.User;
import com.example.userapi.repository.IUserRepository;
import com.example.userapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        IUserRepository userRepository = Mockito.mock(IUserRepository.class);
        userService = new UserService(userRepository);
        List<User> mockUsers = List.of(new User(1L, "Bob", "bob@example.com"),
                new User(2L, "Alice", "alice@example.com"));
        Mockito.when(userRepository.findAll()).thenReturn(mockUsers);
        Mockito.when(userRepository.getUsers(1)).thenReturn(mockUsers.subList(0, 1));
        Mockito.when(userRepository.getUsers(2)).thenReturn(mockUsers);
    }

    @Test
    void getAllUsers_returnsUsers() {
        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void getUsers_returnsLimitedUsers() {
        List<User> users = userService.getUsers(1);
        assertEquals(1, users.size());
        assertEquals("Bob", users.getFirst().getUsername());

        users = userService.getUsers(2);
        assertEquals(2, users.size());
        assertEquals("Bob", users.get(0).getUsername());
        assertEquals("Alice", users.get(1).getUsername());
    }
}
