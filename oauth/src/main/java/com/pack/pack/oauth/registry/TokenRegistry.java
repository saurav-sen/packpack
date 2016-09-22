package com.pack.pack.oauth.registry;

import java.security.Principal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.oauth.token.AccessToken;
import com.pack.pack.oauth.token.AccessTokenInfo;
import com.pack.pack.oauth.token.AccessTokens;
import com.pack.pack.oauth.token.PersistedUserToken;
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

	public boolean isValidAccessToken(String token) throws PackPackException {
		RedisCacheService cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		boolean exists = cacheService.isKeyExists(token);
		if (exists) {
			cacheService.setTTL(token, 2 * 60 * 60);
		}
		return exists;
	}

	public boolean isValidRefreshToken(String refreshToken, String userAgent,
			String username, String deviceId) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			PersistedUserToken token = cacheService.getFromCache(refreshToken,
					PersistedUserToken.class);
			if (token == null)
				return false;
			return token.getUserId().equals(username)
					&& token.getUserIp().equals(deviceId);
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
			return false;
		}
	}

	public void addAccessToken(AccessToken token) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			cacheService.addToCache(token.getToken(), token.convert(),
					2 * 60 * 60, token.getRefreshToken());
			Principal principal = token.getPrincipal();
			AccessTokens fromCache = cacheService.getFromCache(
					principal.getName(), AccessTokens.class);
			if (fromCache == null) {
				fromCache = new AccessTokens();
			}
			fromCache.getTokens().add(token.convert());
			String username = principal.getName();
			cacheService.addToCache(username, fromCache);
			String refreshToken = token.getRefreshToken();
			if (refreshToken != null) {
				PersistedUserToken pToken = new PersistedUserToken();
				pToken.setRefreshToken(refreshToken);
				pToken.setTimeOfIssue(new DateTime(DateTimeZone.getDefault())
						.getMillis());
				pToken.setUserId(username);
				cacheService.addToCache(refreshToken, pToken);
			}
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
		}
	}

	public List<AccessToken> getAllAccessTokens(String principalName) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			AccessTokens fromCache = cacheService.getFromCache(principalName,
					AccessTokens.class);
			List<AccessTokenInfo> list = (fromCache != null ? fromCache
					.getTokens() : null);
			if (list == null) {
				return Collections.emptyList();
			}
			List<AccessToken> r = new LinkedList<AccessToken>();
			for (AccessTokenInfo l : list) {
				r.add(AccessToken.build(l));
			}
			return r;
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
		}
		return java.util.Collections.emptyList();
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

		/*
		 * Map<String, Token> map = hazelcast.getMap(ACCESS_TOKEN_CACHE);
		 * AccessToken token = (AccessToken)map.remove(accessToken);
		 * IMap<Principal, List<Token>> map2 =
		 * hazelcast.getMap(PRINCIPAL_VS_ACCESS_TOKEN_CACHE); Principal
		 * principal = token.getPrincipal(); List<Token> list =
		 * map2.get(principal.getName()); if(list != null) { list.remove(token);
		 * } return token;
		 */
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