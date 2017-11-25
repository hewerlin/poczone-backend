package net.poczone.framework.defaults;

import java.util.ArrayList;
import java.util.List;

import net.poczone.framework.definitions.Application;
import net.poczone.framework.definitions.Operations;
import net.poczone.framework.definitions.operations.Operation;

public abstract class ComposedApplication implements Application {
	@Override
	public List<Operation> getOperations() {
		List<Operation> operations = new ArrayList<>();
		for (Operations application : getOperationModules()) {
			operations.addAll(application.getOperations());
		}
		return operations;
	}

	public abstract List<Operations> getOperationModules();
}
