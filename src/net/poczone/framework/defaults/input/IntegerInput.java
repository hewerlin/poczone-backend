package net.poczone.framework.defaults.input;

import net.poczone.framework.definitions.operations.ErrorCodeException;
import net.poczone.framework.definitions.operations.Input;

public class IntegerInput implements Input<Integer> {
	private String name;
	private int minValue;
	private int maxValue;

	public IntegerInput(String name, int minValue, int maxValue) {
		this.name = name;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Integer parse(String value) throws IllegalArgumentException, ErrorCodeException {
		try {
			int intValue = Integer.parseInt(value);
			if (minValue <= intValue && intValue <= maxValue) {
				return intValue;
			}
		} catch (Exception e) {
		}
		
		throw new IllegalArgumentException();
	}
}
