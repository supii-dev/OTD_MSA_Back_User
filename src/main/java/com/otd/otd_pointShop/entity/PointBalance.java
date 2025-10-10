package com.otd.otd_pointShop.entity;

import com.otd.otd_user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "point_balance")
public class PointBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer pointBalance = 0;
}
