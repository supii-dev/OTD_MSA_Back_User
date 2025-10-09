package com.otd.configuration.feignclient;

import com.otd.configuration.model.ResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "life", url = "${constants.feign-client.life.url}")
public interface LifeFeignClient {
    @PostMapping("/challenge/names")
    void sendActiveChallengeNames(@RequestBody List<String> challengeNames);

    @DeleteMapping("/{userId}")
    ResultResponse<?> deleteUserData(@PathVariable("userId") Long userId);
}
