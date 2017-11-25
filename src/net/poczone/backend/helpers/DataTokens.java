package net.poczone.backend.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.poczone.framework.defaults.FrameworkErrorCodes;
import net.poczone.framework.definitions.operations.ErrorCodeException;
import net.poczone.framework.definitions.operations.Input;

public class DataTokens {
	public static final Input<DataAccess> DATA_TOKEN = new DataTokenInput();

	private static DataTokens instance = new DataTokens();

	private Map<String, DataAccess> dataTokensByID = new HashMap<>();

	public static DataTokens get() {
		return instance;
	}

	public synchronized String create(String spaceUUID, int spaceID, boolean allowWrite) {
		String dataToken = spaceUUID + "/" + (allowWrite ? "RW" : "R") + "/" + UUID.randomUUID().toString();
		dataTokensByID.put(dataToken, new DataAccess(dataToken, spaceID, allowWrite));
		return dataToken;
	}

	public synchronized DataAccess get(String dataToken) throws ErrorCodeException {
		DataAccess token = dataTokensByID.get(dataToken);
		if (token == null) {
			throw new ErrorCodeException(FrameworkErrorCodes.INVALID_ACCESS_TOKEN, DATA_TOKEN.getName());
		}
		return token;
	}

	public synchronized void revoke(String dataToken) {
		dataTokensByID.remove(dataToken);
	}

	public static class DataAccess {
		private String dataToken;
		private int spaceID;
		private boolean allowWrite;

		public DataAccess(String dataToken, int spaceID, boolean allowWrite) {
			this.dataToken = dataToken;
			this.spaceID = spaceID;
			this.allowWrite = allowWrite;
		}

		public String getDataToken() {
			return dataToken;
		}

		public int getSpaceID() {
			return spaceID;
		}

		public boolean isAllowWrite() {
			return allowWrite;
		}
	}

	public static class DataTokenInput implements Input<DataAccess> {
		@Override
		public String getName() {
			return "dataToken";
		}

		@Override
		public DataAccess parse(String value) throws ErrorCodeException {
			return get().get(value);
		}
	}
}
