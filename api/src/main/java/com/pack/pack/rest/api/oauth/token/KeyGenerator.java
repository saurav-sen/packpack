package com.pack.pack.rest.api.oauth.token;


import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.pack.pack.rest.api.security.util.EncryptionUtil;

/**
 * 
 * @author Saurav
 *
 */
public class KeyGenerator {
	
	public KeyGenerator() {
	}
	
	public String generateNewToken() throws Exception {
		StringBuilder token = new StringBuilder();
		token.append(generateMD5HashKey(UUID.randomUUID()));
		token.append(generateSH1HashKey(UUID.randomUUID()));
		return token.toString();
	}
	
	public String generateSH1HashKey(UUID guid) throws NoSuchAlgorithmException {
		return EncryptionUtil.generateSH1HashKey(guid.toString(), true, true);
	}

	public String generateMD5HashKey(UUID guid) throws NoSuchAlgorithmException {
		return EncryptionUtil.generateMD5HashKey(guid.toString(), true, true);
	}
	
	public String generateNewOTPHashKey() throws NoSuchAlgorithmException {
		String md5key = generateMD5HashKey(UUID.randomUUID());
		int key = (int)(Math.abs(md5key.hashCode()) % 1e7);
		return Integer.toHexString(key).toUpperCase();
	}
	
	public static void main(String[] args) throws Exception {
		for(int i=0; i<1000; i++) {
			System.out.println("Token:: " + new KeyGenerator().generateNewToken());
		}
		System.out.println();
		System.out.println("**************************");
		System.out.println();
		for(int i=0; i<1000; i++) {
			System.out.println("OTP:: " + new KeyGenerator().generateNewOTPHashKey());
		}
	}
}