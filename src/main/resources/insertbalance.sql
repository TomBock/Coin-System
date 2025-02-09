INSERT INTO users (player_id, coins)
VALUES (?, ?)
ON DUPLICATE KEY UPDATE coins = coins + ?;