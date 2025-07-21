package com.example.userapi.service;

import com.example.userapi.dto.PatchUserDTO;
import com.example.userapi.dto.UpdateUserDTO;
import com.example.userapi.dto.UserDTO;
import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.PersistenceExceptionHandler;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.model.Role;
import com.example.userapi.repository.RoleRepository;
import com.example.userapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.userapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImplementation.class);

    public UserServiceImplementation(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
    public User addUser(UserDTO user) throws ClashingUserException {
        User newUser = null;
        try {

            String roleName = user.getRole();
            Role userRole = roleRepository.findById(roleName)
                            .orElseThrow(() -> new IllegalArgumentException("Role Not Found: " + roleName));
            User userToAdd = User.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .password(passwordEncoder.encode(user.getPassword()))
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .role(userRole)
                    .build();
            logger.info("Service - Attempting to save new user: {}", user.getUsername());
            newUser = userRepository.save(userToAdd);
        } catch (DataIntegrityViolationException e){
            PersistenceExceptionHandler.handleConstraintViolationExceptions(e);
        }
        return newUser;
    }

    @Override
    public User updateUser(UpdateUserDTO user, Long id) throws UserNotFoundException, ClashingUserException{
        User updatedUser = null;
        try {
            String roleName = user.getRole();
            Role userRole = roleRepository.findById(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role Not Found: " + roleName));
            User updateReqUser = User.builder()
                    .id(id)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .password(passwordEncoder.encode(user.getPassword()))
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .role(userRole)
                    .build();
            User userToUpdate = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("User with ID: {} not found for update", id);
                        return new UserNotFoundException("User with ID: " + id + " not found for update.");
                    });

            updateUserFields(updateReqUser, userToUpdate);

            logger.info("Service - Attempting to update user with ID: {}", id);
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
        target.setRole(source.getRole());
    }

    /**
     * Patches the target user with the values from the PatchUserDTO.
     * This method allows partial updates to a user.
     * It updates only the fields that are not null in the DTO.
     *
     * @param patchDTO the DTO containing fields to update
     * @param target the user to be patched
     *
     * @throws IllegalArgumentException if the role ID in the DTO does not exist
     */
    private void patchUserFromDTO(PatchUserDTO patchDTO, User target) {
        if (patchDTO.getUsername() != null) target.setUsername(patchDTO.getUsername());
        if (patchDTO.getEmail() != null) target.setEmail(patchDTO.getEmail());
        if (patchDTO.getPassword() != null) target.setPassword(passwordEncoder.encode(patchDTO.getPassword()));
        if (patchDTO.getFirstname() != null) target.setFirstname(patchDTO.getFirstname());
        if (patchDTO.getLastname() != null) target.setLastname(patchDTO.getLastname());
        if (patchDTO.getRole() != null) {
            Role userRole = roleRepository.findById(patchDTO.getRole())
                    .orElseThrow(() -> new IllegalArgumentException("Role Not Found: " + patchDTO.getRole()));
            target.setRole(userRole);
        }
    }
}
