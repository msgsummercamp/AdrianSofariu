package com.example.userapi.unit.service;

import com.example.userapi.dto.PatchUserDTO;
import com.example.userapi.dto.UserDTO;
import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.model.User;
import com.example.userapi.model.Role;
import com.example.userapi.repository.RoleRepository;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.service.UserServiceImplementation;
import com.example.userapi.unit.testobjects.TestRolesFactory;
import com.example.userapi.unit.testobjects.TestUsersFactory;
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
        List<User> users = TestUsersFactory.createUserList();

        Pageable pageable = Pageable.unpaged();
        Page<User> userPage = new org.springframework.data.domain.PageImpl<>(users, pageable, 2);

        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<User> result = userService.getUsers(pageable);

        assertThat(result.getContent(), hasSize(users.size()));
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        User user = TestUsersFactory.createUserWithIdentifiers();
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(user.getId());

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
        User user = TestUsersFactory.createUserWithIdentifiers();
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByUsername(user.getUsername());

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(user));
    }

    @Test
    void getUserByEmail_UserExists_ReturnsUser() {
        User user = TestUsersFactory.createUserWithIdentifiers();
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail(user.getEmail());

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

        UserDTO userDTO = TestUsersFactory.createUserDTO("password", Set.of("USER"));


        Role role = TestRolesFactory.getUserRole();
        User user = TestUsersFactory.createTestUser("encodedPassword", Set.of(role));

        Mockito.when(roleRepository.findById("USER")).thenReturn(Optional.of(role));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User saved = userService.addUser(userDTO);

        assertThat(saved.getUsername(), is(userDTO.getUsername()));
        assertThat(saved.getEmail(), is(userDTO.getEmail()));
        assertThat(saved.getPassword(), is(user.getPassword()));
        assertThat(saved.getRoles(), contains(role));
    }

    @Test
    void addUser_RoleNotFound_ThrowsException() {
        UserDTO userDTO = TestUsersFactory.createUserDTO("password", Set.of("NOT_EXIST"));

        Mockito.when(roleRepository.findById("NOT_EXIST")).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addUser(userDTO)
        );
        assertThat(thrown.getMessage(), containsString("Role Not Found"));
    }

    @Test
    void updateUser_UserExists_ValidUpdate_ReturnsUpdatedUser() throws ClashingUserException, UserNotFoundException {
        UserDTO updateDTO = TestUsersFactory.createUserDTO("password", Set.of("USER"));

        Role role = TestRolesFactory.getUserRole();

        User dbUser = TestUsersFactory.createPreUpdateUser("somePassword", Set.of(role));

        Mockito.when(roleRepository.findById("USER")).thenReturn(Optional.of(role));
        Mockito.when(userRepository.findById(dbUser.getId())).thenReturn(Optional.of(dbUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updated = userService.updateUser(updateDTO, 1L);

        assertThat(updated.getUsername(), is(updateDTO.getUsername()));
        assertThat(updated.getEmail(), is(updateDTO.getEmail()));
        assertThat(updated.getPassword(), is("encodedPassword"));
        assertThat(updated.getRoles(), contains(role));
    }

    @Test
    void updateUser_UserDoesNotExist_ThrowsUserNotFoundException() {
        UserDTO updateDTO = TestUsersFactory.createUserDTO("password", Set.of("USER"));

        Role role = TestRolesFactory.getUserRole();

        Mockito.when(roleRepository.findById("USER")).thenReturn(Optional.of(role));
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(updateDTO, 99L)
        );
        assertThat(thrown.getMessage(), containsString("not found"));
    }

    @Test
    void updateUser_RoleNotFound_ThrowsException() {
        UserDTO UserDTO = TestUsersFactory.createUserDTO("password", Set.of("NOT_EXIST"));

        Mockito.when(roleRepository.findById("NOT_EXIST")).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(UserDTO, 1L)
        );
        assertThat(thrown.getMessage(), containsString("Role Not Found"));
    }

    @Test
    void patchUser_UserExists_UpdatesProvidedFields() throws ClashingUserException, UserNotFoundException {
        Role oldRole = TestRolesFactory.getUserRole();
        User dbUser = TestUsersFactory.createTestUser("somePassword", Set.of(oldRole));

        PatchUserDTO patch = TestUsersFactory.createPatchUserDTO(Set.of("ADMIN"));

        Role newRole = TestRolesFactory.getAdminRole();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(dbUser));
        Mockito.when(roleRepository.findById("ADMIN")).thenReturn(Optional.of(newRole));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.patchUser(1L, patch);

        assertThat(result.getUsername(), is(patch.getUsername()));
        assertThat(result.getEmail(), is(patch.getEmail()));
        assertThat(result.getPassword(), is("encodedPassword"));
        assertThat(result.getFirstname(), is(patch.getFirstname()));
        assertThat(result.getLastname(), is(patch.getLastname()));
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
        Role oldRole = TestRolesFactory.getUserRole();
        User dbUser = TestUsersFactory.createTestUser("encodedPassword", Set.of(oldRole));

        PatchUserDTO patch = TestUsersFactory.createPatchUserDTO(Set.of("ADMIN"));

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
        Role oldRole = TestRolesFactory.getUserRole();
        User dbUser = TestUsersFactory.createTestUser("encodedPassword", Set.of(oldRole));

        PatchUserDTO patch = TestUsersFactory.createPatchUserDTOWithUsername("patcheduser");

        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(dbUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.patchUser(2L, patch);

        assertThat(result.getUsername(), is(patch.getUsername()));
        assertThat(result.getEmail(), is(dbUser.getEmail()));
        assertThat(result.getPassword(), is(dbUser.getPassword()));
        assertThat(result.getFirstname(), is(dbUser.getFirstname()));
        assertThat(result.getLastname(), is(dbUser.getLastname()));
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