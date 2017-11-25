package net.poczone.framework.tools.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import net.poczone.framework.definitions.context.Database;

public class JdbcDatabase implements Database {
	private static final String[] DEFAULT_DRIVERS = new String[] { "com.mysql.jdbc.Driver" };

	static {
		for (String driver : DEFAULT_DRIVERS) {
			try {
				Class.forName(driver);
			} catch (Exception e) {
			}
		}
	}

	private String url;
	private String username;
	private String password;

	private Connection connection;

	public JdbcDatabase(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	@Override
	public JSONArray select(String sql, Object... args) throws SQLException {
		return new JSONArray(select(sql, new JSONObjectRowMapper(), args));
	}

	@Override
	public <T> List<T> select(String sql, RowMapper<T> mapper, Object... args) throws SQLException {
		List<T> list = new ArrayList<>();
		try (PreparedStatement stmt = prepareStatement(buildSelect(sql), args);
				ResultSet result = stmt.executeQuery()) {
			while (result.next()) {
				list.add(mapper.map(result));
			}
		}
		return list;
	}

	public JSONObject selectOne(String sql, Object... args) throws SQLException {
		return selectOne(sql, new JSONObjectRowMapper(), args);
	}

	@Override
	public <T> T selectOne(String sql, RowMapper<T> mapper, Object... args) throws SQLException {
		try (PreparedStatement stmt = prepareStatement(buildSelect(sql), args);
				ResultSet result = stmt.executeQuery()) {
			if (result.next()) {
				return mapper.map(result);
			} else {
				return null;
			}
		}
	}

	private static String buildSelect(String sql) {
		return sql.startsWith("SELECT ") ? sql : "SELECT " + sql;
	}

	@Override
	public int count(String sql, Object... args) throws SQLException {
		return selectOne("SELECT count(*) FROM " + sql, new RowMapper<Integer>() {
			@Override
			public Integer map(ResultSet result) throws SQLException {
				return result.getInt(1);
			}
		}, args);
	}

	@Override
	public int run(String sql, Object... args) throws SQLException {
		try (PreparedStatement stmt = prepareStatement(sql, args)) {
			return stmt.executeUpdate();
		}
	}

	@Override
	public BatchUpdate createBatch(String sql) throws SQLException {
		final PreparedStatement stmt = getConnection().prepareStatement(sql);

		return new BatchUpdate() {
			@Override
			public void add(Object... args) throws SQLException {
				putArgs(stmt, args);
				stmt.addBatch();
			}

			@Override
			public void run() throws SQLException {
				stmt.executeBatch();
				stmt.close();
			}
		};
	}

	private PreparedStatement prepareStatement(String sql, Object[] args) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		putArgs(stmt, args);
		return stmt;
	}

	private void putArgs(PreparedStatement stmt, Object[] args) throws SQLException {
		for (int i = 0; i < args.length; i++) {
			stmt.setObject(i + 1, args[i]);
		}
	}

	private Connection getConnection() throws SQLException {
		if (connection == null) {
			connection = DriverManager.getConnection(url, username, password);
		}
		return connection;
	}

	@Override
	public void close() throws SQLException {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

	private static class JSONObjectRowMapper implements RowMapper<JSONObject> {
		@Override
		public JSONObject map(ResultSet result) throws SQLException {
			JSONObject object = new JSONObject();

			int columnCount = result.getMetaData().getColumnCount();
			for (int col = 1; col <= columnCount; col++) {
				String name = result.getMetaData().getColumnName(col);
				object.put(name, result.getObject(col));
			}

			return object;
		}
	}
}
