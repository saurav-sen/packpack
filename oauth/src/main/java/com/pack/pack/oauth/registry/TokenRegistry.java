package com.pack.pack.oauth.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.oauth.token.AccessToken;
import com.pack.pack.oauth.token.AccessTokenInfo;
import com.pack.pack.oauth.token.Token;
import com.pack.pack.oauth.token.TokenInfo;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.RedisCacheService;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
public class TokenRegistry {

	public static final TokenRegistry INSTANCE = new TokenRegistry();

	public static final String TOKEN_REGISTRY = "token-registry"; //$NON-NLS-1$

	private static final Logger LOG = LoggerFactory
			.getLogger(TokenRegistry.class);

	private TokenRegistry() {
	}

	public void start() {
	}

	public Token serviceRequestToken(String token) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			TokenInfo info = cacheService.getFromCache(token, TokenInfo.class);
			return Token.build(info);
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
			return null;
		}
	}

	public void addRequestToken(Token token) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			long ttlSeconds = 2 * 60 * 60;
			cacheService.addToCache(token.getToken(), token.convert(),
					ttlSeconds);
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
		}
	}

	public Token invalidateRequestToken(String token) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			TokenInfo fromCache = cacheService.getFromCache(token,
					TokenInfo.class);
			if (fromCache != null) {
				cacheService.removeFromCache(token);
			}
			return Token.build(fromCache);
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
			return null;
		}
	}

	public boolean isValidAccessToken(String token) {
		RedisCacheService cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		boolean exists = cacheService.isKeyExists(token);
		if (exists) {
			cacheService.setTTL(token, 2 * 60 * 60);
		}
		return exists;
	}

	private boolean isValidRefreshToken(String refreshToken,
			String accessToken, String deviceId) throws PackPackException {
		RedisCacheService cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		boolean bool = cacheService.isKeyExists(refreshToken);
		if (!bool) {
			return false;
		}
		String expectedOldToken = cacheService.getFromCache(refreshToken,
				String.class);
		return expectedOldToken != null && accessToken != null
				&& expectedOldToken.equals(accessToken);
	}

	public boolean removeRefreshToken(String refreshToken, String accessToken,
			String deviceId) throws PackPackException {
		boolean validRefreshToken = isValidRefreshToken(refreshToken,
				accessToken, deviceId);
		if (validRefreshToken) {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			String oldAccessToken = cacheService.getFromCache(refreshToken,
					String.class);
			if (oldAccessToken != null
					&& cacheService.isKeyExists(oldAccessToken)) {
				cacheService.removeFromCache(oldAccessToken);
			}
			cacheService.removeFromCache(refreshToken);
		}
		return validRefreshToken;
	}

	public void addAccessToken(AccessToken token) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.addToCache(token.getToken(), token.convert(),
					2 * 60 * 60);
			cacheService.addToCache(token.getRefreshToken(), token.getToken(),
					2 * 24 * 60 * 60);
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
		}
	}

	public void invalidateAccessToken(String accessToken) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.removeFromCache(accessToken);
			String refreshToken = cacheService.getFromCache(accessToken
					+ ":expired", String.class);
			if (refreshToken != null) {
				cacheService.removeFromCache(accessToken + ":expired");
				cacheService.removeFromCache(refreshToken);
			}
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
		}
	}

	public void addVerifier(String requestToken, String verifier) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.addToCache(requestToken + ":verifier", verifier);
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
		}
	}

	public String removeVerifier(String requestToken) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			String key = requestToken + ":verifier";
			String verifier = cacheService.getFromCache(key, String.class);
			cacheService.removeFromCache(key);
			return verifier;
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
		}
		return null;
	}

	public Token getAccessToken(String accessToken) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			AccessTokenInfo fromCache = cacheService.getFromCache(accessToken,
					AccessTokenInfo.class);
			if (fromCache == null) {
				return null;
			}
			return AccessToken.build(fromCache);
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
		}
		return null;
	}
}