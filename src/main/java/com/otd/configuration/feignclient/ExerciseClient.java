package com.otd.configuration.feignclient;

import com.otd.otd_challenge.application.challenge.model.ExerciseReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "challenge",
        url = "${constants.feign-client.exercise.url}")
public interface ExerciseClient {

    @GetMapping("/exercise/feign")
    int getAllExerciseRecordCount(@RequestParam("userId") Long userId,
                                  @RequestParam("recDate") LocalDate recDate) ;
}
