package com.example.userapi.unit.testobjects;

import com.example.userapi.model.Role;

import java.util.Set;

public class TestRolesFactory {

    /**
     * Creates a role with the name "ADMIN".
     * @return a Role object with the name "ADMIN"
     */
    public static Role getAdminRole() {
        Role role = new Role();
        role.setName("ADMIN");
        return role;
    }

    /**
     * Creates a role with the name "USER".
     * @return a Role object with the name "USER"
     */
    public static Role getUserRole() {
        Role role = new Role();
        role.setName("USER");
        return role;
    }

    /**
     * Returns a set of roles containing both ADMIN and USER roles.
     * @return a Set of Role objects containing ADMIN and USER
     */
    public static Set<Role> getRoles(){
        return Set.of(getAdminRole(), getUserRole());
    }

    /**
     * Returns a set of role names containing "ADMIN" and "USER".
     * @return a Set of String containing the names of the roles
     */
    public static Set<String> getRoleNames() {
        return Set.of(getAdminRole().getName(), getUserRole().getName());
    }
}
