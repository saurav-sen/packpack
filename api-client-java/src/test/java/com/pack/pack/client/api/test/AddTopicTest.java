package com.pack.pack.client.api.test;

import java.io.File;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JTopic;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;

/**
 * 
 * @author Saurav
 *
 */
public class AddTopicTest extends UserFollowedTopicListTest {

	private static final String TOPIC_WALLPAPER = "D:/Saurav/Freedom.jpg";

	public void createNewTopic(TestSession session) {
		try {
			API api = APIBuilder
					.create(BASE_URL)
					.setAction(COMMAND.CREATE_NEW_TOPIC)
					.setOauthToken(session.getOauthToken())
					.addApiParam(APIConstants.Topic.OWNER_ID, session.getUserId())
					.addApiParam(APIConstants.Topic.NAME, "Travel Share")
					.addApiParam(APIConstants.Topic.DESCRIPTION,
							"Some of our work from travel last year.")
					.addApiParam(APIConstants.Topic.CATEGORY, "lifestyle")
					.addApiParam(APIConstants.Topic.WALLPAPER,
							new File(TOPIC_WALLPAPER)).build();
			JTopic topic = (JTopic) api.execute();

			api = APIBuilder.create(BASE_URL).setAction(COMMAND.GET_TOPIC_BY_ID)
					.setOauthToken(session.getOauthToken())
					.addApiParam(APIConstants.Topic.ID, topic.getId()).build();
			topic = (JTopic) api.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		createNewTopic(session);
	}
}