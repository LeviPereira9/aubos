CREATE TABLE tb_api_key_statuses(
    id TINYINT UNSIGNED PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
) engine=InnoDB default charset=utf8mb4;

INSERT INTO tb_api_key_statuses (id, name)
VALUES (1, 'ACTIVE'),
       (2, 'INACTIVE'),
       (3,'REVOKED'),
       (4,'EXPIRED');

CREATE TABLE tb_api_keys(
    id BINARY(16) NOT NULL PRIMARY KEY,
    public_id VARCHAR(64) NOT NULL UNIQUE,
    hashed_secret CHAR(64) NOT NULL,
    previous_hashed_secret CHAR(64),
    user_id BINARY(16) NOT NULL,

    label VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at DATETIME,
    expires_at DATETIME,

    rate_limit INT DEFAULT 1000,
    request_count INT DEFAULT 0,
    reset_at DATETIME,
    rotated_at DATETIME,

    status TINYINT UNSIGNED DEFAULT 1,
    soft_delete BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_api_key_user
        FOREIGN KEY (user_id)
        REFERENCES tb_users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_api_key_status
        FOREIGN KEY (status)
        REFERENCES tb_api_key_statuses(id)
)engine=InnoDB default charset=utf8mb4