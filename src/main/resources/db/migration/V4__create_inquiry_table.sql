CREATE TABLE IF NOT EXISTS inquiry (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '문의 ID',
                                       user_id BIGINT NOT NULL COMMENT '문의한 사용자 ID',
                                       subject VARCHAR(255) NOT NULL COMMENT '문의 제목',
    sender_name VARCHAR(255) NOT NULL COMMENT '발신자 이름',
    sender_email VARCHAR(255) NOT NULL COMMENT '발신자 이메일',
    content TEXT COMMENT '문의 내용',
    created_at DATETIME(6) NOT NULL COMMENT '생성일시',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' COMMENT '처리 상태',

    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at DESC),

    CONSTRAINT fk_inquiry_user
    FOREIGN KEY (user_id) REFERENCES user(user_id)
    ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;