package com.otd.configuration.security.oauth;

import com.otd.configuration.enumcode.model.EnumUserRole;
import com.otd.configuration.model.JwtUser;
import lombok.Getter;

import java.util.List;

@Getter
public class OAuth2JwtUser extends JwtUser {
    private String nickName;
    private String pic;
    private String providerType;
    private String providerId;
    private String name;

    public OAuth2JwtUser(
            String name,
            String nickName,
            String pic,
            Long signedUserId,
            List<EnumUserRole> roles,
            String providerType,
            String providerId) {
        super(signedUserId, roles);
        this.name = name;
        this.nickName = nickName;
        this.pic = pic;
        this.providerType = providerType;
        this.providerId = providerId;
    }
}
