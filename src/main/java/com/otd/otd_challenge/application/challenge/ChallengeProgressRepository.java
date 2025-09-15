package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.entity.ChallengeProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeProgressRepository extends JpaRepository<ChallengeProgress, Long> {
}
