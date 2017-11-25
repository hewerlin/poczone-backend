package net.poczone.framework.definitions.operations;

import net.poczone.framework.definitions.context.ExecutionContext;

@FunctionalInterface
public interface Execution {
	void execute(ExecutionContext context) throws Exception;
}
