package com.otd.otd_challenge.application.challenge;

import com.otd.configuration.model.ResultResponse;
import com.otd.otd_challenge.application.challenge.model.ChallengeDefinitionGetRes;
import com.otd.otd_challenge.application.challenge.model.ChallengeDetailGetRes;
import com.otd.otd_challenge.application.challenge.model.ChallengeProgressGetReq;
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

    @GetMapping("/list")
    public Map<String, Object> getChallengeList() {
        return challengeService.getChallengeList();
    }

    @GetMapping("/selected")
    public Map<String, List<ChallengeProgressGetRes>> getSelectedList(@ModelAttribute ChallengeProgressGetReq req) {
        Map<String, List<ChallengeProgressGetRes>> result = challengeService.getSelectedList(req);
        log.info("result={}", result);
        return result;
    }

    @GetMapping("/addlist")
    public List<ChallengeDefinitionGetRes> getChallenge(@ModelAttribute ChallengeProgressGetReq req) {
        return challengeService.getChallenge(req);
    }

    @GetMapping("/addcompetitionlist")
    public Map<String, List<ChallengeDefinitionGetRes>> getMapChallenge(@ModelAttribute ChallengeProgressGetReq req) {
        return challengeService.getMapChallenge(req);
    }

    @GetMapping("/detail/{cdId}")
    public ChallengeDetailGetRes getDetail(@PathVariable Long cdId, @RequestBody ChallengeProgressGetReq req) {
        return challengeService.getDetail(cdId, req);
    }
}
