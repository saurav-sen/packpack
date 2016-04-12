package com.pack.pack.oauth1.client.internal;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacSha1Method {

	private static final String SIGNATURE_ALGORITHM = "HmacSHA1";
	
	public static final HmacSha1Method INSTANCE = new HmacSha1Method();
	
	public static final String NAME = "HMAC-SHA1";

	public String sign(String baseString, OAuth1Secrets secrets) {

		Mac mac;

		try {
			mac = Mac.getInstance(SIGNATURE_ALGORITHM);
		} catch (NoSuchAlgorithmException nsae) {
			throw new IllegalStateException(nsae);
		}

		StringBuilder buf = new StringBuilder();

		// null secrets are interpreted as blank per OAuth specification
		String secret = secrets.getConsumerSecret();
		if (secret != null) {
			buf.append(UriComponent
					.encode(secret, UriComponent.Type.UNRESERVED));
		}

		buf.append('&');
		
		secret = secrets.getTokenSecret();
        if (secret != null) {
            buf.append(UriComponent.encode(secret, UriComponent.Type.UNRESERVED));
        }

		byte[] key;

		try {
			key = buf.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException uee) {
			throw new IllegalStateException(uee);
		}

		SecretKeySpec spec = new SecretKeySpec(key, SIGNATURE_ALGORITHM);

		try {
			mac.init(spec);
		} catch (InvalidKeyException ike) {
			throw new IllegalStateException(ike);
		}

		return new String(java.util.Base64.getEncoder().encode(
				mac.doFinal(baseString.getBytes())));
		// return Base64.encode(mac.doFinal(baseString.getBytes()));
	}
}
