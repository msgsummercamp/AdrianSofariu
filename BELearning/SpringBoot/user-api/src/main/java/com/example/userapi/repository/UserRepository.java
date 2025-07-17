package com.example.userapi.repository;

import com.example.userapi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository implements IUserRepository {

    private final List<User> users = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public UserRepository(){
        initUserList();
    }

    private void initUserList() {
        logger.info("Initializing user list");
        users.add(new User(1L, "Alice", "alice@example.com"));
        users.add(new User(2L, "Bob", "bob@example.com"));
        logger.debug("User list initialized with {} users", users.size());
    }

    @Override
    public List<User> findAll() {
        logger.info("Returning all users");
        return users;
    }



}
