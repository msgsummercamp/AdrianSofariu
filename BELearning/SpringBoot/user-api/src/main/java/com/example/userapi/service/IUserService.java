package com.example.userapi.service;

import java.util.List;
import com.example.userapi.model.User;

public interface IUserService {
    /**
     * Fetches all users from the service.
     *
     * @return a list of all users
     */
    List<User> getAllUsers();

    /**
     * Fetches a limited number of users from the service.
     *
     * @param count the maximum number of users to return
     * @return a list of users, limited to the specified count
     */
    List<User> getUsers(int count);
}
