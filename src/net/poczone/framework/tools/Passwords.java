package net.poczone.framework.tools;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class Passwords {
	private static final String HASH_ALGORITHM = "SHA-256";
	private static final int SALT_BYTES = 16;

	private static Random random = new SecureRandom();

	public static synchronized String encode(String password) throws IOException {
		byte[] salt = generateSalt();
		byte[] hash = hash(password, salt);
		return toHex(salt) + ":" + toHex(hash);
	}

	public static boolean check(String inputPassword, String encodedPassword) throws IOException {
		String[] parts = encodedPassword.split(":");
		if (parts.length < 2) {
			return false;
		}

		byte[] salt = fromHex(parts[0]);
		byte[] prevHash = fromHex(parts[1]);

		byte[] hash = hash(inputPassword, salt);

		return Arrays.equals(hash, prevHash);
	}

	private static byte[] generateSalt() {
		byte[] salt = new byte[SALT_BYTES];
		random.nextBytes(salt);
		return salt;
	}

	private static byte[] hash(String password, byte[] salt) throws IOException {
		try {
			MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
			md.update(salt);
			if (password != null) {
				md.update(password.getBytes("UTF-8"));
			}
			return md.digest();
		} catch (Exception e) {
			throw new IOException("Failed to compute password hash", e);
		}
	}

	private static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(Integer.toHexString(0x100 | (0xff & b)).substring(1));
		}
		return sb.toString();
	}

	private static byte[] fromHex(String hex) {
		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
		}
		return bytes;
	}
}
