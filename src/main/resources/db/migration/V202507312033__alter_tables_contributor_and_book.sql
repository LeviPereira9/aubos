ALTER TABLE tb_book MODIFY COLUMN title VARCHAR(255) NOT NULL;
ALTER TABLE tb_book MODIFY COLUMN subtitle VARCHAR(255);

ALTER TABLE tb_contributor ADD COLUMN soft_deleted BOOLEAN NOT NULL DEFAULT FALSE;