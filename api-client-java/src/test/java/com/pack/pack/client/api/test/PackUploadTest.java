package com.pack.pack.client.api.test;

import java.io.File;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.StatusType;

public class PackUploadTest extends UserFollowedTopicListTest {

	public void uploadIMagePackTest(String topicId, String imageFilePath)
			throws Exception {
		File file = new File(imageFilePath);
		API api = APIBuilder
				.create()
				.setAction(COMMAND.UPLOAD_IMAGE_PACK)
				.setOauthToken(oAuthToken)
				.addApiParam(APIConstants.User.ID, userId)
				.addApiParam(APIConstants.Topic.ID, topicId)
				.addApiParam(APIConstants.Pack.TITLE, "Shantineketan")
				.addApiParam(APIConstants.Pack.DESCRIPTION,
						"Sample Theme of Shantineketan")
				.addApiParam(
						APIConstants.Pack.STORY,
						"On the way. Where we stopped by advasi villages. Gramin beauty of nature at it best.")
				.addApiParam(APIConstants.Attachment.FILE_ATTACHMENT, file)
				.build();
		JStatus status = (JStatus) api.execute();
		assert (status.getStatus() == StatusType.OK);
	}

	@SuppressWarnings("unchecked")
	public Pagination<JPack> testGetAllPacksInTopic(String topicId)
			throws Exception {
		API api = APIBuilder.create().setAction(COMMAND.GET_ALL_PACKS_IN_TOPIC)
				.setOauthToken(oAuthToken)
				.addApiParam(APIConstants.User.ID, userId)
				.addApiParam(APIConstants.Topic.ID, topicId)
				.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE")
				.build();
		Pagination<JPack> page = (Pagination<JPack>) api.execute();
		return page;
	}

	public JPack testAddImageToPack(String topicId, String packId,
			String imageFilePath) throws Exception {
		File file = new File(imageFilePath);
		API api = APIBuilder.create().setAction(COMMAND.ADD_IMAGE_TO_PACK)
				.setOauthToken(oAuthToken)
				.addApiParam(APIConstants.Topic.ID, topicId)
				.addApiParam(APIConstants.Pack.ID, packId)
				.addApiParam(APIConstants.User.ID, userId)
				.addApiParam(APIConstants.Attachment.FILE_ATTACHMENT, file)
				.build();
		return (JPack) api.execute();
	}
}
