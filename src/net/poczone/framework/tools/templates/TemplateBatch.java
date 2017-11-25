package net.poczone.framework.tools.templates;

import java.io.IOException;

public class TemplateBatch {
	private StringBuilder sb;
	private Template template;
	private TemplateInstance next;

	public TemplateBatch(Template template) {
		this.template = template;
		reset();
	}

	public TemplateBatch put(String key, Object value) {
		next.put(key, value);
		return this;
	}

	public TemplateBatch addBatch() {
		sb.append(next.toString());
		next = new TemplateInstance(template);
		return this;
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	public byte[] toUTF8() throws IOException {
		return toString().getBytes("UTF-8");
	}

	public void reset() {
		sb = new StringBuilder();
		next = new TemplateInstance(template);
	}
}
