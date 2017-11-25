package net.poczone.framework.defaults.input;

import net.poczone.framework.definitions.operations.ErrorCodeException;
import net.poczone.framework.definitions.operations.Input;

public class LongInput implements Input<Long> {
	private String name;
	private long minValue;
	private long maxValue;

	public LongInput(String name, long minValue, long maxValue) {
		this.name = name;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Long parse(String value) throws IllegalArgumentException, ErrorCodeException {
		try {
			long longValue = Long.parseLong(value);
			if (minValue <= longValue && longValue <= maxValue) {
				return longValue;
			}
		} catch (Exception e) {
		}

		throw new IllegalArgumentException();
	}
}
