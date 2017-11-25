package net.poczone.framework.tools.templates;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TemplateEngine {
	private File root;

	public TemplateEngine(File root) {
		this.root = root;
	}

	public Template read(String template) throws IOException {
		File file = new File(root, template);
		try (InputStream in = new FileInputStream(file)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int len;
			while ((len = in.read(bytes)) > 0) {
				out.write(bytes, 0, len);
			}
			return new Template(out.toString("UTF-8"));
		}
	}

	public TemplateInstance newInstance(String template) throws IOException {
		return read(template).newInstance();
	}

	public TemplateBatch newBatch(String template) throws IOException {
		return read(template).newBatch();
	}
}
