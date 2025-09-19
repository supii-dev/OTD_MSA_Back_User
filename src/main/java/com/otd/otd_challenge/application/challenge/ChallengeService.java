package com.otd.otd_challenge.application.challenge;

import com.otd.configuration.model.ResultResponse;
import com.otd.otd_challenge.application.challenge.model.*;
import com.otd.otd_challenge.entity.ChallengeDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeMapper challengeMapper;
    private final ChallengeDefinitionRepository challengeDefinitionRepository;
    @Value("${constants.file.challenge-pic}")
    private String imgPath;

    private void addImgPath(List<?> list) {
        for (Object o : list) {
            if (o instanceof ChallengeDefinitionGetRes cd) {
                cd.setImage(imgPath + cd.getImage());
            } else if (o instanceof ChallengeProgressGetRes cp) {
                cp.setImage(imgPath + cp.getImage());
            }
        }
    }
    public Map<String, Object> getChallengeList() {
        List<ChallengeDefinitionGetRes> res = challengeMapper.findAll();

        List<ChallengeDefinitionGetRes> personal = new ArrayList<>();
        List<ChallengeDefinitionGetRes> weekly = new ArrayList<>();
        List<ChallengeDefinitionGetRes> competition = new ArrayList<>();
        List<ChallengeDefinitionGetRes> daily = new ArrayList<>();

        addImgPath(res);

        for (ChallengeDefinitionGetRes challengeDefinitionGetRes : res) {

            switch (challengeDefinitionGetRes.getType()) {
                case "personal" -> personal.add(challengeDefinitionGetRes);
                case "weekly" -> weekly.add(challengeDefinitionGetRes);
                case "competition" -> competition.add(challengeDefinitionGetRes);
                case "daily" -> daily.add(challengeDefinitionGetRes);
            }
        }
        Map<String, List<ChallengeDefinitionGetRes>> grouping = competition.stream()
                .collect(Collectors.groupingBy(ChallengeDefinitionGetRes::getName));

        Map<String, Object> dto = new HashMap<>();

        dto.put("personalChallenge", personal);
        dto.put("weeklyChallenge", weekly);
        dto.put("competitionChallenge", grouping);
        dto.put("dailyMission", daily);

        log.info("ChallengeService getChallengeList dto: {}", dto);
        return dto;
    }

    public Map<String, List<ChallengeProgressGetRes>> getSelectedList(ChallengeProgressGetReq req) {
        List<ChallengeProgressGetRes> res = challengeMapper.findAllProgressFromUserId(req);

        List<ChallengeProgressGetRes> personal = new ArrayList<>();
        List<ChallengeProgressGetRes> weekly = new ArrayList<>();
        List<ChallengeProgressGetRes> competition = new ArrayList<>();
        List<ChallengeProgressGetRes> daily = new ArrayList<>();

        addImgPath(res);
        for (ChallengeProgressGetRes challengeProgressGetRes : res) {

            switch (challengeProgressGetRes.getType()) {
                case "personal" -> personal.add(challengeProgressGetRes);
                case "weekly" -> weekly.add(challengeProgressGetRes);
                case "competition" -> competition.add(challengeProgressGetRes);
                case "daily" -> daily.add(challengeProgressGetRes);
            }
        }
        Map<String, List<ChallengeProgressGetRes>> dto = new HashMap<>();

        dto.put("personalChallenge", personal);
        dto.put("weeklyChallenge", weekly);
        dto.put("competitionChallenge", competition);
        dto.put("dailyMission", daily);

        log.info(" dto: {}", dto);
        return dto;
    }

    public List<ChallengeDefinitionGetRes> getChallenge(ChallengeProgressGetReq req) {
        List<ChallengeDefinitionGetRes> res = challengeMapper.findByType(req);
        addImgPath(res);
        return res;
    }

    public Map<String, List<ChallengeDefinitionGetRes>> getMapChallenge(ChallengeProgressGetReq req) {
        List<ChallengeDefinitionGetRes> res = challengeMapper.findByTypeForCompetition(req);
        addImgPath(res);
        Map<String, List<ChallengeDefinitionGetRes>> grouping = res.stream()
                    .collect(Collectors.groupingBy(ChallengeDefinitionGetRes::getName));

        return grouping;
    }

    public ChallengeDetailGetRes getDetail(Long cdId, ChallengeProgressGetReq req) {
        req.setCdId(cdId);
        ChallengeDetailGetRes res = challengeMapper.findProgressByUserIdAndCdId(req);
        List<ChallengeRankGetRes> rank = challengeMapper.findRankingLimitFive(req);
        if (res.getGoal() > res.getTotalRecord()) {
            res.setPercent(((res.getTotalRecord() / res.getGoal()) * 100 ));
        } else {
            res.setPercent(100.0);
        }

        res.setRanking(rank);

        return res;
    }
}
