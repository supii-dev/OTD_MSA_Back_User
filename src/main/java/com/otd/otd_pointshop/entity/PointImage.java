package com.otd.otd_pointshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

// 포인트 아이템 이미지 엔티티
// Point 엔티티와 1:N 양방향 관계

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "point")
@Entity
@Table(name = "point_image")
public class PointImage {

    // 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    // 실제 이미지 접근 URL (ex: /upload/pointshop/abc123.png)
    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    // 이미지 파일 유형 (ex: jpg, png, webp 등)
    @Column(name = "image_type", length = 50)
    private String imageType;

    // 대체 텍스트 (접근성 / SEO 용)
    @Column(name = "alt_text", length = 200)
    private String altText;

    // 파일 원본 이름 (UUID 전 이름)
    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    // 이미지 고유 UUID 파일명 (서버 내부용)
    @Column(name = "uuid", length = 100)
    private String uuid;

    // 연결된 포인트 상품
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "point_id",
            referencedColumnName = "point_id",
            foreignKey = @ForeignKey(name = "fk_point_image_point")
    )
    @JsonIgnore
    private Point point;
}
