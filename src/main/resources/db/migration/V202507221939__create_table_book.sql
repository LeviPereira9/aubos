/*=====Tabela Principal=======*/
CREATE TABLE IF NOT EXISTS tb_book(
    ID BINARY(16) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    subtitle VARCHAR(100),
    synopsis TEXT NOT NULL,
    published_at DATETIME NOT NULL,
    finished_at DATETIME,
    language_id INT NOT NULL,
    type_id INT NOT NULL,
    status_id INT NOT NULL,
    restriction_id INT NOT NULL,
    license_id INT NOT NULL DEFAULT 0,

    created_by BINARY(16) NOT NULL,
    last_update DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BINARY(16),

    CONSTRAINT fk_book_language
        FOREIGN KEY (language_id) REFERENCES tb_language(id),
    CONSTRAINT fk_book_type
        FOREIGN KEY (type_id) REFERENCES tb_book_type(id),
    CONSTRAINT fk_book_status
        FOREIGN KEY (status_id) REFERENCES tb_book_status(id),
    CONSTRAINT fk_book_restriction
        FOREIGN KEY (restriction_id) REFERENCES tb_book_restriction(id),
    CONSTRAINT fk_book_created_by
        FOREIGN KEY (created_by) REFERENCES tb_users(id),
    CONSTRAINT fk_book_updated_by
        FOREIGN KEY (updated_by) REFERENCES tb_users(id),
    CONSTRAINT fk_book_license
        FOREIGN KEY (license_id) REFERENCES tb_book_license(id),
    INDEX (title, subtitle)
)engine=InnoDB default charset=utf8mb4;