package net.poczone.framework.tools;

import java.security.SecureRandom;
import java.util.Random;

public class IDGenerator {
	private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

	private static final Random random = new SecureRandom();

	public static synchronized String generate(int length) {
		char[] code = new char[length];
		for (int i = 0; i < length; i++) {
			code[i] = CHARS.charAt(random.nextInt(CHARS.length()));
		}
		return new String(code);
	}

	public static String getPattern(int length) {
		return "[" + CHARS + "]{" + length + "}";
	}
}
