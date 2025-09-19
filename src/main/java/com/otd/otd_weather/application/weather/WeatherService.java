package com.otd.otd_weather.application.weather;

import com.otd.configuration.constants.ConstKma;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    private final WeatherFeignClient weatherFeignClient;
    private final ConstKma constKma;
}
