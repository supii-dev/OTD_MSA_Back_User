package com.otd.otd_challenge.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class ChallengeIds implements Serializable {
    private Long userId;
    private Long cdId;
}
