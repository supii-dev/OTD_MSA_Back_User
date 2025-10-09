
package com.otd.otd_user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otd.configuration.enumcode.model.EnumChallengeRole;
import com.otd.configuration.enumcode.model.EnumUserRole;
import com.otd.configuration.security.SignInProviderType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity //테이블을 만들고 DML때 사용
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class User extends UpdatedAt{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String uid;

    @Column(nullable = false, length = 100)
    @JsonIgnore
    private String upw;

    @Column(length = 30, name = "nick_name")
    private String nickName;

    @Column(length = 100)
    private String pic;

    @Column(length = 30)
    private String name;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(length = 1)
    private String gender;

    @Column(length = 100)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 2)
    private SignInProviderType providerType;

    @Column(columnDefinition = "int DEFAULT 0", nullable = false)
    private int point;

    @Column(columnDefinition = "int DEFAULT 0")
    private int xp;

    @Column(length = 300)
    @JsonIgnore
    private String refreshToken;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAgreement> agreements = new ArrayList<>();

    //cascade는 자식과 나랑 모든 연결 (내가 영속성되면 자식도 영속성되고, 내가 삭제되면 자식도 삭제 된다. 등등)
    //ohphanRemoval은 userRoles에서 자식을 하나 제거함. 그러면 DB에도 뺀 자식은 삭제처리가 된다.
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRole> userRoles = new ArrayList<>(1);

    public void addUserRoles(List<EnumUserRole> enumUserRole, EnumChallengeRole enumChallengeRole) {
        for(EnumUserRole e : enumUserRole) {
            UserRoleIds ids = new UserRoleIds(this.userId, e, enumChallengeRole);
            UserRole userRole = new UserRole(ids, this);

            this.userRoles.add(userRole);
        }
    }
    public EnumChallengeRole getChallengeRole() {
        return this.userRoles.stream()
                .map(userRole -> userRole.getUserRoleIds().getChallengeCode())
                .findFirst() // 유저가 가진 챌린지 Role 하나만 꺼냄
                .orElse(EnumChallengeRole.TBD);
    }
}
