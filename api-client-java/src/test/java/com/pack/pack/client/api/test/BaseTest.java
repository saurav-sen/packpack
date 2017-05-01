package com.pack.pack.client.api.test;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JUser;

/**
 * 
 * @author CipherCloud
 *
 */
public class BaseTest {
	
	protected String oAuthToken;

	protected String userId;

	public void beforeTest() throws Exception {
		oAuthToken = SignInUtil.signIn();
		API api = APIBuilder.create(BASE_URL).setAction(COMMAND.GET_USER_BY_USERNAME)
				.setOauthToken(oAuthToken)
				.addApiParam(APIConstants.User.USERNAME, SignInUtil.USERNAME)
				.build();
		JUser user = (JUser) api.execute();
		userId = user.getId();
	}
}
