package com.example.userapi.service;

import com.example.userapi.repository.IUserRepository;
import org.springframework.stereotype.Service;
import com.example.userapi.model.User;
import java.util.List;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
