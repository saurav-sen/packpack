package com.pack.pack.client.api.test;

import com.pack.pack.model.web.JUser;

/**
 * 
 * @author Saurav
 *
 */
public class TestSessionExecutor {

	public void execute(TestSession session) throws Exception {
		new SignUpUserTest().signUp(session);
		String oAuthToken = new SignInUserTest().signIn(session);
		session.setOauthToken(oAuthToken);
		JUser user = new UserInfoTest().getUserInfo(session);
		session.setUserId(user.getId());
		new DefaultTopicResourceTest().execute(session);
		new AddTopicTest().execute(session);
		new PackUploadTest().execute(session);
		new PackAttachmentStoryTest().execute(session);
	}
}
