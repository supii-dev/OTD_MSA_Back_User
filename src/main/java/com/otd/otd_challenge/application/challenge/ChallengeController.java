package com.otd.otd_challenge.application.challenge;

import com.otd.configuration.model.ResultResponse;
import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_challenge.application.challenge.model.*;
import com.otd.otd_challenge.application.challenge.model.detail.ChallengeDetailDayGetRes;
import com.otd.otd_challenge.application.challenge.model.detail.ChallengeDetailPerGetRes;
import com.otd.otd_challenge.application.challenge.model.detail.ChallengeProgressGetReq;
import com.otd.otd_challenge.application.challenge.model.detail.ChallengeSuccessPutReq;
import com.otd.otd_challenge.application.challenge.model.home.ChallengeHomeGetRes;
import com.otd.otd_challenge.application.challenge.model.home.ChallengeRecordMissionPostReq;
import com.otd.otd_challenge.application.challenge.model.settlement.ChallengeSettlementDto;
import com.otd.otd_challenge.application.challenge.model.settlement.ChallengeSettlementGetReq;
import com.otd.otd_challenge.application.challenge.model.settlement.ChallengeSettlementGetRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/OTD/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final ChallengeSchedulerService challengeSchedulerService;



    @GetMapping("/list")
    public Map<String, Object> getChallengeList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("유저 아이디: {}", userPrincipal.getSignedUserId());
        return challengeService.getChallengeList();
    }

    @GetMapping("/selected")
    public ChallengeHomeGetRes getSelectedList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                               @ModelAttribute ChallengeProgressGetReq req) {
        return challengeService.getSelectedList(userPrincipal.getSignedUserId(), req);
    }

    @GetMapping("/addlist")
    public List<ChallengeDefinitionGetRes> getChallengeList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                            @ModelAttribute ChallengeProgressGetReq req) {
        return challengeService.getChallengeList(userPrincipal.getSignedUserId(), req);
    }

    @GetMapping("/addcompetitionlist")
    public Map<String, List<ChallengeDefinitionGetRes>> getCompetitionList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                        @ModelAttribute ChallengeProgressGetReq req) {
        return challengeService.getCompetitionList(userPrincipal.getSignedUserId(), req);
    }

    @GetMapping("/detail/per/{cdId}")
    public ChallengeDetailPerGetRes getDetailPer(@PathVariable Long cdId, @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @ModelAttribute ChallengeProgressGetReq req) {
        return challengeService.getDetailPer(cdId, userPrincipal.getSignedUserId(), req);
    }

    @GetMapping("/detail/day/{cdId}")
    public ChallengeDetailDayGetRes getDetailDay(@PathVariable Long cdId, @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @ModelAttribute ChallengeProgressGetReq req) {
        return challengeService.getDetailDay(cdId, userPrincipal.getSignedUserId(), req);
    }

    @PutMapping("/success")
    public ResultResponse<?> putSuccess(@RequestBody ChallengeSuccessPutReq req) {
        return challengeService.updateIsSuccess(req.getCpId());
    }

    @PostMapping("/record/mission")
    public ResultResponse<?> postMissionRecord(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                               @RequestBody ChallengeRecordMissionPostReq req){
        return challengeService.saveMissionRecord(userPrincipal.getSignedUserId(), req);
    }
    @PostMapping("/add")
    public ResultResponse<?> postChallenge(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @RequestBody ChallengePostReq req){
        return challengeService.saveChallenge(userPrincipal.getSignedUserId(), req);
    }


    @PostMapping("/settlement")
    public ResultResponse<?> weeklySettlement(@RequestBody ChallengeSettlementDto dto) {
        return challengeService.setSettlement(dto);
    }

    @GetMapping("/settlement/log")
    public List<ChallengeSettlementGetRes> getSettlementLog(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                             @ModelAttribute ChallengeSettlementGetReq req){
        System.out.println("req" + req);
        return challengeSchedulerService.getSettlementLog(userPrincipal.getSignedUserId(), req);
    }
}