package com.edu.tutor_platform.user.repository;

import com.edu.tutor_platform.user.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    Optional<LoginAttempt> findByIpAddress(String ipAddress);
}
