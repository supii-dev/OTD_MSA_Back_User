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

    @Modifying
    @Query("UPDATE User u Set u.refreshToken = :refreshToken WHERE u.userId = :userId")
    void updateRefreshToken(Long userId, String refreshToken);

    // 포인트 조회
    @Query("SELECT u.point FROM User u WHERE u.userId = :userId")
    Optional<Integer> findPointByUserId(Long userId);

    // 포인트 등록
    @Query("update User u set u.point = :point where u.userId = :userId")
    @Modifying
    void addPointByUserId(int point, Long userId);

    // 포인트 수정, 차감
    @Modifying
    @Query("UPDATE User u SET u.point = u.point + :amount WHERE u.userId = :userId")
    void updatePointByUserId(int amount, Long userId);

    User findByProviderId(String providerId);
}