package com.otd.configuration.enumcode.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnumInquiryStatus {
    PENDING("대기중", "문의가 접수되었습니다"),
    RESOLVED("완료", "문의 처리가 완료되었습니다");

    private final String title;
    private final String description;
}
