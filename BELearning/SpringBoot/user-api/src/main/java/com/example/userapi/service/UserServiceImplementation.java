package com.example.userapi.service;

import com.example.userapi.dto.PatchUserDTO;
import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.PersistenceExceptionHandler;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.example.userapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImplementation.class);

    public UserServiceImplementation(UserRepository userRepository) {
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
    public User addUser(User user) throws ClashingUserException {
        User newUser = null;
        try {
            logger.info("Service - Attempting to save new user: {}", user.getUsername());
            newUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e){
            PersistenceExceptionHandler.handleConstraintViolationExceptions(e);
        }
        return newUser;
    }

    @Override
    public User updateUser(User user) throws UserNotFoundException, ClashingUserException{
        User updatedUser = null;
        try {
            User userToUpdate = userRepository.findById(user.getId())
                    .orElseThrow(() -> {
                        logger.warn("User with ID: {} not found for update", user.getId());
                        return new UserNotFoundException("User with ID: " + user.getId() + " not found for update.");
                    });

            updateUserFields(user, userToUpdate);

            logger.info("Service - Attempting to update user with ID: {}", user.getId());
            updatedUser = userRepository.save(userToUpdate);
        }
        catch (DataIntegrityViolationException e) {
            PersistenceExceptionHandler.handleConstraintViolationExceptions(e);
        }
        return updatedUser;
    }

    @Override
    public User patchUser(Long id, PatchUserDTO patchUserDTO) throws UserNotFoundException, ClashingUserException {
        User patchedUser = null;
        try {
            User userToUpdate = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " not found for patch."));

            patchUserFromDTO(patchUserDTO, userToUpdate);

            logger.info("Service - Patching user with ID: {}", id);
            patchedUser = userRepository.save(userToUpdate);
        }
        catch (DataIntegrityViolationException e) {
            PersistenceExceptionHandler.handleConstraintViolationExceptions(e);
        }
        return patchedUser;
    }

    @Override
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

    /**
     * Updates the fields of the target user with the values from the source user.
     *
     * @param source the user containing new values
     * @param target the user to be updated
     */
    private void updateUserFields(User source, User target){
        target.setUsername(source.getUsername());
        target.setEmail(source.getEmail());
        target.setPassword(source.getPassword());
        target.setFirstname(source.getFirstname());
        target.setLastname(source.getLastname());
    }

    /**
     * Patches the target user with the values from the PatchUserDTO.
     * This method allows partial updates to a user.
     * It updates only the fields that are not null in the DTO.
     *
     * @param patchDTO the DTO containing fields to update
     * @param target the user to be patched
     */
    private void patchUserFromDTO(PatchUserDTO patchDTO, User target) {
        if (patchDTO.getUsername() != null) target.setUsername(patchDTO.getUsername());
        if (patchDTO.getEmail() != null) target.setEmail(patchDTO.getEmail());
        if (patchDTO.getPassword() != null) target.setPassword(patchDTO.getPassword());
        if (patchDTO.getFirstname() != null) target.setFirstname(patchDTO.getFirstname());
        if (patchDTO.getLastname() != null) target.setLastname(patchDTO.getLastname());
    }
}
