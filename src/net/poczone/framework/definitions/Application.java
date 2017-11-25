package net.poczone.framework.definitions;

import net.poczone.framework.definitions.operations.Operation;

public interface Application extends Operations {
	boolean acceptsOrigin(Operation operation, String origin);
}
