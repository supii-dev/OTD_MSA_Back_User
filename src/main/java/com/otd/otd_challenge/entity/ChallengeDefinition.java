package com.otd.otd_challenge.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Setter
public class ChallengeDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cdId;

    @Column(nullable = false, length = 10)
    private String cdName;

    @Column(nullable = false)
    private Long cdReward;

    @Column(nullable = false, length = 12)
    private String cdType;

    @Column(nullable = false)
    private String cdImage;

    @Column(nullable = false)
    private Long cdGoal;

    @Column(nullable = false, length = 10)
    private String cdUnit;

    @Column(nullable = false, length = 5)
    private int exp;
}
