package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;

/**
 * 
 * @author Saurav
 *
 */
public class PackAttachmentStoryTest extends BaseTest {
	
	private void testAddStory(TestSession session) throws Exception {
		API api = APIBuilder
				.create(session.getBaseUrl())
				.setOauthToken(session.getOauthToken())
				.setAction(COMMAND.ADD_STORY_TO_ATTACHMENT)
				.addApiParam(APIConstants.PackAttachment.ID, TestDataSet.getInstance().getRandomAttachmentIdFromMap(session.getSeqNo()))
				.addApiParam(APIConstants.AttachmentStory.STORY, TestDataSet.getInstance().getAttachmentStory(session.getSeqNo()))
				.build();
		api.execute();
				
	}
	

	private void testReadStory(TestSession session) throws Exception {
		API api = APIBuilder
				.create(session.getBaseUrl())
				.setOauthToken(session.getOauthToken())
				.setAction(COMMAND.GET_STORY_FROM_ATTACHMENT)
				.addApiParam(APIConstants.PackAttachment.ID,
						TestDataSet.getInstance().getRandomAttachmentIdFromMap(session.getSeqNo()))
				.addApiParam(APIConstants.User.ID,
						session.getUserId()).build();
		String story = (String) api.execute();
		System.out.println("Story = " + story);
	}
	
	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		testAddStory(session);
		testReadStory(session);
	}
}