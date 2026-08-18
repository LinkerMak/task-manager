package com.example.task_manager_backend.repositories;

import com.example.task_manager_backend.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String normalizedEmail);

    Optional<User> findByEmail(String normalizedEmail);
}
