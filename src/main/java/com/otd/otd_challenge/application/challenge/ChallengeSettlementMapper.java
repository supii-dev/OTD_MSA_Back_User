package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.application.challenge.model.settlement.ChallengeSettlementDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChallengeSettlementMapper {
  List<Long> findByUserId(ChallengeSettlementDto dto);
}
