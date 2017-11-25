package net.poczone.framework.definitions.context;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public interface Database extends AutoCloseable {
	BatchUpdate createBatch(String sql) throws SQLException;

	JSONArray select(String sql, Object... args) throws SQLException;

	<T> List<T> select(String sql, RowMapper<T> mapper, Object... args) throws SQLException;

	JSONObject selectOne(String sql, Object... args) throws SQLException;

	<T> T selectOne(String sql, RowMapper<T> mapper, Object... args) throws SQLException;

	int count(String sql, Object... args) throws SQLException;

	int run(String sql, Object... args) throws SQLException;

	void close() throws SQLException;

	public static interface RowMapper<T> {
		T map(ResultSet result) throws SQLException;
	}

	public static interface BatchUpdate {
		void add(Object... args) throws SQLException;

		void run() throws SQLException;
	}
}
