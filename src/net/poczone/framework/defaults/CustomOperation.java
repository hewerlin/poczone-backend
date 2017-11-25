package net.poczone.framework.defaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.poczone.framework.definitions.context.ExecutionContext;
import net.poczone.framework.definitions.operations.ErrorCode;
import net.poczone.framework.definitions.operations.ErrorCodeException;
import net.poczone.framework.definitions.operations.Execution;
import net.poczone.framework.definitions.operations.Input;
import net.poczone.framework.definitions.operations.Operation;
import net.poczone.framework.definitions.operations.Output;

public class CustomOperation implements Operation {
	private String name;

	private List<Input<?>> parameters = new ArrayList<>();
	private List<Output<?>> outputs = new ArrayList<>();
	private List<ErrorCode> errorCodes = new ArrayList<>();

	private Execution execution;

	public CustomOperation(String name) {
		this.name = name;
	}

	public CustomOperation setInput(Input<?>... parameters) {
		this.parameters = Arrays.asList(parameters);
		return this;
	}

	public CustomOperation setOutput(Output<?>... outputs) {
		this.outputs = Arrays.asList(outputs);
		return this;
	}

	public CustomOperation setErrorCodes(ErrorCode... codes) {
		this.errorCodes = Arrays.asList(codes);
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Input<?>> getInputs() {
		return parameters;
	}

	@Override
	public List<Output<?>> getOutputs() {
		return outputs;
	}

	@Override
	public List<ErrorCode> getErrorCodes() {
		return errorCodes;
	}

	public CustomOperation onExecution(Execution execution) {
		if (this.execution != null) {
			throw new IllegalArgumentException("Execution handler already set");
		}
		this.execution = execution;
		return this;
	}

	@Override
	public void execute(ExecutionContext context) throws Exception {
		if (execution == null) {
			throw new ErrorCodeException(FrameworkErrorCodes.NOT_YET_IMPLEMENTED);
		}
		execution.execute(context);
	}
}
