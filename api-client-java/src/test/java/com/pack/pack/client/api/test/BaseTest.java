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
public abstract class BaseTest implements Test {
	
	private void beforeTest(TestSession session) throws Exception {
		String oAuthToken = SignInUtil.signIn();
		session.setOauthToken(oAuthToken);
		
		API api = APIBuilder.create(BASE_URL).setAction(COMMAND.GET_USER_BY_USERNAME)
				.setOauthToken(oAuthToken)
				.addApiParam(APIConstants.User.USERNAME, SignInUtil.USERNAME)
				.build();
		JUser user = (JUser) api.execute();
		String userId = user.getId();
		
		session.setUserId(userId);
	}
	
	@Override
	public void execute(TestSession session) throws Exception {
		if(!session.isInitialized()) {
			beforeTest(session);
		}
	}
}
