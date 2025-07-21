package com.example.userapi.service;

import java.util.Optional;

import com.example.userapi.dto.PatchUserDTO;
import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.model.User;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing {@link User} entities.
 * Provides methods for CRUD operations and user retrieval.
 */
public interface UserService {

    /**
     * Retrieves all users from the repository.
     *
     * @return a list of all users
     */
    Page<User> getUsers(Pageable pageable);

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the ID of the user
     * @return an {@code Optional} containing the user if found, or empty if not
     */
    Optional<User> getUserById(Long id);

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user
     * @return an {@code Optional} containing the user if found, or empty if not
     */
    Optional<User> getUserByUsername(String username);

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email of the user
     * @return an {@code Optional} containing the user if found, or empty if not
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Add a user to the repository.
     * If there is an existing user with the same username or email, it will throw an exception.
     *
     * @param user the user to save
     * @return the saved user entity
     * @throws ClashingUserException if a user with the same username or email already exists
     * @throws IllegalArgumentException if the username, email or password is null or empty
     * @throws ConstraintViolationException if there are other constraint violations
     * @throws DataIntegrityViolationException if there is an unknown data integrity violation
     */
    User addUser(User user) throws ClashingUserException;

    /**
     * Updates an existing user in the repository.
     * If the user does not exist or the username or email already exists for another user, it will throw an exception.
     *
     * @param user the user to update
     * @return the updated user entity
     * @throws ClashingUserException if a user with the same username or email already exists
     * @throws UserNotFoundException if the user with the specified ID does not exist
     * @throws IllegalArgumentException if the username, email or password is null or empty
     * @throws ConstraintViolationException if there are other constraint violations
     * @throws DataIntegrityViolationException if there is an unknown data integrity violation
     */
    User updateUser(User user) throws ClashingUserException, UserNotFoundException;

    /**
     * Patches an existing user in the repository.
     * This method allows partial updates to a user.
     * If the user does not exist or the username or email already exists for another user, it will throw an exception.
     *
     * @param id the ID of the user to patch
     * @param patchUserDTO the DTO containing the fields to update
     * @return the updated user entity
     * @throws ClashingUserException if a user with the same username or email already exists
     * @throws UserNotFoundException if the user with the specified ID does not exist
     * @throws ConstraintViolationException if there are other constraint violations
     * @throws DataIntegrityViolationException if there is an unknown data integrity violation
     */
    User patchUser(Long id, PatchUserDTO patchUserDTO) throws ClashingUserException, UserNotFoundException;

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the ID of the user to delete
     * @throws UserNotFoundException if the user with the specified ID does not exist
     */
    void deleteUser(Long id) throws UserNotFoundException;

    /**
     * Counts the total number of users in the repository.
     *
     * @return the count of users
     */
    long countUsers();

}