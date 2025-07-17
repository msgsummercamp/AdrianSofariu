package com.example.userapi.service;

import com.example.userapi.repository.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.userapi.model.User;
import java.util.List;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Fetching all users from the repository");
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsers(int count) {
        logger.info("Fetching up to {} users from the repository", count);
        return userRepository.getUsers(count);
    }
}
