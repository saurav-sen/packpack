package com.pack.pack.rest.api.security;

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
}