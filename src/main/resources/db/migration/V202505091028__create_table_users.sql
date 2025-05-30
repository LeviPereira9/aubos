CREATE TABLE tb_account_status (
    id TINYINT UNSIGNED PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
) engine=InnoDB default charset=utf8mb4;

INSERT INTO tb_account_status(id, name)
VALUES
    (1, 'ACTIVE'),
    (2, 'BANNED'),
    (3, 'SUSPENDED');

CREATE TABLE tb_users(
    id BINARY(16) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    profile_pic VARCHAR(255),
    bio TEXT,
    location VARCHAR(100),
    date_of_birth DATE,
    join_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME,
    status TINYINT UNSIGNED DEFAULT 1,
    is_verified BOOLEAN DEFAULT FALSE,
    is_official BOOLEAN DEFAULT FALSE,
    soft_deleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_status_account_status
        FOREIGN KEY (status)
        REFERENCES tb_account_status(id)
)engine=InnoDB default charset=utf8mb4;