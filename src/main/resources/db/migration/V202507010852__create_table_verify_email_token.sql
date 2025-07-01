CREATE TABLE  tb_verify_email_token(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    user_id BINARY(16) NOT NULL,
    token VARCHAR(6) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    used BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_user_id_email
    FOREIGN KEY (user_id) REFERENCES tb_users(id),
    INDEX dx_token(token)
)engine=InnoDB default charset=utf8mb4;