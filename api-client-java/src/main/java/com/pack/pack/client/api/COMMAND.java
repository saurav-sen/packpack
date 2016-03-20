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
	SEARCH_USER_BY_NAME(APIConstants.User.SEARCH_USER_BY_NAME),
	GET_USER_FOLLOWED_TOPIC_LIST(APIConstants.PageInfo.PAGE_LINK, APIConstants.User.USER_ID),
	FOLLOW_TOPIC(APIConstants.User.USER_ID, APIConstants.Topic.TOPIC_ID);
	
	private String[] paramNames;
	
	private COMMAND(String... paramNames) {
		this.paramNames = paramNames;
	}

	public String[] getParamNames() {
		return paramNames;
	}
}