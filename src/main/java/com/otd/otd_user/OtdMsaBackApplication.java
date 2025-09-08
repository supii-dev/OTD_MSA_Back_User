package com.otd.otd_user;

import com.otd.otd_user.configuration.constants.ConstJwt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@ConfigurationPropertiesScan
@SpringBootApplication
public class OtdMsaBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtdMsaBackApplication.class, args);
    }

}
