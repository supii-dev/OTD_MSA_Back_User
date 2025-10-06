package com.otd.otd_pointShop.entity;

import com.otd.otd_user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "point_recharge_history")
public class RechargeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rechargeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private LocalDateTime rechargeTime;
}