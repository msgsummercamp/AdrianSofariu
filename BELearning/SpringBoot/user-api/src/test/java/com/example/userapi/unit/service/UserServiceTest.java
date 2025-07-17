package com.example.userapi.unit.service;

import com.example.userapi.model.User;
import com.example.userapi.repository.IUserRepository;
import com.example.userapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {

    @Test
    void getAllUsers_returnsUsers() {
        IUserRepository repo = Mockito.mock(IUserRepository.class);
        // UserServiceTest.java
        List<User> mockUsers = List.of(
                new User(1L, "Bob", "bob@example.com", "password1", "BobFirst", "BobLast")
        );
        Mockito.when(repo.findAll()).thenReturn(mockUsers);

        UserService userService = new UserService(repo);
        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("Bob", users.getFirst().getUsername());
    }

    @Test
    void getUsers_returnsLimitedUsers() {
        IUserRepository repo = Mockito.mock(IUserRepository.class);
        // UserServiceTest.java
        List<User> mockUsers = List.of(
                new User(1L, "Bob", "bob@example.com", "password1", "BobFirst", "BobLast"),
                new User(2L, "Alice", "alice@example.com", "password2", "AliceFirst", "AliceLast")
        );
        Mockito.when(repo.getUsers(1)).thenReturn(mockUsers.subList(0, 1));
        Mockito.when(repo.getUsers(2)).thenReturn(mockUsers);

        UserService userService = new UserService(repo);
        List<User> users = userService.getUsers(1);
        assertEquals(1, users.size());
        assertEquals("Bob", users.getFirst().getUsername());

        users = userService.getUsers(2);
        assertEquals(2, users.size());
        assertEquals("Bob", users.get(0).getUsername());
        assertEquals("Alice", users.get(1).getUsername());
    }
}
