package com.pack.pack.rest.api.security;

import javax.ws.rs.WebApplicationException;

import com.pack.pack.rest.api.oauth.token.AccessToken;
import com.pack.pack.rest.api.oauth.token.TokenGenerator;
import com.pack.pack.rest.api.oauth.token.TokenRegistry;
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
	
	public AccessToken getAccessToken(String requestToken, String username, String password) {
		return getAccessToken(requestToken, username, password, null);
	}

	public AccessToken getAccessToken(String requestToken, String username, String password, String deviceID) {
		try {
			if(deviceID != null && !deviceID.trim().isEmpty()) {
				deviceID = deviceID.trim();
			}
			if(TokenRegistry.INSTANCE.serviceRequestToken(requestToken) != null) {
				if(!authenticateUser(username, password)) {
					System.out.println("username/password is wrong.");
					throw new WebApplicationException(401);
				}
				AccessToken token = new TokenGenerator().generateNewAccessToken();
				TokenRegistry.INSTANCE.addAccessToken(token);
				return token;
			}
			System.out.println("Request Token is not valid");
			throw new WebApplicationException(401);
		}catch (WebApplicationException we){
			throw we;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new WebApplicationException(401);
		}
	}
	
	private boolean authenticateUser(String username, String password) throws PackPackException {
		UserRepositoryService umService = ServiceRegistry.INSTANCE.findService(UserRepositoryService.class);
        return umService.validateCredential(username, password);
	}
}