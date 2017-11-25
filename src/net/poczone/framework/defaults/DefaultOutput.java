package net.poczone.framework.defaults;

import net.poczone.framework.definitions.operations.Output;

public class DefaultOutput<T> implements Output<T> {
	private String name;
	private Class<T> type;

	public DefaultOutput(String name, Class<T> type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public String toString() {
		return name + " (" + type.getSimpleName() + ")";
	}
}
