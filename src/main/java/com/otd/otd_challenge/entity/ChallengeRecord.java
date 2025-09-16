package com.otd.otd_challenge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ChallengeRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long crId;

  @ManyToOne
  @JoinColumn(name = "cp_id", nullable = false)
  private ChallengeProgress challengeProgress;

  @Column(nullable = false)
  private LocalDate recDate;

  @Column(nullable = false)
  private double recValue;
}
