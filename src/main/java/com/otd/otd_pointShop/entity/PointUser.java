package com.otd.otd_pointShop.entity;

import com.otd.otd_user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "point_user")
public class PointUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName ="user_id", nullable = false
                , foreignKey = @ForeignKey(name="fk_point_user")) // FK
    private User user;

    @Column(nullable = false)
    private Integer pointBalance = 0;
}
