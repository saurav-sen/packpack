package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

public class TopicEditTest extends UserFollowedTopicListTest {
	
	private void editTopic(TestSession session) {
		Pagination<JTopic> p = testUserFollowedTopicList(session);
		JTopic topic = p.getResult().get(0);
		try {
			API api = APIBuilder.create(session.getBaseUrl())
					.setAction(COMMAND.EDIT_EXISTING_TOPIC)
					.setOauthToken(session.getOauthToken())
					.addApiParam(APIConstants.Topic.ID, topic.getId())
					.addApiParam(APIConstants.Topic.NAME, topic.getName() + "_abc")
					.addApiParam(APIConstants.Topic.DESCRIPTION, topic.getDescription() + "_abc")
					.addApiParam(APIConstants.Topic.OWNER_ID, session.getUserId())
					.build();
			topic = (JTopic)api.execute();
			System.out.println(topic.getId());
			System.out.println(topic.getName());
			System.out.println(topic.getDescription());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		editTopic(session);
	}
}
