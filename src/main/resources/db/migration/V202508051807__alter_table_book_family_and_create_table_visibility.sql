CREATE TABLE IF NOT EXISTS tb_visibility(
    id INT PRIMARY KEY,
    value VARCHAR(100) NOT NULL UNIQUE
)engine=InnoDB default charset=utf8mb4;

INSERT IGNORE INTO tb_visibility
    VALUES (1, 'Público'),(2, 'Privado'),(3, 'Acesso com Link');

ALTER TABLE tb_book_family
    ADD COLUMN is_official BOOLEAN DEFAULT FALSE NOT NULL,
    ADD COLUMN cover_url VARCHAR(255),
    ADD COLUMN description TEXT,
    ADD COLUMN visibility_id INT NOT NULL DEFAULT 1,
    ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN share_token BINARY(16) UNIQUE,
    ADD COLUMN share_token_expires_at DATETIME,
    ADD CONSTRAINT fk_visibility FOREIGN KEY (visibility_id) REFERENCES tb_visibility(id),
    ADD INDEX (id, share_token);