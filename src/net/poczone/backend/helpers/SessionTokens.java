package net.poczone.backend.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.poczone.framework.defaults.FrameworkErrorCodes;
import net.poczone.framework.definitions.operations.ErrorCodeException;
import net.poczone.framework.definitions.operations.Input;

public class SessionTokens {
	public static final Input<Session> SESSION_TOKEN = new SessionTokenInput();

	private static SessionTokens instance = new SessionTokens();

	private Map<String, Session> sessionsByToken = new HashMap<>();

	public static SessionTokens get() {
		return instance;
	}

	public synchronized String create(int userID) {
		String sessionToken = UUID.randomUUID().toString();
		sessionsByToken.put(sessionToken, new Session(sessionToken, userID));
		return sessionToken;
	}

	public synchronized Session get(String sessionToken) throws ErrorCodeException {
		Session session = sessionsByToken.get(sessionToken);
		if (session == null) {
			throw new ErrorCodeException(FrameworkErrorCodes.INVALID_ACCESS_TOKEN, SESSION_TOKEN.getName());
		}
		return session;
	}

	public synchronized void invalidate(String sessionToken) {
		sessionsByToken.remove(sessionToken);
	}

	public static class Session {
		private String sessionToken;
		private int userID;

		public Session(String sessionToken, int userID) {
			this.sessionToken = sessionToken;
			this.userID = userID;
		}

		public String getSessionToken() {
			return sessionToken;
		}

		public int getUserID() {
			return userID;
		}
	}

	public static class SessionTokenInput implements Input<Session> {
		@Override
		public String getName() {
			return "sessionToken";
		}

		@Override
		public Session parse(String value) throws ErrorCodeException {
			return get().get(value);
		}
	}
}
