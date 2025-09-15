package com.otd.otd_challenge.application.challenge;

import com.otd.configuration.model.ResultResponse;
import com.otd.otd_challenge.application.challenge.model.ChallengeDefinitionGetRes;
import com.otd.otd_challenge.application.challenge.model.ChallengeProgressGetRes;
import com.otd.otd_challenge.entity.ChallengeDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/OTD/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    public Map<String, Object> getChallengeList() {
        return challengeService.getChallengeList();
    }

    @GetMapping("/selected")
    public Map<String, List<ChallengeProgressGetRes>> getSelectedList(@RequestParam Long userId) {
        return challengeService.getSelectedList(userId);
    }

    @GetMapping("list")
    public List<ChallengeDefinition> getChallenge(@RequestParam String keyword) {
        return challengeService.getChallenge(keyword);
    }
}