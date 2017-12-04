package net.poczone.framework.servlet;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import net.poczone.framework.definitions.context.Database;
import net.poczone.framework.definitions.context.ExecutionContext;
import net.poczone.framework.definitions.context.Loca;
import net.poczone.framework.definitions.operations.ErrorCodeException;
import net.poczone.framework.definitions.operations.Input;
import net.poczone.framework.definitions.operations.Output;

public class ExecutionContextImpl implements ExecutionContext {
	private JSONObject failureResult = new JSONObject().put("success", false).put("errors", new JSONArray());
	private JSONObject successResult = new JSONObject().put("success", true);

	private Map<String, Object> parameters = new TreeMap<>();

	private File root;
	private Loca loca;
	private Database database;

	@Override
	public File getRoot() {
		return root;
	}

	protected void setRoot(File root) {
		this.root = root;
	}

	@Override
	public Loca getLoca() {
		return loca;
	}

	protected void setLoca(Loca loca) {
		this.loca = loca;
	}

	@Override
	public Database getDatabase() {
		return database;
	}

	protected void setDatabase(Database database) {
		this.database = database;
	}

	protected void set(Input<?> param, Object value) {
		parameters.put(param.getName(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Input<T> param) {
		if (!parameters.containsKey(param.getName())) {
			throw new IllegalArgumentException("Parameter " + param.getName() + " not declared or present");
		}
		return (T) parameters.get(param.getName());
	}

	@Override
	public <T> void put(Output<T> output, T jsonObject) {
		successResult.put(output.getName(), jsonObject);
	}

	@Override
	public void addError(ErrorCodeException error) {
		JSONArray errors = failureResult.getJSONArray("errors");
		errors.put(error.toJSON(loca));
	}

	protected int getHttpStatus() {
		JSONArray errors = failureResult.getJSONArray("errors");

		Integer foundCode = null;
		for (int i = 0; i < errors.length(); i++) {
			int code = errors.getJSONObject(i).getInt("httpCode");

			if (foundCode == null) {
				foundCode = code;
			} else if (foundCode != code) {
				return 500;
			}
		}

		return foundCode == null ? 200 : foundCode;
	}

	protected JSONObject getSuccessResult() {
		return successResult;
	}

	protected JSONObject getFailureResult() {
		return failureResult;
	}

	protected boolean hasErrors() {
		return failureResult.getJSONArray("errors").length() > 0;
	}

	@Override
	public void close() {
		if (database != null) {
			try {
				database.close();
			} catch (SQLException e) {
			}
		}
	}
}
