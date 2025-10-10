package com.otd.otd_user.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
@Setter
public class UserLoginLogIds implements Serializable {

    private Long userId;

    private LocalDateTime loginDate;
}
