package com.otd.otd_user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Setter
public class UserRole {
    @EmbeddedId
    private UserRoleIds userRoleIds;

    //관계 설정
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}
