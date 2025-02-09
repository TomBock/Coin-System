CREATE TABLE IF NOT EXISTS users (
    player_id BINARY(16) NOT NULL,
    coins INT NOT NULL DEFAULT 0,
    PRIMARY KEY (player_id)
);