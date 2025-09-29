package com.otd.otd_challenge.application.challenge.Repository;

import com.otd.otd_challenge.entity.ChallengePointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengePointRepository extends JpaRepository<ChallengePointHistory, Long> {

}
