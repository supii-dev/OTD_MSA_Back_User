package com.otd.otd_challenge.application.challenge;

import com.otd.configuration.model.ResultResponse;
import com.otd.otd_challenge.application.challenge.model.*;
import com.otd.otd_challenge.application.challenge.model.detail.ChallengeDetailDayGetRes;
import com.otd.otd_challenge.application.challenge.model.detail.ChallengeDetailPerGetRes;
import com.otd.otd_challenge.application.challenge.model.detail.ChallengeProgressGetReq;
import com.otd.otd_challenge.application.challenge.model.detail.ChallengeSuccessPutReq;
import com.otd.otd_challenge.application.challenge.model.home.ChallengeHomeGetRes;
import com.otd.otd_challenge.application.challenge.model.home.ChallengeRecordMissionPostReq;
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
    public ChallengeHomeGetRes getSelectedList(@ModelAttribute ChallengeProgressGetReq req) {
        return challengeService.getSelectedList(req);
    }

    @GetMapping("/addlist")
    public List<ChallengeDefinitionGetRes> getChallenge(@ModelAttribute ChallengeProgressGetReq req) {
        return challengeService.getChallenge(req);
    }

    @GetMapping("/addcompetitionlist")
    public Map<String, List<ChallengeDefinitionGetRes>> getMapChallenge(@ModelAttribute ChallengeProgressGetReq req) {
        return challengeService.getMapChallenge(req);
    }

    @GetMapping("/detail/per/{cdId}")
    public ChallengeDetailPerGetRes getDetail(@PathVariable Long cdId, @ModelAttribute ChallengeProgressGetReq req) {
        return challengeService.getDetail(cdId, req);
    }

    @GetMapping("/detail/day/{cdId}")
    public ChallengeDetailDayGetRes getDetailDay(@PathVariable Long cdId, @ModelAttribute ChallengeProgressGetReq req) {
        return challengeService.getDetailDay(cdId, req);
    }

    @PutMapping("/success")
    public ResultResponse<?> putSuccess(@RequestBody ChallengeSuccessPutReq req) {
        return challengeService.updateIsSuccess(req.getCpId());
    }

    @PostMapping("/record/mission")
    public ResultResponse<?> postMissionRecord(@RequestBody ChallengeRecordMissionPostReq req){
        return challengeService.saveMissionRecord(req);
    }
    @PostMapping("/add")
    public ResultResponse<?> postChallenge(@RequestBody ChallengePostReq req){
        return challengeService.saveChallenge(req);
    }
}
