package com.example.userapi.repository;

import com.example.userapi.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository implements IUserRepository {

    private final List<User> users = new ArrayList<>();

    public UserRepository(){
        initUserList();
    }

    private void initUserList() {
        users.add(new User(1L, "Alice", "alice@example.com"));
        users.add(new User(2L, "Bob", "bob@example.com"));
    }

    @Override
    public List<User> findAll() {
        return users;
    }



}
