package com.otd.otd_user.application.user;

import com.otd.otd_user.application.user.model.UserProfileGetDto;
import com.otd.otd_user.application.user.model.UserProfileGetRes;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    UserProfileGetRes findProfileByUserId(UserProfileGetDto dto);
    int countByUid(String uid);
    int countByNickname(String nickname);

}
