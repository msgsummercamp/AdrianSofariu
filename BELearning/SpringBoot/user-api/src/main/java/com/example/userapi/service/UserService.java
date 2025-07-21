package com.example.userapi.service;

import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.PersistenceExceptionHandler;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.example.userapi.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Service - Fetching all users from the repository");
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.info("Service - Fetching user with ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        log.info("Service - Fetching user with username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        log.info("Service - Fetching user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User addUser(User user) throws ClashingUserException, IllegalArgumentException, DataIntegrityViolationException, ConstraintViolationException {
        User addedUser = null;
        log.info("Service - Attempting to save new user: {}", user.getUsername());
        try {
            addedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // Centralized handling for unique and possibly non-null violations
            PersistenceExceptionHandler.handleConstraintViolationExceptions(e);
        }
        return addedUser;
    }

    @Override
    @Transactional
    public User updateUser(User user) throws ClashingUserException, UserNotFoundException, IllegalArgumentException, DataIntegrityViolationException, ConstraintViolationException  {
        // First, check if the user to update actually exists.
        User userToUpdate = userRepository.findById(user.getId())
                .orElseThrow(() -> {
                    log.warn("User with ID: {} not found for update", user.getId());
                    return new UserNotFoundException("User with ID: " + user.getId() + " not found for update.");
                });

        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setPassword(user.getPassword());
        userToUpdate.setFirstname(user.getFirstname());
        userToUpdate.setLastname(user.getLastname());

        log.info("Service - Attempting to update user with ID: {}", user.getId());
        User updatedUser = null;
        try {
            updatedUser = userRepository.save(userToUpdate);
        } catch (DataIntegrityViolationException e) {
            // Centralized handling for unique and possibly non-null violations
            PersistenceExceptionHandler.handleConstraintViolationExceptions(e);
        }
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            log.warn("User with ID: {} not found for deletion", id);
            throw new UserNotFoundException("User with ID: " + id + " not found for deletion.");
        }
        log.info("Service - Deleting user with ID: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public long countUsers() {
        log.info("Counting total number of users in the repository");
        return userRepository.countUsers();
    }


}
