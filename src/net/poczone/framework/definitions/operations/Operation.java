package net.poczone.framework.definitions.operations;

import java.util.List;

import net.poczone.framework.definitions.context.ExecutionContext;

public interface Operation {
	String getName();

	List<Input<?>> getInputs();

	List<Output<?>> getOutputs();

	List<ErrorCode> getErrorCodes();

	void execute(ExecutionContext context) throws Exception;
}
