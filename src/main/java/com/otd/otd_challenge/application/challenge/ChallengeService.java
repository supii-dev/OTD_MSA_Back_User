package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.application.challenge.model.ChallengeDto;
import com.otd.otd_challenge.application.challenge.model.ChallengeGetRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeMapper challengeMapper;

    public ChallengeDto getChallengeList() {
        List<ChallengeGetRes> res = challengeMapper.findAll();

        ChallengeDto dto = new ChallengeDto();

        for (ChallengeGetRes challengeGetRes : res) {
            switch (challengeGetRes.getCdPeriod()) {
                case "daily" -> dto.setDailyChallenges(res);
                case "weekly" -> dto.setWeeklyChallenges(res);
                case "monthly" -> dto.setMonthlyChallenges(res);
            }
        }
        return dto;
    }
}
