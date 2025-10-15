package com.otd.configuration.enumcode;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EnumMapperValue {
    private String code;
    private String value;

    public EnumMapperValue(EnumMapperType enumMapperType) {
        this.code = enumMapperType.getCode();
        this.value = enumMapperType.getValue();
    }
}
