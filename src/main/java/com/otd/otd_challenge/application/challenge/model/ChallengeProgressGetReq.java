package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChallengeProgressGetReq {
  private Long userId;
  private String year;
  private String month;
  private Long cdId;
  private String type;
}
