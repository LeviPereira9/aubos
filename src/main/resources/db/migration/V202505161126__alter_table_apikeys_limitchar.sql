
ALTER TABLE tb_api_keys
MODIFY COLUMN hashed_secret VARCHAR(128);

ALTER TABLE tb_api_keys
MODIFY COLUMN previous_hashed_secret VARCHAR(128);