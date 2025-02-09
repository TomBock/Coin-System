package com.bocktom.coinsystem.db;

import com.bocktom.coinsystem.CoinSystemPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQL {

	private static MySQL instance;

	private final String dbUrl;
	private final String dbUser;
	private final String dbPassword;

	private static final String SQL_CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
			"player_id BINARY(16) PRIMARY KEY, " +
			"coins INT NOT NULL DEFAULT 0" +
			");";
	// This will update balance if user exists, insert if not
	private static final String SQL_INSERT_OR_UPDATE_BALANCE = "INSERT INTO users (player_id, coins) VALUES (?, ?) ON DUPLICATE KEY UPDATE coins = coins + ?";
	private static final String SQL_SELECT_BALANCE = "SELECT coins FROM users WHERE player_id = ?";

	private MySQL() {
		FileConfiguration config = CoinSystemPlugin.instance.getConfig();
		String host = config.getString("mysql.host");
		String port = config.getString("mysql.port");
		String database = config.getString("mysql.database");

		dbUser = config.getString("mysql.username");
		dbPassword = config.getString("mysql.password");
		dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
	}

	private static MySQL getInstance() {
		return instance == null ? instance = new MySQL() : instance;
	}

	public static Connection getConnection() throws SQLException {
		MySQL mySQL = getInstance();




		CoinSystemPlugin.instance.getLogger().info("Connecting to database " + mySQL.dbUrl + " as user " + mySQL.dbUser);
		return DriverManager.getConnection(mySQL.dbUrl, mySQL.dbUser, mySQL.dbPassword);
	}

	private static void logException(String msg, SQLException e) {
		CoinSystemPlugin.instance.getLogger().warning(msg + ": " + e.getMessage());
		e.printStackTrace();
	}

	public static void setupDatabase() {

		try (Connection con = getConnection()) {
			int result = new StatementBuilder(con, SQL_CREATE_USERS_TABLE)
					.executeUpdate();

			if(result > 0)
				CoinSystemPlugin.instance.getLogger().info("Created table users");
		} catch (SQLException e) {
			logException("Failed to setup database", e);
		}
	}

	public static boolean addCoinTransaction(UUID playerId, int amount) {


		int affectedRows = 0;
		try (Connection con = getConnection()) {
			con.setAutoCommit(false);

			// UUID as BINARY(16) for faster lookup
			byte[] playerIdBytes = uuidToBytes(playerId);

			// 1: Insert or update Player Balance
			affectedRows = new StatementBuilder(con, SQL_INSERT_OR_UPDATE_BALANCE)
				.setBytes(1, playerIdBytes)
				.setInt(2, amount)
				.setInt(3, amount)
				.executeUpdate();

			if(affectedRows == 0) {
				throw new SQLException("Failed to insert balance for player " + playerId);
			}

			con.commit();
			return true;
		} catch (SQLException e) {
			logException("Failed to insert balance for player", e);
			return false;
		}
	}

	public static int readCoins(UUID playerId) {

		try (Connection con = getConnection()) {
			byte[] playerIdBytes = uuidToBytes(playerId);

			try (ResultSet rs = new StatementBuilder(con, SQL_SELECT_BALANCE)
					.setBytes(1, playerIdBytes)
					.executeQuery()) {

				if(rs.next()) {
					return rs.getInt(0);
				} else {
					return 0;
				}
			}
		} catch (SQLException e) {
			logException("Failed to read balance for player", e);
			return 0;
		}
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
