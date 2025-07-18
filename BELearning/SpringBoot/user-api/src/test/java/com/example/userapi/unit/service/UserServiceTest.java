package com.example.userapi.unit.service;

import com.example.userapi.dto.PatchUserDTO;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void getUsers_ReturnsPaginatedUsers() {
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        Pageable pageable = Pageable.unpaged();
        Page<User> userPage = new org.springframework.data.domain.PageImpl<>(List.of(user1, user2), pageable, 2);

        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<User> result = userService.getUsers(pageable);

        assertThat(result.getContent(), hasSize(2));
        assertThat(result.getContent(), contains(user1, user2));
        assertThat(result.getTotalElements(), is(2L));
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        User user = new User();
        user.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(user));
    }

    @Test
    void getUserById_UserDoesNotExist_ReturnsEmptyOptional() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(2L);

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void getUserByUsername_UserExists_ReturnsUser() {
        User user = new User();
        user.setUsername("testuser");
        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByUsername("testuser");

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(user));
    }

    @Test
    void getUserByEmail_UserExists_ReturnsUser() {
        User user = new User();
        user.setEmail("test@example.com");
        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail("test@example.com");

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(user));
    }

    @Test
    void countUsers_ReturnsCorrectCount() {
        Mockito.when(userRepository.countUsers()).thenReturn(5L);

        long count = userService.countUsers();

        assertThat(count, is(5L));
    }

    @Test
    void addUser_ValidUser_ReturnsSavedUser() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setPassword("password");
        Mockito.when(userRepository.save(user)).thenReturn(user);

        User saved = userService.addUser(user);

        assertThat(saved, is(user));
    }


    @Test
    void updateUser_UserExists_ValidUpdate_ReturnsUpdatedUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("updateduser");
        user.setEmail("updated@example.com");
        user.setPassword("newpass");
        User dbUser = new User();
        dbUser.setId(1L);
        dbUser.setUsername("olduser");
        dbUser.setEmail("old@example.com");
        dbUser.setPassword("oldpass");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(dbUser));
        Mockito.when(userRepository.save(dbUser)).thenReturn(user);

        User updated = userService.updateUser(user);

        assertThat(updated.getUsername(), is("updateduser"));
        assertThat(updated.getEmail(), is("updated@example.com"));
        assertThat(updated.getPassword(), is("newpass"));
    }

    @Test
    void updateUser_UserDoesNotExist_ThrowsUserNotFoundException() {
        User user = new User();
        user.setId(99L);
        user.setUsername("nouser");
        user.setEmail("nouser@example.com");
        user.setPassword("pass");

        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(user)
        );
        assertThat(thrown.getMessage(), containsString("not found"));
    }

    @Test
    void patchUser_UserExists_UpdatesProvidedFields() throws Exception {
        User dbUser = new User();
        dbUser.setId(1L);
        dbUser.setUsername("olduser");
        dbUser.setEmail("old@example.com");
        dbUser.setPassword("oldpass");
        dbUser.setFirstname("Old");
        dbUser.setLastname("User");

        PatchUserDTO patch = new PatchUserDTO();
        patch.setUsername("newuser");
        patch.setEmail(null); // not provided
        patch.setPassword("newpass");
        patch.setFirstname(null); // not provided
        patch.setLastname("Newlastname");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(dbUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.patchUser(1L, patch);

        assertThat(result.getUsername(), is("newuser"));
        assertThat(result.getEmail(), is("old@example.com")); // unchanged
        assertThat(result.getPassword(), is("newpass"));
        assertThat(result.getFirstname(), is("Old")); // unchanged
        assertThat(result.getLastname(), is("Newlastname"));
    }

    @Test
    void patchUser_UserDoesNotExist_ThrowsUserNotFoundException() {
        PatchUserDTO patch = new PatchUserDTO();
        patch.setUsername("newuser");

        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
                UserNotFoundException.class,
                () -> userService.patchUser(99L, patch)
        );
        assertThat(thrown.getMessage(), containsString("not found"));
    }

    @Test
    void patchUser_OnlyUsernameProvided_UpdatesOnlyUsername() throws Exception {
        User dbUser = new User();
        dbUser.setId(2L);
        dbUser.setUsername("olduser");
        dbUser.setEmail("old@example.com");
        dbUser.setPassword("oldpass");
        dbUser.setFirstname("Old");
        dbUser.setLastname("User");

        PatchUserDTO patch = new PatchUserDTO();
        patch.setUsername("patcheduser");

        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(dbUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.patchUser(2L, patch);

        assertThat(result.getUsername(), is("patcheduser"));
        assertThat(result.getEmail(), is("old@example.com"));
        assertThat(result.getPassword(), is("oldpass"));
        assertThat(result.getFirstname(), is("Old"));
        assertThat(result.getLastname(), is("User"));
    }

    @Test
    void deleteUser_UserExists_DeletesUser() throws Exception {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        Mockito.verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_UserDoesNotExist_ThrowsUserNotFoundException() {
        Mockito.when(userRepository.existsById(2L)).thenReturn(false);

        Exception thrown = assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(2L)
        );
        assertThat(thrown.getMessage(), containsString("not found"));
    }
}
