package com.pack.pack.oauth.registry;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.oauth.token.AccessToken;
import com.pack.pack.oauth.token.PersistedUserToken;
import com.pack.pack.oauth.token.Token;
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
			return cacheService.getFromCache(token, Token.class);
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
			cacheService.addToCache(token.getToken(), token, ttlSeconds);
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
		}
	}

	public Token invalidateRequestToken(String token) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			Token fromCache = cacheService.getFromCache(token, Token.class);
			if (fromCache != null) {
				cacheService.removeFromCache(token);
			}
			return fromCache;
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
			return null;
		}
	}

	public boolean isValidAccessToken(String token) {
		RedisCacheService cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		return cacheService.isKeyExists(token);
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
			cacheService.addToCache(token.getToken(), token, 2 * 60 * 60,
					token.getRefreshToken());
			Principal principal = token.getPrincipal();
			AccessTokens fromCache = cacheService.getFromCache(
					principal.getName(), AccessTokens.class);
			if (fromCache == null) {
				fromCache = new AccessTokens();
			}
			fromCache.getTokens().add(token);
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
			return fromCache != null ? fromCache.getTokens()
					: java.util.Collections.emptyList();
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
			return cacheService.getFromCache(requestToken + ":verifier",
					String.class);
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
		}
		return null;
	}

	public Token getAccessToken(String accessToken) {
		try {
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			return cacheService.getFromCache(accessToken, AccessToken.class);
		} catch (PackPackException e) {
			LOG.debug(e.getErrorCode(), e.getMessage(), e);
		}
		return null;
	}

	private class AccessTokens {
		private List<AccessToken> tokens;

		public List<AccessToken> getTokens() {
			if (tokens == null) {
				tokens = new LinkedList<AccessToken>();
			}
			return tokens;
		}

		public void setTokens(List<AccessToken> tokens) {
			this.tokens = tokens;
		}
	}
}