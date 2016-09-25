package com.pack.pack.client.internal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 
 * @author Saurav
 * 
 */
public class EncryptionUtil {

	public static String encryptPassword(String plainTextPassword) {
		try {
			String encryptedText = generateMD5HashKey(plainTextPassword, false, false);
			return new String(Base64.getEncoder().encode(encryptedText.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String generateMD5HashKey(String key, boolean addSeperator, boolean toUpper)
			throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(key.getBytes());
		byte[] digest = md5.digest();
		return convertToHexString(digest, addSeperator, toUpper);
	}
	
	private static String convertToHexString(byte[] digest, boolean addSeperator, boolean toUpper) {
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < digest.length; i++) {
			int x = (digest[i] & 0xff) + 0x100;
			if(i > 0 && i<digest.length-1 && i%4 == 0 && addSeperator) {
				strBuilder.append("-");
			}
			strBuilder.append(toUpper ? Integer.toString(x, 16).toUpperCase() : Integer.toString(x, 16));
		}
		return strBuilder.toString();
	}
}