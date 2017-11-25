package net.poczone.backend;

import java.util.Arrays;
import java.util.List;

import net.poczone.framework.defaults.ComposedApplication;
import net.poczone.framework.definitions.Operations;
import net.poczone.framework.definitions.operations.Operation;

public class POCZoneApplication extends ComposedApplication {
	private AuthOperations auth = new AuthOperations();
	private SpacesOperations spaces = new SpacesOperations();
	private DataOperations data = new DataOperations();

	@Override
	public List<Operations> getOperationModules() {
		return Arrays.asList(auth, spaces, data);
	}

	@Override
	public boolean acceptsOrigin(Operation operation, String origin) {
		if (operation.getName().startsWith(DataOperations.DATA_JSON_PREFIX)) {
			return true;
		}
		return POCZoneConsts.VALID_ORIGINS.contains(origin);
	}
}
