package com.pack.pack.client.api.test;

import java.io.File;

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

	public JTopic createNewTopic(TestSession session) {
		try {
			API api = APIBuilder
					.create(session.getBaseUrl())
					.setAction(COMMAND.CREATE_NEW_TOPIC)
					.setOauthToken(session.getOauthToken())
					.addApiParam(APIConstants.Topic.OWNER_ID, session.getUserId())
					.addApiParam(APIConstants.Topic.NAME, TestDataSet.getInstance().randomNewTopicTitle())
					.addApiParam(APIConstants.Topic.DESCRIPTION,
							TestDataSet.getInstance().randomNewTopicDescription())
					.addApiParam(APIConstants.Topic.CATEGORY, TestDataSet.getInstance().randomNewTopicCategory())
					.addApiParam(APIConstants.Topic.WALLPAPER,
							new File(TestDataSet.getInstance().randomNewTopicWallpaperFilePath())).build();
			JTopic topic = (JTopic) api.execute();

			api = APIBuilder.create(session.getBaseUrl()).setAction(COMMAND.GET_TOPIC_BY_ID)
					.setOauthToken(session.getOauthToken())
					.addApiParam(APIConstants.Topic.ID, topic.getId()).build();
			topic = (JTopic) api.execute();
			
			return topic;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		JTopic topic = createNewTopic(session);
		TestDataSet.getInstance().addToTopicsMap(session.getSeqNo(), topic);
	}
}