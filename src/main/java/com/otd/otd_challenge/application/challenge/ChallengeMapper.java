package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.application.challenge.model.ChallengeDefinitionGetRes;
import com.otd.otd_challenge.application.challenge.model.ChallengeProgressGetRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChallengeMapper {
    List<ChallengeDefinitionGetRes> findAll();
    List<ChallengeProgressGetRes> findAllProgressFromUserId(Long userId);
}
