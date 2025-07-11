ALTER TABLE tb_users
ADD COLUMN token_id BINARY(16);


CREATE INDEX idx_user_token_id ON tb_users(username, token_id);