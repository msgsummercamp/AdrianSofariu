package com.example.userapi.unit.testobjects;

import com.example.userapi.dto.PatchUserDTO;
import com.example.userapi.dto.UserDTO;
import com.example.userapi.model.Role;
import com.example.userapi.model.User;

import java.util.List;
import java.util.Set;

public class TestUsersFactory {

    /**
     * Creates a list of User objects for testing purposes.
     *
     * @return a List of User objects
     */
    public static List<User> createUserList() {

        User user1 = User.builder().username("user1").roles(Set.of()).build();
        User user2 = User.builder().username("user2").roles(Set.of()).build();

        return List.of(user1, user2);
    }

    /**
     * Creates a User object only having an Id, a Username and an Email.
     *
     * @return a User object
     */
    public static User createUserWithIdentifiers() {
        return User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
    }

    /**
     * Creates a UserDTO object for testing purposes.
     * The roles and password can be customized.
     *
     * @param password the password to be set for the user
     * @param roles the roles to be assigned to the user
     * @return a UserDTO object
     */
    public static UserDTO createUserDTO(String password, Set<String> roles) {
        UserDTO user = new UserDTO();
        user.setUsername("exampleuser");
        user.setEmail("test@example.com");
        user.setPassword(password);
        user.setRoles(roles);
        user.setFirstname("Example");
        user.setLastname("User");
        return user;
    }

    /**
     * Creates a User object with all fields set for testing purposes.
     * The roles and password can be customized.
     *
     * @param password the password to be set for the user
     * @param roles the roles to be assigned to the user
     * @return a User object
     */
    public static User createTestUser(String password, Set<Role> roles){
        return User.builder()
                .id(1L)
                .username("exampleuser")
                .email("test@example.com")
                .password(password)
                .firstname("Example")
                .lastname("User")
                .roles(roles)
                .build();
    }


    /**
     * Creates a User object for pre-update testing purposes.
     * The roles and password can be customized.
     *
     * @param password the password to be set for the user
     * @param roles the roles to be assigned to the user
     * @return a User object
     */
    public static User createPreUpdateUser(String password, Set<Role> roles){
        return User.builder()
                .id(1L)
                .username("preUpdateUser")
                .email("pre@email.com")
                .password(password)
                .firstname("Pre")
                .lastname("Update")
                .roles(roles)
                .build();
    }


    /**
     * Creates a PatchUserDTO object for testing purposes.
     * The roles can be customized.
     *
     * @param roles the roles to be assigned to the user
     * @return a PatchUserDTO object
     */
    public static PatchUserDTO createPatchUserDTO(Set<String> roles) {
        PatchUserDTO patch = new PatchUserDTO();
        patch.setUsername("newuser");
        patch.setEmail("new@example.com");
        patch.setPassword("newpass");
        patch.setFirstname("NewFirst");
        patch.setLastname("NewLast");
        patch.setRoles(roles);
        return patch;
    }

    /**
     * Creates a PatchUserDTO object where only the username is changed.
     *
     * @param newUsername the new username to be set
     * @return a PatchUserDTO object with a changed username
     */
    public static PatchUserDTO createPatchUserDTOWithUsername(String newUsername) {
        PatchUserDTO patch = new PatchUserDTO();
        patch.setUsername(newUsername);
        return patch;
    }



}
