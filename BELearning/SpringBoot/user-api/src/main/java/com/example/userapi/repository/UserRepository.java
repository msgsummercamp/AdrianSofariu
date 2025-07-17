package com.example.userapi.repository;

import com.example.userapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findFirstByUsernameOrEmailAndIdNot(String username, String email, Long id);

    @Query("SELECT COUNT(u) FROM User u")
    long countUsers();
}
