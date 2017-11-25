package net.poczone.framework.definitions.context;

import java.util.List;

import org.json.JSONObject;

public interface Loca {
	List<String> getLanguages();
	
	String get(String language, String key, String defaultValue);
	
	JSONObject getAll(String key);
}
