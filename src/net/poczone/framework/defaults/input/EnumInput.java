package net.poczone.framework.defaults.input;

import net.poczone.framework.definitions.operations.Input;

public class EnumInput<T extends Enum<T>> implements Input<T> {
	private String name;
	private Class<T> enumClass;

	public EnumInput(String name, Class<T> enumClass) {
		this.name = name;
		this.enumClass = enumClass;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public T parse(String value) throws IllegalArgumentException {
		T[] values = enumClass.getEnumConstants();
		for (T enumValue : values) {
			if (enumValue.name().equals(value)) {
				return enumValue;
			}
		}

		throw new IllegalArgumentException();
	}
}
