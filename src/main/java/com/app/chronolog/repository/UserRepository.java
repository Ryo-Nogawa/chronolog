package com.app.chronolog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.chronolog.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByUsername(String username);

    public Optional<User> findByEmployeeId(String employeeId);

    public boolean existsByUsername(String username);
}
