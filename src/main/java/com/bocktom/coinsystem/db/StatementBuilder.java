package com.bocktom.coinsystem.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatementBuilder {

	private final PreparedStatement statement;

	public StatementBuilder(Connection con, String updateBalanceSql) throws SQLException {
		statement = con.prepareStatement(updateBalanceSql);
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
