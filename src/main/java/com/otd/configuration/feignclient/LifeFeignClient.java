package com.otd.configuration.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "life", url = "${constants.feign-client.life.url}")
public interface LifeFeignClient {
    @PostMapping("/challenge/names")
    void sendActiveChallengeNames(@RequestBody List<String> challengeNames);
}
