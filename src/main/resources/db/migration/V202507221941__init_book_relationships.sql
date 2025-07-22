/*======= Tabelas Dependentes=========*/
/*Relação livro e língua disponível*/
CREATE TABLE IF NOT EXISTS tb_book_languages(
    id BINARY(16) PRIMARY KEY,
    book_id BINARY(16) NOT NULL,
    language_id INT NOT NULL,

    CONSTRAINT tb_booklanguages_book_id
        FOREIGN KEY (book_id) REFERENCES tb_book(id)
            ON DELETE CASCADE,
    CONSTRAINT tb_booklanguages_language
        FOREIGN KEY (language_id) REFERENCES tb_language(id),
    UNIQUE (book_id, language_id)
)engine=InnoDB default charset=utf8mb4;

/*Métricas de um livro*/
CREATE TABLE IF NOT EXISTS tb_book_metrics(
    ID BINARY(16) PRIMARY KEY,
    book_id BINARY(16) UNIQUE NOT NULL,
    qty_chapters INT UNSIGNED DEFAULT 0,
    qty_views INT UNSIGNED DEFAULT 0,
    qty_favorites INT UNSIGNED DEFAULT 0,
    last_update DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_bookmetrics_book_id
        FOREIGN KEY (book_id) REFERENCES tb_book(id)
            ON DELETE CASCADE
)engine=InnoDB default charset=utf8mb4;

/*Relação de Livro com Contribuidor*/
CREATE TABLE IF NOT EXISTS tb_book_contributor(
    id BINARY(16) PRIMARY KEY,
    book_id BINARY(16) NOT NULL,
    contributor_id BINARY(16) NOT NULL,
    contributor_role_id INT NOT NULL,

    CONSTRAINT fk_bookcontributor_book_id
        FOREIGN KEY (book_id) REFERENCES tb_book(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_bookcontributor_contributor_id
        FOREIGN KEY (contributor_id) REFERENCES tb_contributor(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_bookcontributor_contributor_role_id
      FOREIGN KEY (contributor_role_id) REFERENCES tb_contributor_roles(id),
    INDEX (book_id, contributor_id)
)engine=InnoDB default charset=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_book_family_membership(
    id BINARY(16) PRIMARY KEY,
    book_id BINARY(16),
    book_family_id BINARY(16),
    order_in_family INT NOT NULL,
    note VARCHAR(100),

    created_by BINARY(16),
    last_update DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BINARY(16),

    CONSTRAINT fk_bfm_created_by
        FOREIGN KEY (created_by) REFERENCES tb_users(id),
    CONSTRAINT fk_bfm_updated_by
        FOREIGN KEY (updated_by) REFERENCES tb_users(id),
    CONSTRAINT fk_bfm_book_family
        FOREIGN KEY (book_family_id) REFERENCES tb_book_family(id),
    CONSTRAINT fk_bfm_book_id
        FOREIGN KEY (book_id) REFERENCES tb_book(id)
            ON DELETE CASCADE,
    UNIQUE (book_family_id, order_in_family)
)engine=InnoDB default charset=utf8mb4;