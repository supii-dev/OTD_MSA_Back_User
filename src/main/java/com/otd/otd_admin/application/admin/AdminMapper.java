package com.otd.otd_admin.application.admin;

import com.otd.otd_admin.application.admin.model.AdminUserGetRes;
import com.otd.otd_admin.application.admin.model.AgeCountRes;
import com.otd.otd_admin.application.admin.model.ChallengeSuccessRateCountRes;
import com.otd.otd_admin.application.admin.model.TierCountRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    List<AgeCountRes> groupByAge();
    List<TierCountRes> countByTier();
    List<ChallengeSuccessRateCountRes> countByChallengeType();
}
