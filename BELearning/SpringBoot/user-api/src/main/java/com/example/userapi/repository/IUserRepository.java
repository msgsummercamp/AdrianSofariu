package com.example.userapi.repository;

import com.example.userapi.model.User;

import java.util.List;

public interface IUserRepository {
    List<User> findAll();
}
