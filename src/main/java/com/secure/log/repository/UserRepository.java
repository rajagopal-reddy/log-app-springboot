package com.secure.log.repository;

import com.secure.log.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName); // Correct method name to match 'userName' field
    Boolean existsByUserName(String userName); // Correct method name to match 'userName' field
    Boolean existsByEmail(String email);
}