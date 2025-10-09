package com.otd.otd_user.entity;

import com.otd.configuration.enumcode.model.EnumInquiryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiry")
@Getter
@Setter
@NoArgsConstructor
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private String senderEmail;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "status")
    @Convert(converter = EnumInquiryStatus.CodeConverter.class)
    private EnumInquiryStatus status = EnumInquiryStatus.PENDING;
}