package net.poczone.framework.defaults;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONObject;

import net.poczone.framework.definitions.context.Loca;

public class PropertiesLoca implements Loca {
	private Map<String, String> values = new TreeMap<>();
	private Set<String> languages = new TreeSet<>();

	public PropertiesLoca() {
	}

	public PropertiesLoca load(Class<?> clazz) throws IOException {
		for (char c1 = 'a'; c1 <= 'z'; c1++) {
			for (char c2 = 'a'; c2 <= 'z'; c2++) {
				String language = c1 + "" + c2;
				load(clazz, language);
			}
		}
		return this;
	}

	private void load(Class<?> clazz, String language) throws IOException {
		String filename = clazz.getSimpleName() + "_" + language + ".properties";
		InputStream in = clazz.getResourceAsStream(filename);
		if (in != null) {
			Properties properties = new Properties();
			properties.load(in);
			in.close();

			for (Object key : properties.keySet()) {
				String keyString = (String) key;
				add(language, keyString, properties.getProperty(keyString));
			}
		}
	}

	public PropertiesLoca add(String language, String key, String value) {
		values.put(language + ":" + key, value);
		languages.add(language);
		return this;
	}

	@Override
	public String get(String language, String key, String defaultValue) {
		String value = values.get(language + ":" + key);

		if (value != null) {
			return value;
		}

		int slash = key.indexOf("/");
		if (slash >= 0) {
			return get(language, key.substring(slash + 1), defaultValue);
		}

		return defaultValue;
	}

	@Override
	public List<String> getLanguages() {
		return new ArrayList<>(languages);
	}

	@Override
	public JSONObject getAll(String key) {
		JSONObject result = new JSONObject();
		for (String language : languages) {
			String value = get(language, key, null);
			if (value != null) {
				result.put(language, value);
			}
		}
		return result;
	}
}
