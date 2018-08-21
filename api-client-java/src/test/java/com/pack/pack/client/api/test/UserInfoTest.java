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

	//private String oAuthToken;

	/*public void beforeTest() throws Exception {
		oAuthToken = SignInUtil.signIn();
	}*/

	public JUser getUserInfo(TestSession session) {
		try {
			API api = APIBuilder
					.create(session.getBaseUrl())
					.setAction(COMMAND.GET_USER_BY_USERNAME)
					.setUserName(session.getUserName())
					.addApiParam(APIConstants.User.USERNAME,
							TestDataSet.getInstance().getUserEmail(session.getSeqNo())).build();
			JUser user = (JUser) api.execute();
			String id = user.getId();
			
			api = APIBuilder.create(session.getBaseUrl()).setAction(COMMAND.GET_USER_BY_ID)
					.setUserName(session.getUserName())
					.addApiParam(APIConstants.User.ID, id).build();
			return (JUser) api.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}