package com.pack.pack.client.api;

/**
 * 
 * @author Saurav
 *
 */
public enum COMMAND {

	SIGN_IN(APIConstants.Login.CLIENT_KEY, 
			APIConstants.Login.CLIENT_SECRET, 
			APIConstants.Login.USERNAME, 
			APIConstants.Login.PASSWORD),
	SIGN_UP(),
	SIGN_OUT(),
	GET_USER_BY_ID(APIConstants.User.USER_ID),
	GET_USER_BY_USERNAME(APIConstants.User.USERNAME),
	SEARCH_USER_BY_NAME(APIConstants.User.SEARCH_USER_BY_NAME);
	
	private String[] paramNames;
	
	private COMMAND(String... paramNames) {
		this.paramNames = paramNames;
	}

	public String[] getParamNames() {
		return paramNames;
	}
}