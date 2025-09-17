package com.otd.configuration.enumcode.model;

import com.otd.configuration.enumcode.AbstractEnumCodeConverter;
import com.otd.configuration.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnumUserRole implements EnumMapperType {
    USER("01", "유저")
    , MANAGER("02", "매니저")
    , ADMIN("03", "관리자")

    ;

    private final String code;
    private final String value;

    @Converter(autoApply = false)
    public static class CodeConverter extends AbstractEnumCodeConverter<EnumUserRole> {
        public CodeConverter() { super(EnumUserRole.class, false); }
    }
}
