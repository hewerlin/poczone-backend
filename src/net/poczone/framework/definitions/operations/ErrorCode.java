package net.poczone.framework.definitions.operations;

public class ErrorCode {
	private int httpStatus;
	private String name;

	public ErrorCode(int httpStatus, String name) {
		this.httpStatus = httpStatus;
		this.name = name;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name + " (HTTP:" + httpStatus + ")";
	}
}
