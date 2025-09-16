package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

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

//    @Value("${constants.file.challenge-pic}")
//    private String imgPath;
//
//    public void setImage(String image) {
//        this.image = imgPath + image;
//    }
}
