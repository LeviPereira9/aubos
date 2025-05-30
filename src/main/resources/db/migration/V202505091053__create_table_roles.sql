CREATE TABLE tb_roles(
    id TINYINT UNSIGNED PRIMARY KEY,
    name VARCHAR(20)
) engine=InnoDB default charset=utf8mb4;

INSERT INTO tb_roles (id, name)
    VALUES (1, 'READER'),
           (2, 'AUTHOR'),
           (3, 'TRANSLATOR'),
           (4, 'MOD'),
           (5, 'ADMIN');

ALTER TABLE tb_users ADD role TINYINT UNSIGNED DEFAULT 1;
ALTER TABLE tb_users ADD
    CONSTRAINT fk_users_role
        FOREIGN KEY (role) REFERENCES tb_roles(id);