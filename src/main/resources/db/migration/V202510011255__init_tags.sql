CREATE TABLE IF NOT EXISTS tb_tag(
    id int PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(155) UNIQUE NOT NULL
)engine=InnoDB default charset=utf8mb4;

INSERT IGNORE INTO tb_tag (name) VALUES
     ('Ficção'),
     ('Não-Ficção'),
     ('Romance'),
     ('Fantasia'),
     ('Ficção Científica'),
     ('Suspense'),
     ('Terror'),
     ('Biografia'),
     ('História'),
     ('Autoajuda'),
     ('Poesia'),
     ('Drama'),
     ('Aventura'),
     ('Infantil');

CREATE TABLE IF NOT EXISTS tb_book_tag(
    id BINARY(16) PRIMARY KEY,
    book_id BINARY(16) NOT NULL,
    tag_id int NOT NULL,

    CONSTRAINT fk_book_tag_book_id FOREIGN KEY (book_id) REFERENCES tb_book(id),
    CONSTRAINT fk_book_tag_tag_id FOREIGN KEY (tag_id) REFERENCES tb_tag(id),
    CONSTRAINT uk_book_tag_id UNIQUE (book_id, tag_id)
) engine=InnoDB default charset=utf8mb4;