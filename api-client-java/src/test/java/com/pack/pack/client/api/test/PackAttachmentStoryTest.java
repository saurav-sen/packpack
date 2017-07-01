package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;

/**
 * 
 * @author Saurav
 *
 */
public class PackAttachmentStoryTest extends BaseTest {
	
	private void testAddStory(TestSession session) throws Exception {
		API api = APIBuilder
				.create(BASE_URL)
				.setOauthToken(session.getOauthToken())
				.setAction(COMMAND.ADD_STORY_TO_ATTACHMENT)
				.addApiParam(APIConstants.PackAttachment.ID, "838958fbd1eebc2d7f9a1950adb8de63")
				.addApiParam(APIConstants.AttachmentStory.STORY, "jfjdjk<div><br></div><div><ol><li>dddf</li><li>dd</li></ol>sddfsdf</div></div></div>")
				.build();
		api.execute();
				
	}
	

	private void testReadStory(TestSession session) throws Exception {
		API api = APIBuilder
				.create(BASE_URL)
				.setOauthToken(session.getOauthToken())
				.setAction(COMMAND.GET_STORY_FROM_ATTACHMENT)
				.addApiParam(APIConstants.PackAttachment.ID,
						"838958fbd1eebc2d7f9a1950adb8de63")
				.addApiParam(APIConstants.User.ID,
						"5f8d7b1a50cbf76d0fdbdf18a912ce36").build();
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