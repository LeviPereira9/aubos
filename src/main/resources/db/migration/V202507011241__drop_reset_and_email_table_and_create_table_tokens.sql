DROP TABLE IF EXISTS tb_reset_password_token;
DROP TABLE IF EXISTS tb_verify_email_token;

CREATE TABLE IF NOT EXISTS tb_token_type(
   id bigint primary key,
   type varchar(16)
) engine=InnoDB default charset=utf8mb4;

INSERT IGNORE INTO tb_token_type (id, type)
VALUES (1, 'PASSWORD_RESET'),
       (2, 'EMAIL_VERIFICATION');

CREATE TABLE  IF NOT EXISTS tb_tokens(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    user_id BINARY(16) NOT NULL,
    token VARCHAR(6) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    used BOOLEAN DEFAULT FALSE,
    revoked BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP,
    type bigint not null,

    CONSTRAINT fk_user_id
    FOREIGN KEY (user_id) REFERENCES tb_users(id),

    CONSTRAINT fk_token_type
    FOREIGN KEY (type) REFERENCES tb_token_type(id),

    UNIQUE u_user_token_type(user_id, token, type),
    INDEX idx_token_type_status (token, type, used, revoked),
    INDEX idx_token_status (token, used, revoked),
    INDEX idx_expiry_status (expires_at, used, revoked)
)engine=InnoDB default charset=utf8mb4;