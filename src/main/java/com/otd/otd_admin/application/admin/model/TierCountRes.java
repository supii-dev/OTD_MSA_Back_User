package com.otd.otd_admin.application.admin.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class TierCountRes {
    String tier;
    Long count;
}
