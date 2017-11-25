package net.poczone.framework.defaults.input;

import net.poczone.framework.definitions.operations.ErrorCodeException;
import net.poczone.framework.definitions.operations.Input;

public class BooleanInput implements Input<Boolean> {
	private String name;

	public BooleanInput(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Boolean parse(String value) throws IllegalArgumentException, ErrorCodeException {
		if ("true".equals(value)) {
			return true;
		} else if ("false".equals(value)) {
			return false;
		} else {
			throw new IllegalArgumentException();
		}
	}
}
