package net.poczone.framework.definitions.context;

import java.io.File;

import net.poczone.framework.definitions.operations.ErrorCodeException;
import net.poczone.framework.definitions.operations.Input;
import net.poczone.framework.definitions.operations.Output;

public interface ExecutionContext extends AutoCloseable {
	File getRoot();

	Database getDatabase();

	Loca getLoca();

	<T> T get(Input<T> param);

	<T> void put(Output<T> output, T jsonValue);

	void addError(ErrorCodeException error);

	void close();
}
