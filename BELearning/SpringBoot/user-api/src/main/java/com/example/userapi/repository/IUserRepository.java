package com.example.userapi.repository;

import com.example.userapi.model.User;

import java.util.List;

public interface IUserRepository {
    /**
     * Fetches all users from the repository.
     *
     * @return a list of all users
     */
    List<User> findAll();

    /**
     * Fetches a limited number of users from the repository.
     *
     * @param count the maximum number of users to return, should be positive and greater than zero
     * @return a list of users, limited to the specified count
     */
    List<User> getUsers(int count);
}
