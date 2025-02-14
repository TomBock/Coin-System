package com.bocktom.coinsystem.db;

import com.bocktom.coinsystem.CoinSystemPlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQL {

	private static MySQL instance;

	private final HikariDataSource db;

	private final int retries;

	private MySQL() {
		FileConfiguration config = CoinSystemPlugin.instance.getConfig();
		String host = config.getString("mysql.host");
		String port = config.getString("mysql.port");
		String database = config.getString("mysql.database");

		String user = config.getString("mysql.username");
		String pw = config.getString("mysql.password");
		String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&&allowPublicKeyRetrieval=true";

		retries = config.getInt("mysql.retries");

		HikariConfig dbConfig = new HikariConfig();
		dbConfig.setJdbcUrl(url);
		dbConfig.setUsername(user);
		dbConfig.setPassword(pw);
		dbConfig.addDataSourceProperty("cachePrepStmts", "true");
		dbConfig.addDataSourceProperty("prepStmtCacheSize", "10");
		dbConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		db = new HikariDataSource(dbConfig);
	}

	private static MySQL getInstance() {
		return instance == null ? instance = new MySQL() : instance;
	}

	private static void logException(String msg, Exception e) {
		CoinSystemPlugin.instance.getLogger().warning(msg + ": " + e.getMessage());
		e.printStackTrace();
	}

	public static void setupDatabase() {
		MySQL mySQL = getInstance();

		try (Connection con = mySQL.db.getConnection()) {
			int result = new StatementBuilder(con, "createusertable.sql")
					.executeUpdate();

			if(result > 0)
				CoinSystemPlugin.instance.getLogger().info("Created table users");
		} catch (Exception e) {
			logException("Failed to setup database", e);
		}
	}

	public static CompletableFuture<Boolean> addCoinTransaction(UUID playerId, long amount) {
		return addCoinTransaction(playerId, amount, MySQL.getInstance().retries);
	}

	private static CompletableFuture<Boolean> addCoinTransaction(UUID playerId, long amount, int retriesLeft) {
		CompletableFuture<Boolean> future = new CompletableFuture<>();

		MySQL mySQL = getInstance();

		Bukkit.getScheduler().runTaskAsynchronously(CoinSystemPlugin.instance, () -> {

			int affectedRows = 0;
			try (Connection con = mySQL.db.getConnection()) {
				con.setAutoCommit(false);

				// UUID as BINARY(16) for faster lookup
				byte[] playerIdBytes = uuidToBytes(playerId);

				// 1: Insert or update Player Balance
				affectedRows = new StatementBuilder(con, "insertbalance.sql")
						.setBytes(1, playerIdBytes)
						.setLong(2, amount)
						.setLong(3, amount)
						.executeUpdate();

				if(affectedRows == 0) {
					throw new SQLException("Failed to insert balance for player " + playerId);
				}

				con.commit();
				future.complete(true);
			} catch (Exception e) {
				logException("Failed to insert balance for player. Retries left: " + retriesLeft, e);

				if(retriesLeft > 0)
				{
					Bukkit.getScheduler().runTaskLaterAsynchronously(CoinSystemPlugin.instance, () -> {
						addCoinTransaction(playerId, amount, retriesLeft - 1).thenAccept(future::complete);
					}, 20L); // 1 second later

				} else {
					future.complete(false);
				}
			}

		});
		return future;
	}

	public static CompletableFuture<Long> readBalance(UUID playerId) {
		return attemptReadCoins(playerId, MySQL.getInstance().retries);
	}

	private static CompletableFuture<Long> attemptReadCoins(UUID playerId, int retriesLeft) {
		CompletableFuture<Long> future = new CompletableFuture<>();
		MySQL mySQL = getInstance();

		Bukkit.getScheduler().runTaskAsynchronously(CoinSystemPlugin.instance, () -> {

			try (Connection con = mySQL.db.getConnection()) {
				byte[] playerIdBytes = uuidToBytes(playerId);

				try (ResultSet rs = new StatementBuilder(con, "readbalance.sql")
						.setBytes(1, playerIdBytes)
						.executeQuery()) {

					if(rs != null && rs.next()) {
						future.complete(rs.getLong("coins"));
					} else {
						future.complete(0L);
					}
				}
			} catch (Exception e) {
				logException("Failed to read balance for player. Retries left: " + retriesLeft, e);

				if(retriesLeft > 0)
				{
					Bukkit.getScheduler().runTaskLaterAsynchronously(CoinSystemPlugin.instance, () -> {
						attemptReadCoins(playerId, retriesLeft - 1).thenAccept(future::complete);
					}, 20L); // 1 second later

				} else {
					future.complete(-1L);
				}
			}
		});
		return future;
	}

	private static byte[] uuidToBytes(UUID playerId) {
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(playerId.getMostSignificantBits());
		bb.putLong(playerId.getLeastSignificantBits());
		return bb.array();
	}

	private static UUID bytesToUUID(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		long high = bb.getLong();
		long low = bb.getLong();
		return new UUID(high, low);
	}
}
