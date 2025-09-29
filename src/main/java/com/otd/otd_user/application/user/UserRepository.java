package com.otd.otd_user.application.user;

import com.otd.configuration.security.SignInProviderType;
import com.otd.otd_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUidAndProviderType(String uid, SignInProviderType signInProviderType);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User findByUserId(Long userId);

    @Query("update User u set u.point = :point where u.userId = :userId")
    @Modifying
    void addPointByUserId(int point, Long userId);

    @Modifying
    @Query("UPDATE User u Set u.refreshToken = :refreshToken WHERE u.userId = :userId")
    void updateRefreshToken(Long userId, String refreshToken);
}