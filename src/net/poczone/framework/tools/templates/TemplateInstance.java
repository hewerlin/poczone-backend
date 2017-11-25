package net.poczone.framework.tools.templates;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class TemplateInstance {
	private Template template;
	private Map<String, String> args = new TreeMap<String, String>();

	public TemplateInstance(Template template) {
		this.template = template;
	}

	public TemplateInstance put(String key, Object value) {
		args.put(key, value != null ? value.toString() : "");
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(template.get(0));
		for (int i = 1; i + 1 < template.size(); i += 2) {
			sb.append(getArg(template.get(i)));
			sb.append(template.get(i + 1));
		}
		return sb.toString();
	}

	private String getArg(String key) {
		if (key.startsWith("\t")) {
			return "\t" + getArg(key.substring(1)).replace("\n", "\n\t");
		} else if (key.startsWith("html:")) {
			return escapeHtml(getArg(key.substring(5)));
		}

		String value = args.get(key);
		return value != null ? value : "";
	}

	private static String escapeHtml(String str) {
		return str.replace("&", "&amp;").replaceAll("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&apos;");
	}

	public byte[] toUTF8() throws IOException {
		return toString().getBytes("UTF-8");
	}
}
