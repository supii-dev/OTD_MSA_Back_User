package com.otd.otd_user.application.user;

import com.otd.configuration.security.SignInProviderType;
import com.otd.otd_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUidAndProviderType(String uid, SignInProviderType signInProviderType);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User findByUserId(Long userId);
}