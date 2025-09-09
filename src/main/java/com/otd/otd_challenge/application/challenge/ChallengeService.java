package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.application.challenge.model.ChallengeGetRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeMapper challengeMapper;

    public  Map<String, Object> getChallengeList() {
        List<ChallengeGetRes> res = challengeMapper.findAll();

        List<ChallengeGetRes> daily = new ArrayList<>();
        List<ChallengeGetRes> weekly = new ArrayList<>();
        List<ChallengeGetRes> monthly = new ArrayList<>();

        for (ChallengeGetRes challengeGetRes : res) {
            switch (challengeGetRes.getPeriod()) {
                case "daily" -> daily.add(challengeGetRes);
                case "weekly" -> weekly.add(challengeGetRes);
                case "monthly" -> monthly.add(challengeGetRes);
            }
        }
        Map<String, List<ChallengeGetRes>> grouping = monthly.stream()
                .collect(Collectors.groupingBy(ChallengeGetRes::getName));

        Map<String, Object> dto = new HashMap<>();

        dto.put("dailyChallenge", daily);
        dto.put("weeklyChallenge", weekly);
        dto.put("monthlyChallenge", grouping);

        log.info("ChallengeService getChallengeList dto: {}", dto);
        return dto;
    }
}
