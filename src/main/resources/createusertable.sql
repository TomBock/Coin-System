CREATE TABLE IF NOT EXISTS users (
    player_id BINARY(16) NOT NULL,
    coins BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (player_id)
);