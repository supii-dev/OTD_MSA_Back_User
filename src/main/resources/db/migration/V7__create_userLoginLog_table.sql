CREATE TABLE user_login_log (
    user_id BIGINT NOT NULL,
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES user(user_id)
)