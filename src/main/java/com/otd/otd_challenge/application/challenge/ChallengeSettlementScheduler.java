package com.otd.otd_challenge.application.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ChallengeSettlementScheduler {
  private final ChallengeSchedulerService challengeSchedulerService;

  @Scheduled(cron = "0 0 0 * * Mon")
  public void weeklySettlementScheduler() {
    LocalDate today = LocalDate.now();
    LocalDate startDate = today.minusWeeks(1);
    LocalDate endDate = today.minusDays(1);


    challengeSchedulerService.weeklySettlement(startDate, endDate);
  }

  @Scheduled(cron = "0 0 0 1 * ?")
  public void monthlySettlementScheduler() {

  }
}
