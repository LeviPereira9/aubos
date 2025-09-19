-- tb book
ALTER TABLE tb_book
    MODIFY COLUMN title VARCHAR(255) NOT NULL,
    MODIFY COLUMN subtitle VARCHAR(255);

-- tb book family
ALTER TABLE tb_book_family
    MODIFY COLUMN  name VARCHAR(155) NOT NULL;

-- tb contributor
ALTER TABLE tb_contributor
    MODIFY COLUMN name VARCHAR(255) NOT NULL;

-- tb book status
ALTER TABLE tb_book_status
    MODIFY COLUMN label VARCHAR(100) NOT NULL;

-- tb book family membership
ALTER TABLE tb_book_family_membership
    MODIFY COLUMN note VARCHAR(255);