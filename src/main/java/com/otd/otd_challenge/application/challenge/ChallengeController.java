package com.otd.otd_challenge.application.challenge;

import com.otd.otd_challenge.application.challenge.model.ChallengeDto;
import com.otd.otd_challenge.application.challenge.model.ChallengeGetRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/OTD/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    public ChallengeDto getChallengeList() {
        return challengeService.getChallengeList();
    }
}
