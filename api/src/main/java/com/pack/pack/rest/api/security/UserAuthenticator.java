package com.pack.pack.rest.api.security;

import com.pack.pack.model.PersistedUserToken;
import com.pack.pack.oauth.registry.TokenRegistry;
import com.pack.pack.services.couchdb.PersistedUserTokenRepositoryService;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
public class UserAuthenticator {

	public static final UserAuthenticator INSTANCE = new UserAuthenticator();

	private UserAuthenticator() {
	}

	public boolean authenticateUser(String username, String password)
			throws PackPackException {
		UserRepositoryService umService = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		return umService.validateCredential(username, password);
	}
	
	public boolean IsValidRefreshToken(
			String refreshToken, String userId, String deviceID)
			throws Exception {
		if (TokenRegistry.INSTANCE.isValidRefreshToken(refreshToken, null,
				userId, deviceID)) {
			PersistedUserTokenRepositoryService service = ServiceRegistry.INSTANCE
					.findService(PersistedUserTokenRepositoryService.class);
			PersistedUserToken token = service.findByRefreshToken(refreshToken);
			service.remove(token);
			return true;
		}
		return false;
	}
}