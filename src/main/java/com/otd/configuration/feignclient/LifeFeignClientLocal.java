package com.otd.configuration.feignclient;

import com.otd.configuration.model.ResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Profile("default")
@FeignClient(name = "${constants.feign-client.life.name}", url = "${constants.feign-client.life.url:}")
public interface LifeFeignClientLocal extends LifeFeignClient {

}
