package com.example.userapi.service;

import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.userapi.model.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        logger.info("Service - Fetching all users with pagination: {}", pageable);
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        logger.info("Service - Fetching user with ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        logger.info("Service - Fetching user with username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        logger.info("Service - Fetching user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User addUser(User user) {
        logger.info("Service - Attempting to save new user: {}", user.getUsername());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) throws UserNotFoundException {
        // First, check if the user to update actually exists.
        User userToUpdate = userRepository.findById(user.getId())
                .orElseThrow(() -> {
                    logger.warn("User with ID: {} not found for update", user.getId());
                    return new UserNotFoundException("User with ID: " + user.getId() + " not found for update.");
                });

        // Copy relevant fields from the input 'user' to the 'userToUpdate' (fetched from DB)
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setPassword(user.getPassword());
        userToUpdate.setFirstname(user.getFirstname());
        userToUpdate.setLastname(user.getLastname());

        logger.info("Service - Attempting to update user with ID: {}", user.getId());
        return userRepository.save(userToUpdate);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            logger.warn("User with ID: {} not found for deletion", id);
            throw new UserNotFoundException("User with ID: " + id + " not found for deletion.");
        }
        logger.info("Service - Deleting user with ID: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public long countUsers() {
        logger.info("Counting total number of users in the repository");
        return userRepository.countUsers();
    }
}
