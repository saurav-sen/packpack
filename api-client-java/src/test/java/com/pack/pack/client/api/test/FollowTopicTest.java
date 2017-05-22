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
public class FollowTopicTest extends UserFollowedTopicListTest {

	@Override
	public void beforeTest() throws Exception {
		super.beforeTest();
	}

	public void testFollowTopic() {
		try {
			Pagination<JTopic> page = testUserFollowedTopicList();
			JTopic topic = page.getResult().get(0);
			API api = APIBuilder.create(BASE_URL).setAction(COMMAND.FOLLOW_TOPIC)
					.setOauthToken(oAuthToken)
					.addApiParam(APIConstants.User.ID, userId)
					.addApiParam(APIConstants.Topic.ID, topic.getId())
					.build();
			api.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		new FollowTopicTest().beforeTest();
	}
}