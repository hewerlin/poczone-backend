package net.poczone.backend;

import static net.poczone.backend.POCZoneConsts.LEVEL;
import static net.poczone.backend.POCZoneConsts.SESSION_TOKEN;
import static net.poczone.backend.POCZoneConsts.SPACE;
import static net.poczone.backend.POCZoneConsts.SPACES;
import static net.poczone.backend.POCZoneConsts.SPACE_APP;
import static net.poczone.backend.POCZoneConsts.SPACE_ID_LENGTH;
import static net.poczone.backend.POCZoneConsts.SPACE_NAME;
import static net.poczone.backend.POCZoneConsts.SPACE_UUID;
import static net.poczone.backend.POCZoneConsts.USERNAME;
import static net.poczone.backend.POCZoneConsts.USER_NOT_FOUND;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import net.poczone.backend.helpers.Permissions;
import net.poczone.backend.helpers.Permissions.PermissionLevel;
import net.poczone.framework.defaults.CustomOperation;
import net.poczone.framework.defaults.FrameworkErrorCodes;
import net.poczone.framework.definitions.Operations;
import net.poczone.framework.definitions.operations.ErrorCodeException;
import net.poczone.framework.definitions.operations.Operation;
import net.poczone.framework.tools.IDGenerator;

public class SpacesOperations implements Operations {
	private Operation SPACES_CREATE = new CustomOperation("spaces/create")
			.setInput(SESSION_TOKEN, SPACE_NAME, SPACE_APP).setOutput(SPACE).onExecution(context -> {
				Permissions permissions = new Permissions(context);

				String spaceUUID = IDGenerator.generate(SPACE_ID_LENGTH);

				context.getDatabase().run("INSERT INTO spaces(spaceUUID, name, app) VALUES (?,?,?)", spaceUUID,
						context.get(SPACE_NAME), context.get(SPACE_APP));

				JSONObject space = context.getDatabase().selectOne("SELECT * FROM spaces WHERE spaceUUID=?", spaceUUID);
				space.put("level", PermissionLevel.ADMIN.name());
				space.put("contributors", new JSONArray());

				int spaceID = space.getInt("spaceID");
				space.remove("spaceID");

				context.getDatabase().run("INSERT INTO spaceusers(userID, spaceID, level) VALUES (?,?,?)",
						permissions.getUserID(), spaceID, PermissionLevel.ADMIN.name());

				context.put(SPACE, space);
			});

	private Operation SPACES_GET_MINE = new CustomOperation("spaces/getMine").setInput(SESSION_TOKEN).setOutput(SPACES)
			.onExecution(context -> {
				Permissions permissions = new Permissions(context);
				int userID = permissions.getUserID();

				JSONArray spaces = context.getDatabase().select(
						"SELECT spaces.*, spaceusers.level FROM spaces, spaceusers WHERE spaces.spaceID=spaceusers.spaceID AND spaceusers.userID=?",
						userID + " ORDER BY spaces.app, spaces.name");

				for (int i = 0; i < spaces.length(); i++) {
					JSONObject space = spaces.getJSONObject(i);

					int spaceID = space.getInt("spaceID");
					space.put("contributors",
							context.getDatabase().select(
									"SELECT users.username, spaceusers.level FROM users, spaceusers WHERE users.userID=spaceusers.userID AND spaceusers.spaceID=? AND spaceusers.userID!=? ORDER BY users.username ASC",
									spaceID, userID));

					space.remove("spaceID");
				}

				context.put(SPACES, spaces);
			});

	private Operation SPACES_EDIT = new CustomOperation("spaces/edit").setInput(SESSION_TOKEN, SPACE_UUID, SPACE_NAME)
			.setOutput().setErrorCodes(FrameworkErrorCodes.INSUFFICIENT_RIGHTS).onExecution(context -> {
				Permissions permissions = new Permissions(context);
				int spaceID = permissions.getSpaceID(context.get(SPACE_UUID), PermissionLevel.ADMIN);

				context.getDatabase().run("UPDATE spaces SET name=? WHERE spaceID=?", context.get(SPACE_NAME), spaceID);
			});

	private Operation SPACES_SHARE = new CustomOperation("spaces/share")
			.setInput(SESSION_TOKEN, SPACE_UUID, USERNAME, LEVEL)
			.setErrorCodes(FrameworkErrorCodes.INSUFFICIENT_RIGHTS, USER_NOT_FOUND).onExecution(context -> {
				Permissions permissions = new Permissions(context);
				int spaceID = permissions.getSpaceID(context.get(SPACE_UUID), PermissionLevel.ADMIN);

				JSONObject user = context.getDatabase().selectOne("SELECT userID FROM users WHERE username=?",
						context.get(USERNAME));

				if (user == null) {
					throw new ErrorCodeException(USER_NOT_FOUND, context.get(USERNAME));
				}
				int userID = user.getInt("userID");

				PermissionLevel level = context.get(LEVEL);
				if (PermissionLevel.NONE.equals(level)) {
					context.getDatabase().run("DELETE FROM spaceusers WHERE spaceID=? AND userID=?", spaceID, userID);
				} else {
					context.getDatabase().run("REPLACE spaceusers SET spaceID=?, userID=?, level=?", spaceID, userID,
							level.name());
				}
			});

	private Operation SPACES_LEAVE = new CustomOperation("spaces/leave").setInput(SESSION_TOKEN, SPACE_UUID)
			.onExecution(context -> {
				Permissions permissions = new Permissions(context);
				int userID = permissions.getUserID();
				int spaceID = permissions.getSpaceID(context.get(SPACE_UUID), PermissionLevel.NONE);

				if (spaceID > 0) {
					context.getDatabase().run("DELETE FROM spaceusers WHERE spaceID=? AND userID=?", spaceID, userID);
				}
			});

	@Override
	public List<Operation> getOperations() {
		return Arrays.asList(SPACES_CREATE, SPACES_GET_MINE, SPACES_EDIT, SPACES_SHARE, SPACES_LEAVE);
	}
}
