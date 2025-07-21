package com.example.userapi.unit.service;

import com.example.userapi.dto.PatchUserDTO;
import com.example.userapi.dto.UserDTO;
import com.example.userapi.dto.UpdateUserDTO;
import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.model.User;
import com.example.userapi.model.Role;
import com.example.userapi.repository.RoleRepository;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.service.UserServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceImplementationTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImplementation userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserServiceImplementation(userRepository, roleRepository, passwordEncoder);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");
    }

    @Test
    void getUsers_ReturnsPaginatedUsers() {
        User user1 = User.builder().username("user1").roles(Set.of()).build();
        User user2 = User.builder().username("user2").roles(Set.of()).build();

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
        User user = User.builder().id(1L).roles(Set.of()).build();
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
        User user = User.builder().username("testuser").roles(Set.of()).build();
        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByUsername("testuser");

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(user));
    }

    @Test
    void getUserByEmail_UserExists_ReturnsUser() {
        User user = User.builder().email("test@example.com").roles(Set.of()).build();
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
    void addUser_ValidUser_ReturnsSavedUser() throws ClashingUserException {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        userDTO.setEmail("new@example.com");
        userDTO.setPassword("password");
        userDTO.setRoles(Set.of("USER"));

        Role role = new Role();
        role.setName("USER");

        User user = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("encodedPassword")
                .roles(Set.of(role))
                .build();

        Mockito.when(roleRepository.findById("USER")).thenReturn(Optional.of(role));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User saved = userService.addUser(userDTO);

        assertThat(saved.getUsername(), is("newuser"));
        assertThat(saved.getEmail(), is("new@example.com"));
        assertThat(saved.getPassword(), is("encodedPassword"));
        assertThat(saved.getRoles(), contains(role));
    }

    @Test
    void addUser_RoleNotFound_ThrowsException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        userDTO.setEmail("new@example.com");
        userDTO.setPassword("password");
        userDTO.setRoles(Set.of("NOT_EXIST"));

        Mockito.when(roleRepository.findById("NOT_EXIST")).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addUser(userDTO)
        );
        assertThat(thrown.getMessage(), containsString("Role Not Found"));
    }

    @Test
    void updateUser_UserExists_ValidUpdate_ReturnsUpdatedUser() throws ClashingUserException, UserNotFoundException {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("updateduser");
        updateUserDTO.setEmail("updated@example.com");
        updateUserDTO.setPassword("newpass");
        updateUserDTO.setRoles(Set.of("USER"));

        Role role = new Role();
        role.setName("USER");

        User dbUser = User.builder()
                .id(1L)
                .username("olduser")
                .email("old@example.com")
                .password("oldpass")
                .roles(Set.of(role))
                .build();

        Mockito.when(roleRepository.findById("USER")).thenReturn(Optional.of(role));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(dbUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updated = userService.updateUser(updateUserDTO, 1L);

        assertThat(updated.getUsername(), is("updateduser"));
        assertThat(updated.getEmail(), is("updated@example.com"));
        assertThat(updated.getPassword(), is("encodedPassword"));
        assertThat(updated.getRoles(), contains(role));
    }

    @Test
    void updateUser_UserDoesNotExist_ThrowsUserNotFoundException() {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("nouser");
        updateUserDTO.setEmail("nouser@example.com");
        updateUserDTO.setPassword("pass");
        updateUserDTO.setRoles(Set.of("USER"));

        Role role = new Role();
        role.setName("USER");

        Mockito.when(roleRepository.findById("USER")).thenReturn(Optional.of(role));
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(updateUserDTO, 99L)
        );
        assertThat(thrown.getMessage(), containsString("not found"));
    }

    @Test
    void updateUser_RoleNotFound_ThrowsException() {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("user");
        updateUserDTO.setEmail("user@example.com");
        updateUserDTO.setPassword("pass");
        updateUserDTO.setRoles(Set.of("NOT_EXIST"));

        Mockito.when(roleRepository.findById("NOT_EXIST")).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(updateUserDTO, 1L)
        );
        assertThat(thrown.getMessage(), containsString("Role Not Found"));
    }

    @Test
    void patchUser_UserExists_UpdatesProvidedFields() throws ClashingUserException, UserNotFoundException {
        Role oldRole = new Role();
        oldRole.setName("USER");
        User dbUser = User.builder()
                .id(1L)
                .username("olduser")
                .email("old@example.com")
                .password("oldpass")
                .firstname("Old")
                .lastname("User")
                .roles(Set.of(oldRole))
                .build();

        PatchUserDTO patch = new PatchUserDTO();
        patch.setUsername("newuser");
        patch.setEmail("new@example.com");
        patch.setPassword("newpass");
        patch.setFirstname("NewFirst");
        patch.setLastname("NewLast");
        patch.setRoles(Set.of("ADMIN"));

        Role newRole = new Role();
        newRole.setName("ADMIN");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(dbUser));
        Mockito.when(roleRepository.findById("ADMIN")).thenReturn(Optional.of(newRole));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.patchUser(1L, patch);

        assertThat(result.getUsername(), is("newuser"));
        assertThat(result.getEmail(), is("new@example.com"));
        assertThat(result.getPassword(), is("encodedPassword"));
        assertThat(result.getFirstname(), is("NewFirst"));
        assertThat(result.getLastname(), is("NewLast"));
        assertThat(result.getRoles(), contains(newRole));
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
    void patchUser_RoleNotFound_ThrowsException() {
        Role oldRole = new Role();
        oldRole.setName("USER");
        User dbUser = User.builder()
                .id(1L)
                .username("olduser")
                .email("old@example.com")
                .password("oldpass")
                .roles(Set.of(oldRole))
                .build();

        PatchUserDTO patch = new PatchUserDTO();
        patch.setRoles(Set.of("NOT_EXIST"));

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(dbUser));
        Mockito.when(roleRepository.findById("NOT_EXIST")).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
                IllegalArgumentException.class,
                () -> userService.patchUser(1L, patch)
        );
        assertThat(thrown.getMessage(), containsString("Role Not Found"));
    }

    @Test
    void patchUser_OnlyUsernameProvided_UpdatesOnlyUsername() throws ClashingUserException, UserNotFoundException {
        Role oldRole = new Role();
        oldRole.setName("USER");
        User dbUser = User.builder()
                .id(2L)
                .username("olduser")
                .email("old@example.com")
                .password("oldpass")
                .firstname("Old")
                .lastname("User")
                .roles(Set.of(oldRole))
                .build();

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
        assertThat(result.getRoles(), contains(oldRole));
    }

    @Test
    void deleteUser_UserExists_DeletesUser() throws UserNotFoundException {
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