package com.otd.otd_challenge.entity;

import com.otd.configuration.enumcode.model.EnumChallengeRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ChallengeDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cdId;

    @Column(nullable = false, length = 10)
    private String cdName;

    @Column(nullable = false)
    private int cdReward;

    @Column(nullable = false, length = 12)
    private String cdType;

    @Column(nullable = false)
    private String cdImage;

    @Column(nullable = false)
    private Long cdGoal;

    @Column(nullable = false, length = 10)
    private String cdUnit;

    @Column(nullable = false, length = 4)
    private int xp;

    @Convert(converter = EnumChallengeRole.CodeConverter.class)
    @Column(nullable = false, length = 10)
    private EnumChallengeRole tier;

    @Column(length = 30, columnDefinition = "VARCHAR(30) DEFAULT'-'")
    private String note;
}
