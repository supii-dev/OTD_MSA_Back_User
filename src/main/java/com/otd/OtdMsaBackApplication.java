package com.otd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@ConfigurationPropertiesScan
@SpringBootApplication
@EnableFeignClients(basePackages = "com.otd.configuration.feignclient")
@ComponentScan(basePackages = {"com.otd","com.otd.configuration", "com.otd.otd_user"})
public class OtdMsaBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtdMsaBackApplication.class, args);
    }
}
