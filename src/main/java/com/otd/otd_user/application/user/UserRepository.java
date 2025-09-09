package com.otd.otd_user.application.user;

import com.otd.configuration.security.SignInProviderType;
import com.otd.otd_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUidAndProviderType(String uid, SignInProviderType signInProviderType);
    boolean existsByEmail(String email);
}