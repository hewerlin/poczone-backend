package net.poczone.framework.definitions.operations;

public interface Input<T> {
	String getName();

	T parse(String value) throws IllegalArgumentException, ErrorCodeException;
}
