package com.bocktom.coinsystem.db;

import com.bocktom.coinsystem.CoinSystemPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatementBuilder {

	private final PreparedStatement statement;

	public StatementBuilder(Connection con, String sqlFile) throws SQLException, IOException {

		InputStream input = CoinSystemPlugin.instance.getResource(sqlFile);
		if(input == null)
			throw new IOException("Resource not found: " + sqlFile);
		String sql = new String(input.readAllBytes());

		statement = con.prepareStatement(sql);
	}

	public StatementBuilder setInt(int parameterIndex, int value) throws SQLException {
		statement.setInt(parameterIndex, value);
		return this;
	}

	public StatementBuilder setBytes(int parameterIndex, byte[] value) throws SQLException {
		statement.setBytes(parameterIndex, value);
		return this;
	}

	public int executeUpdate() throws SQLException {
		return statement.executeUpdate();
	}

	public ResultSet executeQuery() throws SQLException {
		return statement.executeQuery();
	}
}
