package com.otd.otd_challenge.application.challenge.model.settlement;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Builder
@ToString
public class ChallengeSettlementDto {
  private LocalDate startDate;
  private LocalDate endDate;
  private String type;
}
