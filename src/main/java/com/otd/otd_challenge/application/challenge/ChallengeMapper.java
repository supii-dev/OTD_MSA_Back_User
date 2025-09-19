package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.application.challenge.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChallengeMapper {
    List<ChallengeDefinitionGetRes> findAll();
    List<ChallengeProgressGetRes> findAllProgressFromUserId(ChallengeProgressGetReq req);
    List<ChallengeDefinitionGetRes> findByType(ChallengeProgressGetReq req);
    List<ChallengeDefinitionGetRes> findByTypeForCompetition(ChallengeProgressGetReq req);
    ChallengeDetailPerGetRes findProgressByUserIdAndCdId(ChallengeProgressGetReq req);
    List<ChallengeRankGetRes> findTop5Ranking(ChallengeProgressGetReq req);
    List<ChallengeRankGetRes> findAroundMyRank(ChallengeProgressGetReq req);
    List<ChallengeDetailDayGetRes> findDayByUserIdAndCdId(ChallengeProgressGetReq req);
    int findSuccessChallenge(Long userId);
    int saveMissionRecordByUserIdAndCpId(ChallengeRecordMissionPostReq req);
}
