package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.entity.ChallengeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeDefinitionRepository extends JpaRepository<ChallengeDefinition, Long> {

    List<ChallengeDefinition> findByCdType(String cdPeriod);
}
