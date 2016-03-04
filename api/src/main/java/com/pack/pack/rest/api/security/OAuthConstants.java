package com.pack.pack.rest.api.security;


/**
 * 
 * @author Saurav
 *
 */
public interface OAuthConstants {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String USER_ID_HEADER = "userID";
	public static final String PATIENT_ID_HEADER = "patientID";
	
	public static final String USER_NAME_FORM_PARAM = "username";
	public static final String PASSWORD_FORM_PARAM = "password";
	public static final String DEVICE_ID_FORM_PARAM = "deviceID";
	
	public static final String DEFAULT_CLIENT_KEY = "53e8a1f2-7568-4ac8-ab26-45738ca02599";
	public static final String DEFAULT_CLIENT_SECRET = "b1f6d761-dcb7-482b-a695-ab17e4a29b25";
	
	public static final String DEFAULT_OAUTH_KEY = "default.oauth.key"; //$NON-NLS-1$
	public static final String DEFAULT_OAUTH_SECRET = "default.oauth.secret"; //$NON-NLS-1$
	
	public static final String OAUTH_REQUEST_TOKEN_PATH = "oauth/request_token";
	public static final String OAUTH_ACCESS_TOKEN_PATH = "oauth/access_token";
	public static final String OAUTH_AUTHORIZATION_PATH = "oauth/authorize";
	
	public static final String DEVICE_ID = "deviceID"; //$NON-NLS-1$
	
	public static final String DEFAULT_ROLE = "DEFAULT_ROLE"; //$NON-NLS-1$
}