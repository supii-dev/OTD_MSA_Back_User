package com.otd.otd_user.application.email;

import com.otd.otd_user.entity.Inquiry;
import com.otd.otd_user.entity.MunheStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MunheRepository extends JpaRepository<Inquiry, Long> {

    // 특정 사용자의 문의 목록 조회
    List<Inquiry> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 사용자의 문의 개수
    int countByUserId(Long userId);

    // 상태별 문의 조회
    List<Inquiry> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, MunheStatus status);
}