package net.poczone.framework.tools.templates;

import java.util.ArrayList;
import java.util.List;

public class Template {
	private List<String> parts = new ArrayList<String>();

	public Template(String str) {
		String[] parts0 = str.split("\\$\\{");

		parts.add(parts0[0]);
		for (int i = 1; i < parts0.length; i++) {
			String[] parts1 = parts0[i].split("\\}", 2);
			parts.add(parts1[0]);
			parts.add(parts1.length == 2 ? parts1[1] : "");
		}
	}

	public TemplateInstance newInstance() {
		return new TemplateInstance(this);
	}

	public TemplateBatch newBatch() {
		return new TemplateBatch(this);
	}

	public int size() {
		return parts.size();
	}

	public String get(int index) {
		return parts.get(index);
	}
}
