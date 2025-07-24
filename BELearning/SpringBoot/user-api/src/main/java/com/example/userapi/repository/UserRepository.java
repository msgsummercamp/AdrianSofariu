package com.example.userapi.repository;

import com.example.userapi.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u")
    long countUsers();

    boolean existsByUsername(@NotBlank(message = "Username is required") String username);

    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email);
}
