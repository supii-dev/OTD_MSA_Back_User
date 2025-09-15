package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.application.challenge.model.ChallengeDefinitionGetRes;
import com.otd.otd_challenge.application.challenge.model.ChallengeProgressGetReq;
import com.otd.otd_challenge.application.challenge.model.ChallengeProgressGetRes;
import com.otd.otd_challenge.entity.ChallengeDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public Map<String, Object> getChallengeList() {
        List<ChallengeDefinitionGetRes> res = challengeMapper.findAll();

        List<ChallengeDefinitionGetRes> personal = new ArrayList<>();
        List<ChallengeDefinitionGetRes> weekly = new ArrayList<>();
        List<ChallengeDefinitionGetRes> competition = new ArrayList<>();

        for (ChallengeDefinitionGetRes challengeDefinitionGetRes : res) {
            // 파일명 변경
            challengeDefinitionGetRes.setImage(imgPath + challengeDefinitionGetRes.getImage());
            switch (challengeDefinitionGetRes.getType()) {
                case "personal" -> personal.add(challengeDefinitionGetRes);
                case "weekly" -> weekly.add(challengeDefinitionGetRes);
                case "competition" -> competition.add(challengeDefinitionGetRes);
            }
        }
        Map<String, List<ChallengeDefinitionGetRes>> grouping = competition.stream()
                .collect(Collectors.groupingBy(ChallengeDefinitionGetRes::getName));

        Map<String, Object> dto = new HashMap<>();

        dto.put("personalChallenge", personal);
        dto.put("weeklyChallenge", weekly);
        dto.put("competitionChallenge", grouping);

        log.info("ChallengeService getChallengeList dto: {}", dto);
        return dto;
    }

    public Map<String, List<ChallengeProgressGetRes>> getSelectedList(ChallengeProgressGetReq req) {
        List<ChallengeProgressGetRes> res = challengeMapper.findAllProgressFromUserId(req);

        List<ChallengeProgressGetRes> personal = new ArrayList<>();
        List<ChallengeProgressGetRes> weekly = new ArrayList<>();
        List<ChallengeProgressGetRes> competition = new ArrayList<>();

        for (ChallengeProgressGetRes challengeProgressGetRes : res) {
            challengeProgressGetRes.setImage(imgPath + challengeProgressGetRes.getImage());
            switch (challengeProgressGetRes.getType()) {
                case "personal" -> personal.add(challengeProgressGetRes);
                case "weekly" -> weekly.add(challengeProgressGetRes);
                case "competition" -> competition.add(challengeProgressGetRes);
            }
        }
        Map<String, List<ChallengeProgressGetRes>> dto = new HashMap<>();

        dto.put("personalChallenge", personal);
        dto.put("weeklyChallenge", weekly);
        dto.put("competitionChallenge", competition);

        log.info(" dto: {}", dto);
        return dto;
    }

    public List<ChallengeDefinitionGetRes> getChallenge(ChallengeProgressGetReq req) {
        return challengeMapper.findByType(req);
    }
}
