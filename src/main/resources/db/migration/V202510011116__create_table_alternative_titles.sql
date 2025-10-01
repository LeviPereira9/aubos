CREATE TABLE IF NOT EXISTS tb_book_alternative_title(
    id BINARY(16) PRIMARY KEY,
    book_id BINARY(16) NOT NULL,
    alternative_title VARCHAR(255) NOT NULL,

    CONSTRAINT fk_book_id FOREIGN KEY (book_id) REFERENCES tb_book(id),
    CONSTRAINT uk_book_alternative_title UNIQUE (book_id, alternative_title)
)engine=InnoDB default charset=utf8mb4;