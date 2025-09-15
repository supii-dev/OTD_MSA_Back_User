package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChallengeDefinitionGetRes {
    private int id;
    private int goal;
    private String image;
    private String name;
    private String type;
    private int reward;
}
