ALTER TABLE tb_book_family
    DROP COLUMN share_token,
    DROP COLUMN share_token_expires_at,
    DROP INDEX id;