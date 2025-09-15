package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.application.challenge.model.ChallengeDefinitionGetRes;
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

        List<ChallengeDefinitionGetRes> daily = new ArrayList<>();
        List<ChallengeDefinitionGetRes> weekly = new ArrayList<>();
        List<ChallengeDefinitionGetRes> monthly = new ArrayList<>();

        for (ChallengeDefinitionGetRes challengeDefinitionGetRes : res) {
            // 파일명 변경
            challengeDefinitionGetRes.setImage(imgPath + challengeDefinitionGetRes.getImage());
            switch (challengeDefinitionGetRes.getPeriod()) {
                case "daily" -> daily.add(challengeDefinitionGetRes);
                case "weekly" -> weekly.add(challengeDefinitionGetRes);
                case "monthly" -> monthly.add(challengeDefinitionGetRes);
            }
        }
        Map<String, List<ChallengeDefinitionGetRes>> grouping = monthly.stream()
                .collect(Collectors.groupingBy(ChallengeDefinitionGetRes::getName));

        Map<String, Object> dto = new HashMap<>();

        dto.put("dailyChallenge", daily);
        dto.put("weeklyChallenge", weekly);
        dto.put("monthlyChallenge", grouping);

        log.info("ChallengeService getChallengeList dto: {}", dto);
        return dto;
    }

    public Map<String, List<ChallengeProgressGetRes>> getSelectedList(Long userId) {
        List<ChallengeProgressGetRes> res = challengeMapper.findAllProgressFromUserId(userId);

        List<ChallengeProgressGetRes> daily = new ArrayList<>();
        List<ChallengeProgressGetRes> weekly = new ArrayList<>();
        List<ChallengeProgressGetRes> monthly = new ArrayList<>();

        for (ChallengeProgressGetRes challengeProgressGetRes : res) {
            challengeProgressGetRes.setImage(imgPath + challengeProgressGetRes.getImage());
            switch (challengeProgressGetRes.getPeriod()) {
                case "daily" -> daily.add(challengeProgressGetRes);
                case "weekly" -> weekly.add(challengeProgressGetRes);
                case "monthly" -> monthly.add(challengeProgressGetRes);
            }
        }
        Map<String, List<ChallengeProgressGetRes>> dto = new HashMap<>();

        dto.put("dailyChallenge", daily);
        dto.put("weeklyChallenge", weekly);
        dto.put("monthlyChallenge", monthly);

        log.info(" dto: {}", dto);
        return dto;
    }

    public List<ChallengeDefinition> getChallenge(String keyword) {
        return challengeDefinitionRepository.findByCdPeriod(keyword);
    }
}
