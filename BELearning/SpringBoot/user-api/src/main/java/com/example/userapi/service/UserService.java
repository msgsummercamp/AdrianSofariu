package com.example.userapi.service;

import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.userapi.model.User;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Service - Fetching all users from the repository");
        return userRepository.findAll();
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
    public User addUser(User user) throws ClashingUserException {
        Optional<User> existingUser = findClashingUser(user);
        if (existingUser.isPresent()) {
            logger.error("User with username '{}' or email '{}' already exists", user.getUsername(), user.getEmail());
            throw new ClashingUserException("User with username '" + user.getUsername() + "' or email '" + user.getEmail() + "' already exists.");
        }
        logger.info("Service - Saving user: {}", user.getUsername());
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) throws ClashingUserException, UserNotFoundException {
        Optional<User> userToUpdate = userRepository.findById(user.getId());
        if (userToUpdate.isEmpty())
        {
            logger.warn("User with ID: {} not found for deletion", user.getId());
            throw new UserNotFoundException("User with ID: " + user.getId() + " not found for deletion.");
        }
        logger.info("Updating user with ID: {}", user.getId());
        Optional<User> clashingUser = findClashingUser(user);
        if (clashingUser.isPresent()) {
            logger.error("User with username '{}' or email '{}' already exists", user.getUsername(), user.getEmail());
            throw new ClashingUserException("User with username '" + user.getUsername() + "' or email '" + user.getEmail() + "' already exists.");
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) throws UserNotFoundException {
        logger.info("Deleting user with ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
        {
            logger.warn("User with ID: {} not found for deletion", id);
            throw new UserNotFoundException("User with ID: " + id + " not found for deletion.");
        }
        userRepository.deleteById(id);
    }

    @Override
    public long countUsers() {
        logger.info("Counting total number of users in the repository");
        return userRepository.countUsers();
    }

    private Optional<User> findClashingUser(User user) {
        logger.info("Checking for existing user with username: {} or email: {}", user.getUsername(), user.getEmail());
        return userRepository.findFirstByUsernameOrEmailAndIdNot(user.getUsername(), user.getEmail(), user.getId() == null ? -1L : user.getId());
    }
}
