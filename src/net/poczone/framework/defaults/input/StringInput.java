package net.poczone.framework.defaults.input;

import net.poczone.framework.definitions.operations.ErrorCodeException;
import net.poczone.framework.definitions.operations.Input;

public class StringInput implements Input<String> {
	private String name;
	private String pattern;
	private int maxLength;

	public StringInput(String name, String pattern) {
		this(name, pattern, Integer.MAX_VALUE);
	}

	public StringInput(String name, String pattern, int maxLength) {
		this.name = name;
		this.pattern = pattern;
		this.maxLength = maxLength;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String parse(String value) throws IllegalArgumentException, ErrorCodeException {
		if (value.matches(pattern) && value.length() <= maxLength) {
			return value;
		} else {
			throw new IllegalArgumentException();
		}
	}
}
