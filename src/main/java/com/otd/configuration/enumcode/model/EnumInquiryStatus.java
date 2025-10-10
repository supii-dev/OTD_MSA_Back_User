package com.otd.configuration.enumcode.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnumInquiryStatus implements EnumMapperType {
    PENDING("01", "대기중", "문의가 접수되었습니다"),
    RESOLVED("02", "완료", "문의 처리가 완료되었습니다");

    private final String code;
    private final String title;
    private final String description;

    @JsonCreator
    public static EnumInquiryStatus from(String raw) {
        if (raw == null) return null;
        final String key = raw.trim();

        for (EnumInquiryStatus status : values()) {
            // 1) enum 상수명 매칭 (PENDING, RESOLVED)
            if (status.name().equalsIgnoreCase(key)) return status;

            // 2) code 매칭 ("01", "02")
            if (status.code.equalsIgnoreCase(key)) return status;

            // 3) title 매칭 ("대기중", "완료")
            if (status.title.equalsIgnoreCase(key)) return status;
        }

        throw new IllegalArgumentException("Unknown EnumInquiryStatus: " + raw);
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getValue() {
        return title;
    }

    @Converter(autoApply = false)
    public static class CodeConverter extends AbstractEnumCodeConverter<EnumInquiryStatus> {
        public CodeConverter() {
            super(EnumInquiryStatus.class, false);
        }
    }
}