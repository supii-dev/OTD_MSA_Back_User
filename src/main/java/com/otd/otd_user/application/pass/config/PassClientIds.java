package com.otd.otd_user.application.pass.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "constants.pass.client-ids")
@Data
@Component
public class PassClientIds {
    private String skt;
    private String kt;
    private String lgu;
}
