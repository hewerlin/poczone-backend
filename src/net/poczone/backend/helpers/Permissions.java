package net.poczone.backend.helpers;

import java.sql.SQLException;

import org.json.JSONObject;

import net.poczone.backend.helpers.DataTokens.DataAccess;
import net.poczone.framework.defaults.FrameworkErrorCodes;
import net.poczone.framework.definitions.context.ExecutionContext;
import net.poczone.framework.definitions.operations.ErrorCodeException;

public class Permissions {
	private ExecutionContext context;

	public Permissions(ExecutionContext context) throws ErrorCodeException {
		this.context = context;
	}

	public int getUserID() throws ErrorCodeException {
		return context.get(SessionTokens.SESSION_TOKEN).getUserID();
	}

	public int getSpaceID(String spaceUUID, PermissionLevel requiredLevel) throws SQLException, ErrorCodeException {
		JSONObject perm = context.getDatabase()
				.selectOne("SELECT spaceusers.level, spaceusers.spaceID FROM spaces, spaceusers"
						+ " WHERE spaces.spaceID=spaceusers.spaceID AND spaces.spaceUUID=? AND spaceusers.userID=?",
						spaceUUID, getUserID());

		PermissionLevel actualLevel = perm != null ? PermissionLevel.valueOf(perm.getString("level"))
				: PermissionLevel.NONE;

		if (actualLevel.ordinal() < requiredLevel.ordinal()) {
			throw new ErrorCodeException(FrameworkErrorCodes.INSUFFICIENT_RIGHTS, requiredLevel.name(), actualLevel.name());
		}

		int spaceID = perm != null ? perm.getInt("spaceID") : 0;
		return spaceID;
	}

	public int getDataTokenSpaceID(boolean write) throws ErrorCodeException {
		DataAccess token = context.get(DataTokens.DATA_TOKEN);
		if (write && !token.isAllowWrite()) {
			throw new ErrorCodeException(FrameworkErrorCodes.INSUFFICIENT_RIGHTS, PermissionLevel.WRITER.name(),
					PermissionLevel.READER.name());
		}
		return token.getSpaceID();
	}

	public static enum PermissionLevel {
		NONE, READER, WRITER, ADMIN
	}
}
