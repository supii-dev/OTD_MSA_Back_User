package com.otd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients
@ConfigurationPropertiesScan
@SpringBootApplication
@ComponentScan(basePackages = {"com.otd","com.otd.configuration"})
public class OtdMsaBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtdMsaBackApplication.class, args);
    }
}
