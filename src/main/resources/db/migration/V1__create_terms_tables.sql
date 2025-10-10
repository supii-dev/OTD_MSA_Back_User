CREATE TABLE IF NOT EXISTS terms (
                                     terms_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     type VARCHAR(50) NOT NULL,
    version VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    is_required BOOLEAN NOT NULL DEFAULT TRUE,
    effective_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 사용자 동의 테이블 생성
CREATE TABLE IF NOT EXISTS user_agreement (
                                              agreement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              user_id BIGINT NOT NULL,
                                              terms_id BIGINT NOT NULL,
                                              agreed BOOLEAN NOT NULL,
                                              agreed_at DATETIME NOT NULL,
                                              ip_address VARCHAR(45),
    user_agent TEXT,
    created_at DATETIME NOT NULL,
    INDEX idx_user_terms (user_id, terms_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (terms_id) REFERENCES terms(terms_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;