package com.pack.pack.client.api.test;

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
		//String oAuthToken = SignInUtil.signIn(session);
		String userName = TestDataSet.getInstance().getUserEmail(session.getSeqNo());
		session.setUserName(userName);
		
		API api = APIBuilder.create(session.getBaseUrl()).setAction(COMMAND.GET_USER_BY_USERNAME)
				.setUserName(userName)
				.addApiParam(APIConstants.User.USERNAME, TestDataSet.getInstance().getUserEmail(session.getSeqNo()))
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
