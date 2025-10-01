package com.otd.configuration.security;

import com.otd.configuration.enumcode.AbstractEnumCodeConverter;
import com.otd.configuration.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SignInProviderType implements EnumMapperType {
      LOCAL("01", "LOCAL")
    , KAKAO("02", "KAKAO")
    , NAVER("03", "NAVER")
    , GOOGLE("04", "GOOGLE");

    private final String code;
    private final String value;

    @Converter(autoApply = true)
    public static class CodeConverter extends AbstractEnumCodeConverter<SignInProviderType> {
        public CodeConverter() {
            super(SignInProviderType.class, false);
        }
    }
}
