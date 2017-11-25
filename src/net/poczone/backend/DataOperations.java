package net.poczone.backend;

import static net.poczone.backend.POCZoneConsts.COMMIT;
import static net.poczone.backend.POCZoneConsts.DATA_TOKEN;
import static net.poczone.backend.POCZoneConsts.DATA_TOKEN_OUTPUT;
import static net.poczone.backend.POCZoneConsts.DIFF;
import static net.poczone.backend.POCZoneConsts.IDS;
import static net.poczone.backend.POCZoneConsts.NEXT_SINCE;
import static net.poczone.backend.POCZoneConsts.SESSION_TOKEN;
import static net.poczone.backend.POCZoneConsts.SINCE;
import static net.poczone.backend.POCZoneConsts.SPACE_UUID;
import static net.poczone.backend.POCZoneConsts.WAIT;
import static net.poczone.backend.POCZoneConsts.WRITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONWriter;

import net.poczone.backend.helpers.DataTokens;
import net.poczone.backend.helpers.Permissions;
import net.poczone.backend.helpers.Permissions.PermissionLevel;
import net.poczone.framework.defaults.CustomOperation;
import net.poczone.framework.defaults.FrameworkErrorCodes;
import net.poczone.framework.tools.Sleepers;
import net.poczone.framework.definitions.Operations;
import net.poczone.framework.definitions.context.Database.BatchUpdate;
import net.poczone.framework.definitions.operations.Operation;

public class DataOperations implements Operations {
	public static String DATA_JSON_PREFIX = "data/json/";
	
	private Sleepers<Integer> spaceDataSleepers = new Sleepers<>();

	private Operation DATA_TOKEN_CREATE = new CustomOperation("data/token/create")
			.setInput(SESSION_TOKEN, SPACE_UUID, WRITE).setOutput(DATA_TOKEN_OUTPUT)
			.setErrorCodes(FrameworkErrorCodes.INSUFFICIENT_RIGHTS).onExecution(context -> {
				boolean allowWrite = context.get(WRITE);
				PermissionLevel level = allowWrite ? PermissionLevel.WRITER : PermissionLevel.READER;

				String spaceUUID = context.get(SPACE_UUID);
				int spaceID = new Permissions(context).getSpaceID(spaceUUID, level);

				String dataToken = DataTokens.get().create(spaceUUID, spaceID, allowWrite);

				context.put(DATA_TOKEN_OUTPUT, dataToken);
			});

	private Operation DATA_TOKEN_REVOKE = new CustomOperation("data/token/revoke").setInput(DATA_TOKEN)
			.onExecution(context -> {
				DataTokens.get().revoke(context.get(DATA_TOKEN).getDataToken());
			});

	private Operation DATA_JSON_POST = new CustomOperation("data/json/post").setInput(DATA_TOKEN, COMMIT)
			.setErrorCodes(FrameworkErrorCodes.INSUFFICIENT_RIGHTS).onExecution(context -> {
				Permissions permissions = new Permissions(context);
				int spaceID = permissions.getDataTokenSpaceID(true);

				BatchUpdate batch = context.getDatabase()
						.createBatch("REPLACE spacedata SET spaceID=?, itemID=?, modified=?, json=?");

				long modified = System.currentTimeMillis();
				JSONObject commit = context.get(COMMIT);
				for (String itemID : commit.keySet()) {
					batch.add(spaceID, itemID, modified, JSONWriter.valueToString(commit.get(itemID)));
				}

				batch.run();

				spaceDataSleepers.notifyAll(spaceID);
			});

	private Operation DATA_JSON_GET_DIFF = new CustomOperation("data/json/getDiff").setInput(DATA_TOKEN, SINCE, WAIT)
			.setOutput(DIFF, NEXT_SINCE).onExecution(context -> {
				Permissions tool = new Permissions(context);
				int spaceID = tool.getDataTokenSpaceID(false);
				long since = context.get(SINCE);

				String sql = "SELECT itemID, json FROM spacedata WHERE spaceID=? AND modified>=?";
				JSONArray updates = context.getDatabase().select(sql, spaceID, since);

				if (updates.length() == 0 && context.get(WAIT)) {
					context.getDatabase().close();
					spaceDataSleepers.waitFor(spaceID, 50000);
					updates = context.getDatabase().select(sql, spaceID, since);
				}

				JSONObject diff = new JSONObject();
				for (int i = 0; i < updates.length(); i++) {
					JSONObject row = updates.getJSONObject(i);
					diff.put(row.getString("itemID"), new JSONTokener(row.getString("json")).nextValue());
				}

				context.put(DIFF, diff);
				context.put(NEXT_SINCE, System.currentTimeMillis());
			});

	private Operation DATA_JSON_GET_BY_IDS = new CustomOperation("data/json/getByIDs").setInput(DATA_TOKEN, IDS)
			.setOutput(DIFF).onExecution(context -> {
				Permissions tool = new Permissions(context);
				int spaceID = tool.getDataTokenSpaceID(false);

				String idsString = context.get(IDS).replaceAll("[\\[\\]]", "").trim();
				List<String> ids = "".equals(idsString) ? new ArrayList<>() : Arrays.asList(idsString.split(" *, *"));

				JSONObject diff = new JSONObject();
				String sql = "SELECT itemID, json FROM spacedata WHERE spaceID=? AND itemID=?";
				for (String id : ids) {
					JSONObject row = context.getDatabase().selectOne(sql, spaceID, id);
					if (row != null) {
						diff.put(row.getString("itemID"), new JSONTokener(row.getString("json")).nextValue());
					} else {
						diff.put(id, JSONObject.NULL);
					}
				}

				context.put(DIFF, diff);
			});

	@Override
	public List<Operation> getOperations() {
		return Arrays.asList(DATA_TOKEN_CREATE, DATA_TOKEN_REVOKE, DATA_JSON_POST, DATA_JSON_GET_DIFF,
				DATA_JSON_GET_BY_IDS);
	}
}
