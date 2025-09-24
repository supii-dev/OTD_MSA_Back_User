package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.application.challenge.model.settlement.ChallengeSettlementDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeSchedulerService {

  private final ChallengeSettlementMapper challengeSettlementMapper;

  public void weeklySettlement(LocalDate startDate, LocalDate endDate) {
    ChallengeSettlementDto dto = ChallengeSettlementDto.builder().startDate(startDate).endDate(endDate).type("weekly").build();
    List<Long> userIds = challengeSettlementMapper.findByUserId(dto);

    for (Long userId : userIds) {
      int point = 0;
      int xp = 0;
    }
  }
}
