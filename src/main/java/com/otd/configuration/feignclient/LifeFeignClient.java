package com.otd.configuration.feignclient;

import com.otd.configuration.model.ResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "life", url = "${constants.feign-client.life.url}")
public interface LifeFeignClient {
    @PostMapping("/exercise/challenge/names")
    void sendActiveChallengeNames(@RequestBody List<String> challengeNames);

    @DeleteMapping("/admin2/{userId}")
    ResultResponse<?> deleteUserData(@PathVariable("userId") Long userId);
}
