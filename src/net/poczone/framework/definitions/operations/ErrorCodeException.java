package net.poczone.framework.definitions.operations;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import net.poczone.framework.definitions.context.Loca;

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

	public JSONObject getLocalizedMessage(Loca loca) {
		JSONObject messages = loca.getAll(getCode().getName());

		for (String lang : messages.keySet()) {
			String message = messages.getString(lang);
			messages.put(lang, replaceArgumentPlaceholders(message, lang));
		}

		return messages;
	}

	private String replaceArgumentPlaceholders(String input, String lang) {
		String message = input;

		for (int i = 0; i < arguments.length; i++) {
			String argString;
			if (arguments[i] instanceof JSONObject) {
				argString = ((JSONObject) arguments[i]).optString(lang);
			} else {
				argString = String.valueOf(arguments[i]);
			}
			message = message.replace("{" + i + "}", argString);
		}

		return message;
	}

	public JSONObject toJSON(Loca loca) {
		JSONObject jsonError = new JSONObject().put("httpCode", code.getHttpStatus()).put("code", code.getName());

		Object[] args = arguments;
		if (args != null && args.length > 0) {
			JSONArray jsonArgs = new JSONArray(Arrays.asList(args));
			jsonError.put("arguments", jsonArgs);
		}

		if (loca != null) {
			jsonError.put("message", getLocalizedMessage(loca));
		}

		return jsonError;
	}
}
