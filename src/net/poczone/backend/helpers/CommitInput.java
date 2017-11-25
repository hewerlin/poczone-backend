package net.poczone.backend.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import net.poczone.framework.definitions.operations.Input;

public class CommitInput implements Input<JSONObject> {

	@Override
	public String getName() {
		return "commit";
	}

	@Override
	public JSONObject parse(String value) throws IllegalArgumentException {
		try {
			JSONObject commit = new JSONObject(value);

			for (String key : commit.keySet()) {
				if (!isValidKey(key)) {
					throw new IllegalArgumentException();
				}
				if (!commit.isNull(key)) {
					commit.getJSONObject(key);
				}
			}

			return commit;
		} catch (JSONException e) {
			throw new IllegalArgumentException();
		}
	}

	private boolean isValidKey(String key) {
		return key.matches("[a-zA-Z0-9_\\-\\.:/]{1,42}");
	}

}
