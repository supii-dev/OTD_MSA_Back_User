package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.application.challenge.model.ChallengeGetRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChallengeMapper {
    List<ChallengeGetRes> findAll();
}
