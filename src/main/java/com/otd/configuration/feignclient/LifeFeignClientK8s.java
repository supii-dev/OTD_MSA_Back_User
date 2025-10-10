package com.otd.configuration.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@FeignClient(name = "${constants.feign-client.life.name}")
public interface LifeFeignClientK8s extends LifeFeignClient {

}
