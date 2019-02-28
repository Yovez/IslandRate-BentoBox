package com.yovez.islandrate.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DbUtils {

	public static void close(final ResultSet rs) throws SQLException {
		if (rs != null)
			rs.close();
	}

	public static void close(final Statement stmt) throws SQLException {
		if (stmt != null)
			stmt.close();
	}

	public static void close(final Connection conn) throws SQLException {
		if (conn != null)
			conn.close();
	}

}
