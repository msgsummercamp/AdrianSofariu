package com.example.userapi.unit.service;

import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    @Test
    void getAllUsers_ReturnsListOfUsers() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        List<User> users = List.of(new User(), new User());
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        User user = new User();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void getUserById_UserDoesNotExist_ReturnsEmptyOptional() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(1L);

        assertEquals(Optional.empty(), result);
    }

    @Test
    void getUserByUsername_UserExists_ReturnsUser() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        User user = new User();
        Mockito.when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByUsername("johndoe");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void getUserByUsername_UserDoesNotExist_ReturnsEmptyOptional() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        Mockito.when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByUsername("johndoe");

        assertEquals(Optional.empty(), result);
    }

    @Test
    void getUserByEmail_UserExists_ReturnsUser() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        User user = new User();
        Mockito.when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail("john.doe@example.com");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void getUserByEmail_UserDoesNotExist_ReturnsEmptyOptional() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        Mockito.when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail("john.doe@example.com");

        assertEquals(Optional.empty(), result);
    }

    @Test
    void addUser_ClashingUser_ThrowsClashingUserException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        User user = new User();
        user.setUsername("existing");
        user.setEmail("existing@example.com");
        User existingUser = new User();
        existingUser.setId(2L);
        Mockito.when(userRepository.findFirstByUsernameOrEmailAndIdNot("existing", "existing@example.com", -1L))
                .thenReturn(Optional.of(existingUser));

        org.junit.jupiter.api.Assertions.assertThrows(
                com.example.userapi.exception.ClashingUserException.class,
                () -> userService.addUser(user)
        );
    }

    @Test
    void addUser_NoClashingUser_SavesUser() throws Exception {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        User user = new User();
        user.setUsername("unique");
        user.setEmail("unique@example.com");
        Mockito.when(userRepository.findFirstByUsernameOrEmailAndIdNot("unique", "unique@example.com", -1L))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.save(user)).thenReturn(user);

        User result = userService.addUser(user);

        assertEquals(user, result);
    }

    @Test
    void updateUser_ClashingUser_ThrowsClashingUserException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        User user = new User();
        user.setId(1L);
        user.setUsername("clash");
        user.setEmail("clash@example.com");
        User clashingUser = new User();
        clashingUser.setId(2L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findFirstByUsernameOrEmailAndIdNot("clash", "clash@example.com", 1L))
                .thenReturn(Optional.of(clashingUser));

        org.junit.jupiter.api.Assertions.assertThrows(
                com.example.userapi.exception.ClashingUserException.class,
                () -> userService.updateUser(user)
        );
    }

    @Test
    void updateUser_UserExistsAndNoClash_UpdatesUser() throws Exception {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        User user = new User();
        user.setId(1L);
        user.setUsername("updated");
        user.setEmail("updated@example.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findFirstByUsernameOrEmailAndIdNot("updated", "updated@example.com", 1L))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(user);

        assertEquals(user, result);
    }

    @Test
    void updateUser_UserDoesNotExist_ThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        User user = new User();
        user.setId(99L);
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                com.example.userapi.exception.UserNotFoundException.class,
                () -> userService.updateUser(user)
        );
    }

    @Test
    void deleteUser_UserExists_DeletesUser() throws Exception {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        User user = new User();
        user.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        Mockito.verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_UserDoesNotExist_ThrowsUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                com.example.userapi.exception.UserNotFoundException.class,
                () -> userService.deleteUser(1L)
        );
    }

    @Test
    void countUsers_ReturnsCorrectCount() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        Mockito.when(userRepository.countUsers()).thenReturn(5L);
        long result = userService.countUsers();
        assertEquals(5L, result);
    }

}
