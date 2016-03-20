package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JTopic;

/**
 * 
 * @author Saurav
 *
 */
public class AddTopicTest extends UserFollowedTopicListTest {

	public void beforeTest() throws Exception {
		super.beforeTest();
	}

	public void createNewTest() {
		try {
			API api = APIBuilder
					.create()
					.setAction(COMMAND.CREATE_NEW_TOPIC)
					.setOauthToken(oAuthToken)
					.addApiParam(APIConstants.Topic.OWNER_ID, userId)
					.addApiParam(APIConstants.Topic.OWNER_NAME, "Saurav Sen")
					.addApiParam(APIConstants.Topic.NAME, "Freedom of life")
					.addApiParam(APIConstants.Topic.DESCRIPTION,
							"Freedom of mind, thought & work.").build();
			JTopic topic = (JTopic) api.execute();

			api = APIBuilder.create().setAction(COMMAND.GET_TOPIC_BY_ID)
					.setOauthToken(oAuthToken)
					.addApiParam(APIConstants.Topic.TOPIC_ID, topic.getId())
					.build();
			topic = (JTopic) api.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}