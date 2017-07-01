package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;

/**
 * 
 * @author Saurav
 *
 */
public class NeglectTopicTest extends UserFollowedTopicListTest {

	private void testNeglectTopic(TestSession session) {
		try {
			Pagination<JTopic> page = testUserFollowedTopicList(session);
			JTopic topic = page.getResult().get(0);
			API api = APIBuilder.create(BASE_URL).setAction(COMMAND.FOLLOW_TOPIC)
					.setOauthToken(session.getOauthToken())
					.addApiParam(APIConstants.User.ID, session.getUserId())
					.addApiParam(APIConstants.Topic.ID, topic.getId())
					.build();
			api.execute();

			api = APIBuilder.create(BASE_URL).setAction(COMMAND.NEGLECT_TOPIC)
					.setOauthToken(session.getOauthToken())
					.addApiParam(APIConstants.Topic.ID, topic.getId())
					.addApiParam(APIConstants.User.ID, session.getUserId()).build();
			api.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		testNeglectTopic(session);
	}
}
