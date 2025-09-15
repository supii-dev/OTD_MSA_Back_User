package com.otd.otd_user.application.user.model;

import com.otd.configuration.enumcode.model.EnumUserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class UserJoinReq {
    @NotNull(message = "아이디는 필수로 입력하셔야 합니다.")
    @Pattern(regexp = "^[A-Za-z0-9_]{4,50}$", message = "아이디는 영어, 숫자, 언더바로만 구성되어야 하며 4~50자까지 작성할 수 있습니다.")
    private String uid;

    @NotNull(message = "비밀번호는 필수로 입력하셔야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{10,}$", message = "비밀번호는 영문자, 숫자, 특수기호로 구성되며 10자 이상이어야 합니다.")
    private String upw;

    @Pattern(regexp = "^[가-힣]{2,10}$", message = "닉네임은 한글로 2~10자까지 가능합니다.")
    private String nickName;

    @NotNull(message = "이메일은 필수로 입력하셔야 합니다.")
    private String email;
    private String name;
    private String phone;
    private LocalDate birthDate;
    private String gender;
    private String pic;
    private String ci; // 연계정보
    private String di; // 중복가입확인정보
    private String authToken;
    private List<EnumUserRole> roles;
}
