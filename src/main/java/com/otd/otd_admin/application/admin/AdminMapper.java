package com.otd.otd_admin.application.admin;

import com.otd.otd_admin.application.admin.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    List<AgeCountRes> groupByAge();
    List<TierCountRes> countByTier();
    List<ChallengeSuccessRateCountRes> countByChallengeType();
    List<SignInCountRes> countBySignIn();
}
