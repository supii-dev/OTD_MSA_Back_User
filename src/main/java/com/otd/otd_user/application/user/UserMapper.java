package com.otd.otd_user.application.user;

import com.otd.otd_user.application.user.model.UserLoginRes;
import com.otd.otd_user.application.user.model.UserProfileGetDto;
import com.otd.otd_user.application.user.model.UserProfileGetRes;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    UserProfileGetRes findProfileByUserId(long signedUserId);
    int countByUid(String uid);
    int countByNickname(String nickname);
    int countByCi(String ci);
    int countByDi(String di);
    UserLoginRes findRoleByUserId(long signedUserId);

}
