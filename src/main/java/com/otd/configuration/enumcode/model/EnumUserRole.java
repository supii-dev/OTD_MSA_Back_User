package com.otd.configuration.enumcode.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otd.configuration.enumcode.AbstractEnumCodeConverter;
import com.otd.configuration.enumcode.EnumMapperType;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum EnumUserRole implements EnumMapperType {
    USER_1("01", "USER, user"),
    USER_2("02", "SOCIAL, social"),
    MANAGER("03", "MANAGER, manager"),
    ADMIN("04", "ADMIN, admin");

    private final String code;
    private final String value; // 예: "USER, user" (콤마로 구분된 허용 토큰)

    /**
     * JSON 직렬화 시 어떤 문자열을 내보낼지 결정.
     * 여기서는 value의 첫 토큰(예: "USER")만 반환하도록 했습니다.
     * 필요에 따라 name() 등으로 바꿔도 됩니다.
     */
    @JsonValue
    public String jsonValue() {
        return firstToken(value);
    }

    /**
     * JSON 역직렬화: "user", "USER", "USER_1", "01" 등 다양한 입력을 허용.
     */
    @JsonCreator
    public static EnumUserRole from(String raw) {
        if (raw == null) return null;
        final String key = raw.trim();

        for (EnumUserRole r : values()) {
            // 1) enum 상수명 매칭 (USER_1, USER_2, MANAGER, ADMIN)
            if (r.name().equalsIgnoreCase(key)) return r;

            // 2) code 매칭 ("01", "02", "03", "04")
            if (r.code.equalsIgnoreCase(key)) return r;

            // 3) value에 들어있는 토큰 매칭 ("USER", "user", "SOCIAL", "social", ...)
            for (String token : r.value.split(",")) {
                if (token.trim().equalsIgnoreCase(key)) return r;
            }
        }

        throw new IllegalArgumentException("Unknown EnumUserRole: " + raw);
    }

    private static String firstToken(String s) {
        String[] parts = s.split(",");
        return parts.length > 0 ? parts[0].trim() : s;
    }

    @Converter(autoApply = false)
    public static class CodeConverter extends AbstractEnumCodeConverter<EnumUserRole> {
        public CodeConverter() { super(EnumUserRole.class, false); }
    }
}