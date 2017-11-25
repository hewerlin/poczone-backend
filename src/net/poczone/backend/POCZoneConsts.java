package net.poczone.backend;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import net.poczone.backend.helpers.CommitInput;
import net.poczone.backend.helpers.DataTokens;
import net.poczone.backend.helpers.DataTokens.DataAccess;
import net.poczone.backend.helpers.Permissions.PermissionLevel;
import net.poczone.backend.helpers.SessionTokens;
import net.poczone.backend.helpers.SessionTokens.Session;
import net.poczone.framework.defaults.DefaultOutput;
import net.poczone.framework.defaults.input.BooleanInput;
import net.poczone.framework.defaults.input.EnumInput;
import net.poczone.framework.defaults.input.LongInput;
import net.poczone.framework.defaults.input.StringInput;
import net.poczone.framework.definitions.operations.ErrorCode;
import net.poczone.framework.definitions.operations.Input;
import net.poczone.framework.definitions.operations.Output;
import net.poczone.framework.tools.IDGenerator;

public class POCZoneConsts {
	public static final List<String> VALID_ORIGINS = Arrays.asList("https://poczone.net", "https://home.poczone.net",
			"null");

	public static final Input<Session> SESSION_TOKEN = SessionTokens.SESSION_TOKEN;
	public static final Input<DataAccess> DATA_TOKEN = DataTokens.DATA_TOKEN;

	public static final Input<String> USERNAME = new StringInput("username", "[A-Za-z_\\-\\.]+", 42);
	public static final Input<String> PASSWORD = new StringInput("password", ".+");

	public static final int SPACE_ID_LENGTH = 12;
	public static final Input<String> SPACE_UUID = new StringInput("spaceUUID",
			IDGenerator.getPattern(SPACE_ID_LENGTH));
	public static final Input<String> SPACE_NAME = new StringInput("spaceName", ".+", 42);
	public static final Input<String> SPACE_APP = new StringInput("spaceApp", "[A-Z0-9_]+", 10);
	public static final Input<Long> SINCE = new LongInput("since", 0, Long.MAX_VALUE);
	public static final Input<Boolean> WAIT = new BooleanInput("wait");
	public static final Input<JSONObject> COMMIT = new CommitInput();
	public static final Input<PermissionLevel> LEVEL = new EnumInput<>("level", PermissionLevel.class);
	public static final Input<Boolean> WRITE = new BooleanInput("write");
	public static final Input<String> IDS = new StringInput("ids", ".*");

	public static final Output<String> SESSION_TOKEN_OUTPUT = new DefaultOutput<>("sessionToken", String.class);
	public static final Output<JSONObject> SELF = new DefaultOutput<>("self", JSONObject.class);
	public static final Output<JSONObject> SPACE = new DefaultOutput<>("space", JSONObject.class);
	public static final Output<JSONArray> SPACES = new DefaultOutput<>("spaces", JSONArray.class);
	public static final Output<JSONObject> DIFF = new DefaultOutput<>("diff", JSONObject.class);
	public static final Output<Long> NEXT_SINCE = new DefaultOutput<>("nextSince", Long.class);
	public static final Output<String> DATA_TOKEN_OUTPUT = new DefaultOutput<>("dataToken", String.class);

	public static final ErrorCode LOGIN_FAILED = new ErrorCode(400, "LOGIN_FAILED");
	public static final ErrorCode USERNAME_ALREADY_TAKEN = new ErrorCode(400, "USERNAME_ALREADY_TAKEN");
	public static final ErrorCode USER_NOT_FOUND = new ErrorCode(400, "USER_NOT_FOUND");
}
