/*=====Tabelas independentes=======*/
/* Família dos livros: Conjunto da história. */

CREATE TABLE IF NOT EXISTS tb_book_family_type(
    id INT PRIMARY KEY AUTO_INCREMENT,
    value VARCHAR(30)
)engine=InnoDB default charset=utf8mb4;

INSERT IGNORE INTO tb_book_family_type
VALUES
    (1, 'Série'),
    (2, 'Saga'),
    (3, 'Franquia'),
    (4, 'Trilogia'),
    (5, 'Tetralogia'),
    (6, 'Duologia'),
    (7, 'Universo expandido'),
    (8, 'Coletânea'),
    (9, 'Antalogia');

CREATE TABLE IF NOT EXISTS tb_book_family(
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(50),
    type_id INT,

    created_by BINARY(16),
    last_update DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BINARY(16),

    CONSTRAINT fk_bookfamily_created_by
        FOREIGN KEY (created_by) REFERENCES tb_users(id),
    CONSTRAINT fk_bookfamily_updated_by
        FOREIGN KEY (updated_by) REFERENCES tb_users(id),
    CONSTRAINT fk_bookfamily_type_id
        FOREIGN KEY (type_id) REFERENCES tb_book_family_type(ID)
)engine=InnoDB default charset=utf8mb4;

/*Contribuidores que fazem parte da produção de um livro*/
CREATE TABLE IF NOT EXISTS tb_contributor_roles(
    id INT PRIMARY KEY,
    name VARCHAR(15)
)engine=InnoDB default charset=utf8mb4;

INSERT IGNORE INTO tb_contributor_roles
VALUES (1, 'autor'),
       (2, 'ilustrador'),
       (3, 'editor'),
       (4, 'publicadora');

CREATE TABLE IF NOT EXISTS tb_contributor(
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    started_at DATETIME,
    ended_at DATETIME,

    created_by BINARY(16),
    last_update DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BINARY(16),

    CONSTRAINT fk_contributor_created_by
        FOREIGN KEY (created_by) REFERENCES tb_users(id),
    CONSTRAINT fk_contributor_updated_by
        FOREIGN KEY (updated_by) REFERENCES tb_users(id)
)engine=InnoDB default charset=utf8mb4;

/*Licença do livro*/
CREATE TABLE IF NOT EXISTS tb_book_license(
    id INT PRIMARY KEY,
    label VARCHAR(50),
    abbreviation VARCHAR(15),
    description VARCHAR(255)
)engine=InnoDB default charset=utf8mb4;

INSERT IGNORE INTO tb_book_license
VALUES (0, 'Unknown', NULL, 'Status de licença desconhecido ou não especificado'),
       (1, 'All Rights Reserved', 'ARR', 'Todos os direitos reservados. Nenhuma reutilização sem permissão explícita.'),
       (2, 'Public Domain', 'PD', 'Obra sem direitos autorais. Pode ser usada livremente.'),
       (3, 'Creative Commons Attribution', 'CC BY', 'Permitido compartilhar e adaptar, desde que com crédito.'),
       (4, 'Creative Commons ShareAlike', 'CC BY-SA', 'Permitido modificar se compartilhar com a mesma licença.'),
       (5, 'Creative Commons NonCommercial', 'CC BY-NC', 'Permitido apenas para uso não comercial, com crédito.'),
       (6, 'Creative Commons NoDerivatives', 'CC BY-ND', 'Permitido compartilhar, mas não modificar.'),
       (7, 'Creative Commons NonCommercial-ShareAlike', 'CC BY-NC-SA', 'Permitido para uso não comercial, desde que compartilhe com a mesma licença.'),
       (8, 'Creative Commons NonCommercial-NoDerivatives', 'CC BY-NC-ND', 'Permitido para uso não comercial, sem modificações.');

/*Restrição do livro*/
CREATE TABLE IF NOT EXISTS tb_book_restriction(
    id INT PRIMARY KEY,
    age INT NOT NULL,
    description VARCHAR(255)
)engine=InnoDB default charset=utf8mb4;

INSERT IGNORE INTO tb_book_restriction
VALUES(1, 0, 'Livre para todos os públicos - conteúdo adequado para qualquer idade'),
      (2, 10, 'Conteúdo leve - pode conter violência fantasiosa ou linguagem branda'),
      (3, 12, 'Pode conter violência moderada, linguagem leve ou temas levemente sensíveis'),
      (4, 14, 'Pode conter violência mais intensa, linguagem moderada ou temas complexos'),
      (5, 16, 'Pode conter violência intensa, linguagem forte, conteúdo sexual ou temas pesados'),
      (6, 18, 'Apenas para adultos - pode conter conteúdo extremo, violência gráfica, sexo explícito');


/*Status do livro*/
CREATE TABLE IF NOT EXISTS tb_book_status(
    id INT PRIMARY KEY,
    label VARCHAR(10) NOT NULL
)engine=InnoDB default charset=utf8mb4;


INSERT IGNORE INTO tb_book_status
VALUES(1, 'Completo'),
      (2, 'Em progresso'),
      (3, 'Abandonado'),
      (4, 'Sem tradutor');

/*Tipos de lívros*/
CREATE TABLE IF NOT EXISTS tb_book_type (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(25) NOT NULL
)engine=InnoDB default charset=utf8mb4;

INSERT IGNORE INTO  tb_book_type
VALUES (1, 'Romance'),
       (2, 'Novela'),
       (3, 'Conto'),
       (4, 'Fábula'),
       (5, 'Crônica'),
       (6, 'Web Novel'),
       (7, 'Light Novel'),
       (8, 'Fanfic');

/*Línguas*/
CREATE TABLE IF NOT EXISTS tb_language(
    id INT PRIMARY KEY AUTO_INCREMENT,
    value VARCHAR(15)
)engine=InnoDB default charset=utf8mb4;

/*======= Fim das tabelas independentes=========*/