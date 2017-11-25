package net.poczone.framework.defaults;

import net.poczone.framework.definitions.operations.ErrorCode;

public interface FrameworkErrorCodes {
	public static final ErrorCode NOT_YET_IMPLEMENTED = new ErrorCode(500, "NOT_YET_IMPLEMENTED");
	public static final ErrorCode DEPRECATED = new ErrorCode(500, "DEPRECATED");

	public static final ErrorCode DATABASE_EXCEPTION = new ErrorCode(500, "DATABASE_EXCEPTION");
	public static final ErrorCode IO_EXCEPTION = new ErrorCode(500, "IO_EXCEPTION");
	public static final ErrorCode INTERNAL_EXCEPTION = new ErrorCode(500, "INTERNAL_EXCEPTION");

	public static final ErrorCode MISSING_PARAMETER = new ErrorCode(400, "MISSING_PARAMETER");
	public static final ErrorCode INVALID_PARAMETER_VALUE = new ErrorCode(400, "INVALID_PARAMETER_VALUE");
	public static final ErrorCode INVALID_ACCESS_TOKEN = new ErrorCode(400, "INVALID_ACCESS_TOKEN");
	public static final ErrorCode INSUFFICIENT_RIGHTS = new ErrorCode(403, "INSUFFICIENT_RIGHTS");

	public static final ErrorCode ORIGIN_DENIED = new ErrorCode(500, "ORIGIN_DENIED");
	public static final ErrorCode OPERATION_NOT_FOUND = new ErrorCode(404, "OPERATION_NOT_FOUND");
}
