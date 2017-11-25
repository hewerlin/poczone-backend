package net.poczone.backend;

import static net.poczone.backend.POCZoneConsts.LOGIN_FAILED;
import static net.poczone.backend.POCZoneConsts.PASSWORD;
import static net.poczone.backend.POCZoneConsts.SELF;
import static net.poczone.backend.POCZoneConsts.SESSION_TOKEN;
import static net.poczone.backend.POCZoneConsts.SESSION_TOKEN_OUTPUT;
import static net.poczone.backend.POCZoneConsts.USERNAME;
import static net.poczone.backend.POCZoneConsts.USERNAME_ALREADY_TAKEN;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import net.poczone.backend.helpers.SessionTokens;
import net.poczone.framework.defaults.CustomOperation;
import net.poczone.framework.definitions.Operations;
import net.poczone.framework.definitions.operations.ErrorCodeException;
import net.poczone.framework.definitions.operations.Operation;
import net.poczone.framework.tools.Passwords;

public class AuthOperations implements Operations {
	private Operation AUTH_LOGIN = new CustomOperation("auth/login").setInput(USERNAME, PASSWORD)
			.setOutput(SELF, SESSION_TOKEN_OUTPUT).setErrorCodes(LOGIN_FAILED).onExecution(context -> {
				JSONObject self = context.getDatabase().selectOne(
						"SELECT userID, username, password FROM users WHERE username=?", context.get(USERNAME));

				if (self == null || !Passwords.check(context.get(PASSWORD), self.getString("password"))) {
					throw new ErrorCodeException(LOGIN_FAILED);
				}

				int userID = self.getInt("userID");
				self.remove("userID");
				self.remove("password");

				String sessionToken = SessionTokens.get().create(userID);

				context.put(SELF, self);
				context.put(SESSION_TOKEN_OUTPUT, sessionToken);
			});

	private Operation AUTH_REGISTER = new CustomOperation("auth/register").setInput(USERNAME, PASSWORD)
			.setOutput(SELF, SESSION_TOKEN_OUTPUT).setErrorCodes(USERNAME_ALREADY_TAKEN).onExecution(context -> {
				try {
					String username = context.get(USERNAME);
					String encodedPassword = Passwords.encode(context.get(PASSWORD));

					context.getDatabase().run("INSERT INTO users(username,password) VALUES (?,?)", username,
							encodedPassword);

				} catch (SQLException e) {
					if (e.getMessage().contains("uniqueUsername")) {
						throw new ErrorCodeException(USERNAME_ALREADY_TAKEN);
					} else {
						throw e;
					}
				}

				AUTH_LOGIN.execute(context);
			});

	private Operation AUTH_LOGOUT = new CustomOperation("auth/logout").setInput(SESSION_TOKEN).onExecution(context -> {
		SessionTokens.get().invalidate(context.get(SESSION_TOKEN).getSessionToken());
	});

	@Override
	public List<Operation> getOperations() {
		return Arrays.asList(AUTH_REGISTER, AUTH_LOGIN, AUTH_LOGOUT);
	}
}
