package com.pack.pack.rest.api.security.util;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Encoder;

/**
 * 
 * @author Saurav
 * 
 */
public class EncryptionUtil {

	private static final String ALGORITHM = "DES"; //$NON-NLS-1$
	
	private static final String SYSTEM_KEY = "48f6b6c4-f2a0-41d7-a61a-f7d54bf8e6be"; //$NON-NLS-1$

	public static String encryptPassword(String plainTextPassword) {
		/*return encryptDecryptPassword(plainTextPassword,
				Cipher.ENCRYPT_MODE);*/
		try {
			String encryptedText = generateMD5HashKey(plainTextPassword, false, false);
			return new BASE64Encoder().encode(encryptedText.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String encryptPasswordUsingSystemKey(String plainTextPassword) {
		return encryptDecryptPassword(SYSTEM_KEY, plainTextPassword,
				Cipher.ENCRYPT_MODE);
	}

	public static String decryptPasswordWithSystemKey(String encryptedPassword) {
		return encryptDecryptPassword(SYSTEM_KEY, encryptedPassword,
				Cipher.DECRYPT_MODE);
	}
	
	private static String encryptDecryptPassword(String key,
			String plainTextPassword, int mode) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			KeySpec keySpec = new DESKeySpec(regenerateKey(key)
					.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance(ALGORITHM);
			SecretKey secretKey = keyFactory.generateSecret(keySpec);
			cipher.init(mode, secretKey);
			if (mode == Cipher.ENCRYPT_MODE) {
				return new BASE64Encoder().encode(cipher
						.doFinal(plainTextPassword.getBytes()));
			} else {
				throw new UnsupportedOperationException("Decrypt password is not supported");
			}
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	private static String regenerateKey(String emailAddr)
			throws NoSuchAlgorithmException {
		StringBuilder key = new StringBuilder();
		key.append(generateSH1HashKey(emailAddr, false, false));
		return key.toString();
	}

	public static String generateSH1HashKey(String key, boolean addSeperator, boolean toUpper)
			throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("SHA1");
		md5.update(key.getBytes());
		byte[] digest = md5.digest();
		return convertToHexString(digest, addSeperator, toUpper);
	}

	public static String generateMD5HashKey(String key, boolean addSeperator, boolean toUpper)
			throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(key.getBytes());
		byte[] digest = md5.digest();
		return convertToHexString(digest, addSeperator, toUpper);
	}
	
	/*private static String convertToHexString(byte[] digest) {
		return convertToHexString(digest, false, false);
	}*/
	
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

	/*public static void main(String[] args) throws Exception {
		String emailAddr = "inbox@mymedicalfiles.in";
		String password = "p@$$w0rd";
		String encryptedPassword = encryptPassword(emailAddr, password);
		System.out.println(encryptedPassword);
		String decryptedPassword = decryptPassword(emailAddr, encryptedPassword);
		System.out.println(decryptedPassword);

		System.out.println(password.equals(decryptedPassword));
	}*/
	
	public static void main(String[] args) throws Exception {
		String str = "healthOrganizationMasterService";
		System.out.println(generateMD5HashKey(str, true, false));
		str = "findNewCityName";
		System.out.println(generateMD5HashKey(str, true, false));
	}
}