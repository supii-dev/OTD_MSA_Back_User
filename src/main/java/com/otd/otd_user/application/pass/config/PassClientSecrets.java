package com.otd.otd_user.application.pass.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "constants.pass.client-secrets")
@Data
@Component
public class PassClientSecrets {
    private String skt;
    private String kt;
    private String lgu;
    private String payco;
    private String samsung;
}