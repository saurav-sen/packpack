package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.oauth1.client.AccessToken;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;

public class SignInUtil {

	private static final String ANDROID_APP_CLIENT_KEY = "53e8a1f2-7568-4ac8-ab26-45738ca02599";
	private static final String ANDROID_APP_CLIENT_SECRET = "b1f6d761-dcb7-482b-a695-ab17e4a29b25";

	public static final String USERNAME = "sourabhnits@gmail.com";
	public static final String PASSWORD = "password";

	public static String signIn() throws Exception {
		API api = APIBuilder
				.create(BASE_URL)
				.setAction(COMMAND.SIGN_IN)
				.addApiParam(APIConstants.Login.CLIENT_KEY,
						ANDROID_APP_CLIENT_KEY)
				.addApiParam(APIConstants.Login.CLIENT_SECRET,
						ANDROID_APP_CLIENT_SECRET)
				.addApiParam(APIConstants.Login.USERNAME, USERNAME)
				.addApiParam(APIConstants.Login.PASSWORD, PASSWORD).build();
		AccessToken oAuthToken = (AccessToken) api.execute();
		System.out.println(oAuthToken.getToken());
		api = APIBuilder
				.create(BASE_URL)
				.setAction(COMMAND.SIGN_IN)
				.addApiParam(APIConstants.Login.CLIENT_KEY,
						ANDROID_APP_CLIENT_KEY)
				.addApiParam(APIConstants.Login.CLIENT_SECRET,
						ANDROID_APP_CLIENT_SECRET)
				.addApiParam(APIConstants.Login.OLD_ACCESS_TOKEN,
						oAuthToken.getToken())
				.addApiParam(APIConstants.Login.OLD_ACCESS_TOKEN_SECRET,
						oAuthToken.getTokenSecret()).build();
		oAuthToken = (AccessToken) api.execute();
		System.out.println(oAuthToken.getToken());
		return oAuthToken.getToken();
	}
}
