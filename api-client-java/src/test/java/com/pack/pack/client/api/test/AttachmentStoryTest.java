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
public class AttachmentStoryTest extends BaseTest {
	
	//private static final String ATTACHMENT_ID = "15773f7b67a9e0eac6e072faef26983e";

	private void testAddStory(TestSession session) throws Exception {
		API api = APIBuilder
				.create(session.getBaseUrl())
				.setAction(COMMAND.ADD_STORY_TO_ATTACHMENT)
				.setOauthToken(session.getOauthToken())
				.addApiParam(APIConstants.PackAttachment.ID,
						TestDataSet.getInstance().getRandomAttachmentIdFromMap(session.getSeqNo()))
				.addApiParam(
						APIConstants.AttachmentStory.STORY,
						TestDataSet.getInstance().getAttachmentStory(session.getSeqNo()))
				.build();
		String storyID = (String) api.execute();
		System.out.println("StoryID=" + storyID);
		
		api = APIBuilder
				.create(session.getBaseUrl())
				.setAction(COMMAND.GET_STORY_FROM_ATTACHMENT)
				.setOauthToken(session.getOauthToken())
				.addApiParam(APIConstants.PackAttachment.ID,
						TestDataSet.getInstance().getRandomAttachmentIdFromMap(session.getSeqNo()))
				.addApiParam(APIConstants.User.ID, session.getUserId()).build();
		String content = (String) api.execute();
		
		System.out.println(content);
	}
	
	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		testAddStory(session);
	}
	
	/*public static void main(String[] args) throws Exception {
		AttachmentStoryTest test = new AttachmentStoryTest();
		test.beforeTest();
		test.testAddStory();
	}*/
}
