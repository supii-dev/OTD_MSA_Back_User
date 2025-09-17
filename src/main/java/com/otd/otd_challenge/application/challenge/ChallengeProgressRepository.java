package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.entity.ChallengeProgress;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ChallengeProgressRepository extends JpaRepository<ChallengeProgress, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE ChallengeProgress SET isSuccess = true WHERE cpId = :cpId")
    int updateIsSuccess(@Param("cpId") Long cpId);
}
