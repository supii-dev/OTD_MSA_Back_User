package com.otd.otd_admin.application.admin;

import com.otd.otd_admin.application.admin.model.*;
import com.otd.otd_challenge.entity.ChallengeDefinition;
import com.otd.otd_user.entity.Inquiry;
import com.otd.otd_user.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    List<AgeCountRes> groupByAge();
    List<TierCountRes> countByTier();
    List<ChallengeSuccessRateCountRes> countByChallengeType();
    List<SignInCountRes> countBySignIn();
    List<ChallengeDefinition> findTop3ByFailRate();
    List<ChallengeDefinition> findTop5ByParticipationRate();
    Double findAverageSuccessRate();
    List<User> findTop5ByPoint();
    List<Inquiry> findRecent5Inquiry();
    Double getAvgInquiryRepliedTime();
    Double getInquiryRepliedRate();
}
