package com.otd.otd_challenge.application.challenge;

import com.otd.configuration.model.ResultResponse;
import com.otd.otd_challenge.application.challenge.model.*;
import com.otd.otd_challenge.entity.ChallengeDefinition;
import com.otd.otd_user.application.user.UserRepository;
import com.otd.otd_user.entity.User;
import jakarta.transaction.Transactional;
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
    private final ChallengeProgressRepository challengeProgressRepository;
    private final UserRepository userRepository;
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

        addImgPath(res);

        for (ChallengeDefinitionGetRes challengeDefinitionGetRes : res) {

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

    public ChallengeHomeGetRes getSelectedList(ChallengeProgressGetReq req) {
        List<ChallengeProgressGetRes> res = challengeMapper.findAllProgressFromUserId(req);
        List<ChallengeDefinition> daily = challengeDefinitionRepository.findByCdType("daily");
        User userInfo = userRepository.findByUserId(req.getUserId());
        List<ChallengeProgressGetRes> personal = new ArrayList<>();
        List<ChallengeProgressGetRes> weekly = new ArrayList<>();
        List<ChallengeProgressGetRes> competition = new ArrayList<>();

        addImgPath(res);
        for (ChallengeProgressGetRes challengeProgressGetRes : res) {

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

        ChallengeHomeGetRes result = ChallengeHomeGetRes.builder()
                .user(userInfo)
                .challengeDefinition(daily)
                .weeklyChallenge(weekly)
                .competitionChallenge(competition)
                .personalChallenge(personal)
                .build();

        log.info(" dto: {}", dto);
        return result;
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

    private void formatRankingRecords(List<ChallengeRankGetRes> rankingList, String unit) {
        for (ChallengeRankGetRes ranking : rankingList) {
            DecimalFormat df = (ranking.getTotalRecord() % 1 == 0) ? new DecimalFormat("0") : new DecimalFormat("0.0");
            ranking.setFormattedTotalRecord(df.format(ranking.getTotalRecord()) + unit);
        }
    }

    public ChallengeDetailGetRes getDetail(Long cdId, ChallengeProgressGetReq req) {
        req.setCdId(cdId);
        // 상세정보
        ChallengeDetailGetRes res = challengeMapper.findProgressByUserIdAndCdId(req);
        // top5
        List<ChallengeRankGetRes> top5Ranking = challengeMapper.findTop5Ranking(req);
        // 내 주위 랭킹
        List<ChallengeRankGetRes> aroundRanking = challengeMapper.findAroundMyRank(req);



        if (res.getGoal() > res.getTotalRecord()) {
            double percentage = ((res.getTotalRecord() / res.getGoal()) * 100 );
            res.setPercent(Math.round(percentage * 10 ) / 10.0);
        } else {
            res.setPercent(100.0);
        }
        DecimalFormat df = (res.getTotalRecord() % 1 == 0) ? new DecimalFormat("0") : new DecimalFormat("0.0");
        res.setFormattedTotalRecord(df.format(res.getTotalRecord()) + res.getUnit());
//        res.setFormattedGoal(res.getGoal() + res.getUnit());

        formatRankingRecords(top5Ranking, res.getUnit());
        formatRankingRecords(aroundRanking, res.getUnit());

        res.setTopRanking(top5Ranking);
        res.setAroundRanking(aroundRanking);
        return res;
    }

    @Transactional
    public ResultResponse<?> updateIsSuccess(Long cpId){
        int result = challengeProgressRepository.updateIsSuccess(cpId);
        return new ResultResponse<>("success", result);
    }
}
