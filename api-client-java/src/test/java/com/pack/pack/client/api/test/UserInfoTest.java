package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JUser;

/**
 * 
 * @author Saurav
 *
 */
public class UserInfoTest {

	private String oAuthToken;

	public void beforeTest() throws Exception {
		oAuthToken = SignInUtil.signIn();
	}

	public void testUserInfo() {
		try {
			API api = APIBuilder
					.create()
					.setAction(COMMAND.GET_USER_BY_USERNAME)
					.setOauthToken(oAuthToken)
					.addApiParam(APIConstants.User.USERNAME,
							SignInUtil.USERNAME).build();
			JUser user = (JUser) api.execute();
			String id = user.getId();
			
			api = APIBuilder.create().setAction(COMMAND.GET_USER_BY_ID)
					.setOauthToken(oAuthToken)
					.addApiParam(APIConstants.User.USER_ID, id).build();
			user = (JUser) api.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}