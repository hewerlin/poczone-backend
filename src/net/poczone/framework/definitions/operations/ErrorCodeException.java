package net.poczone.framework.definitions.operations;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

public class ErrorCodeException extends Exception {
	private static final long serialVersionUID = -6455209188456490797L;

	private ErrorCode code;
	private Object[] arguments;

	public ErrorCodeException(ErrorCode code, Object... arguments) {
		this.code = code;
		this.arguments = arguments;
	}

	public ErrorCode getCode() {
		return code;
	}

	public Object[] getArguments() {
		return arguments;
	}

	@Override
	public String getMessage() {
		return code.toString() + (arguments.length > 0 ? " " + Arrays.toString(arguments) : "");
	}

	public Object toJSON() {
		JSONObject jsonError = new JSONObject().put("httpCode", code.getHttpStatus()).put("code", code.getName());

		Object[] args = arguments;
		if (args != null && args.length > 0) {
			JSONArray jsonArgs = new JSONArray(Arrays.asList(args));
			jsonError.put("arguments", jsonArgs);
		}

		return jsonError;
	}
}
