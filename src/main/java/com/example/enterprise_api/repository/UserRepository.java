package com.example.enterprise_api.repository;

import com.example.enterprise_api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // 1. Exact match (Original)
    Optional<User> findByEmail(String email);

    // 2. 🔥 NEW: Case-insensitive search (Ignores uppercase/lowercase)
    Optional<User> findByEmailIgnoreCase(String email);
}