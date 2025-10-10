CREATE TABLE inquiry (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         subject VARCHAR(255) NOT NULL,
                         sender_name VARCHAR(255) NOT NULL,
                         sender_email VARCHAR(255) NOT NULL,
                         content TEXT,
                         status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                         reply TEXT,
                         admin_id BIGINT NULL,
                         reply_at DATETIME NULL,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_inquiry_user FOREIGN KEY (user_id) REFERENCES user(user_id),
                         CONSTRAINT fk_inquiry_admin FOREIGN KEY (admin_id) REFERENCES user(user_id)
);
