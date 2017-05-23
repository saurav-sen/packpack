package com.pack.pack.client.api.test;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;

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
	
	private static final String ATTACHMENT_ID = "15773f7b67a9e0eac6e072faef26983e";

	private void testAddStory() throws Exception {
		API api = APIBuilder
				.create(BASE_URL)
				.setAction(COMMAND.ADD_STORY_TO_ATTACHMENT)
				.setOauthToken(oAuthToken)
				.addApiParam(APIConstants.PackAttachment.ID,
						ATTACHMENT_ID)
				.addApiParam(
						APIConstants.AttachmentStory.STORY,
						"<h4>An Unordered List:</h4><ul><li>Coffee</li><li>Tea</li><li>Milk</li></ul><br/>cx, nsj.")
				.build();
		String storyID = (String) api.execute();
		System.out.println("StoryID=" + storyID);
		
		api = APIBuilder
				.create(BASE_URL)
				.setAction(COMMAND.GET_STORY_FROM_ATTACHMENT)
				.setOauthToken(oAuthToken)
				.addApiParam(APIConstants.PackAttachment.ID,
						ATTACHMENT_ID)
				.addApiParam(APIConstants.User.ID, userId).build();
		String content = (String) api.execute();
		
		System.out.println(content);
	}
	
	public static void main(String[] args) throws Exception {
		AttachmentStoryTest test = new AttachmentStoryTest();
		test.beforeTest();
		test.testAddStory();
	}
}
