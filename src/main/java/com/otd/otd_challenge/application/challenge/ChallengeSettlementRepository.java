package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.entity.ChallengeSettlementLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeSettlementRepository extends JpaRepository<ChallengeSettlementLog, Long> {
}
