package com.otd.configuration.enumcode.model;

import com.otd.configuration.enumcode.AbstractEnumCodeConverter;
import com.otd.configuration.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnumChallengeRole implements EnumMapperType {
    TBD("00", "미정"), // To Be Determined
    BRONZE("01", "브론즈"),
    SILVER("02", "실버"),
    GOLD("03", "골드"),
    DIAMOND("04", "다이아");

    private final String code;
    private final String value;

    @Converter(autoApply = false)
    public static class CodeConverter extends AbstractEnumCodeConverter<EnumChallengeRole> {
        public CodeConverter() { super(EnumChallengeRole.class, false); }
    }

    public static EnumChallengeRole fromCode(int surveyResult) {
        return switch (surveyResult) {
            case 0, 1, 2, 3 -> EnumChallengeRole.BRONZE;
            case 4, 5, 6 -> EnumChallengeRole.SILVER;
            case 7, 8, 9 -> EnumChallengeRole.GOLD;
            case 10, 11, 12 -> EnumChallengeRole.DIAMOND;
            default -> EnumChallengeRole.TBD;
        };
    }
}
