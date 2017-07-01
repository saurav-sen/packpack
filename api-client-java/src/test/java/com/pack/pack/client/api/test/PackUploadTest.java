package com.pack.pack.client.api.test;

import java.io.File;
import java.util.List;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.StatusType;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;;

public class PackUploadTest extends UserFollowedTopicListTest {

	private void uploadIMagePackTest(TestSession session, String topicId, String imageFilePath)
			throws Exception {
		File file = new File(imageFilePath);
		API api = APIBuilder
				.create(BASE_URL)
				.setAction(COMMAND.UPLOAD_IMAGE_PACK)
				.setOauthToken(session.getOauthToken())
				.addApiParam(APIConstants.User.ID, session.getUserId())
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
	private Pagination<JPack> testGetAllPacksInTopic(TestSession session, String topicId)
			throws Exception {
		API api = APIBuilder.create(BASE_URL).setAction(COMMAND.GET_ALL_PACKS_IN_TOPIC)
				.setOauthToken(session.getOauthToken())
				.addApiParam(APIConstants.User.ID, session.getUserId())
				.addApiParam(APIConstants.Topic.ID, topicId)
				.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE")
				.build();
		Pagination<JPack> page = (Pagination<JPack>) api.execute();
		return page;
	}

	private JPack testAddImageToPack(TestSession session, String topicId, String packId,
			String imageFilePath) throws Exception {
		File file = new File(imageFilePath);
		API api = APIBuilder.create(BASE_URL).setAction(COMMAND.ADD_IMAGE_TO_PACK)
				.setOauthToken(session.getOauthToken())
				.addApiParam(APIConstants.Topic.ID, topicId)
				.addApiParam(APIConstants.Pack.ID, packId)
				.addApiParam(APIConstants.User.ID, session.getUserId())
				.addApiParam(APIConstants.Attachment.FILE_ATTACHMENT, file)
				.build();
		return (JPack) api.execute();
	}
	
	private JPack testAddVideoToPack(TestSession session, String topicId, String packId,
			String videoFilePath) throws Exception {
		File file = new File(videoFilePath);
		API api = APIBuilder.create(BASE_URL).setAction(COMMAND.ADD_VIDEO_TO_PACK)
				.setOauthToken(session.getOauthToken())
				.addApiParam(APIConstants.Topic.ID, topicId)
				.addApiParam(APIConstants.Pack.ID, packId)
				.addApiParam(APIConstants.User.ID, session.getUserId())
				.addApiParam(APIConstants.Attachment.FILE_ATTACHMENT, file)
				.build();
		return (JPack) api.execute();
	}

	@SuppressWarnings("unchecked")
	private Pagination<JPackAttachment> getAllPackAttachments(TestSession session, String topicId,
			String packId, String pageLink) throws Exception {
		API api = APIBuilder.create(BASE_URL)
				.setAction(COMMAND.GET_ALL_ATTACHMENTS_IN_PACK)
				.setOauthToken(session.getOauthToken())
				.addApiParam(APIConstants.Pack.ID, packId)
				.addApiParam(APIConstants.Topic.ID, topicId)
				.addApiParam(APIConstants.User.ID, session.getUserId())
				.addApiParam(APIConstants.PageInfo.PAGE_LINK, pageLink).build();
		return (Pagination<JPackAttachment>) api.execute();
	}
	
	private void addImageToPack(TestSession session) throws Exception {
		Pagination<JTopic> topicList = testUserFollowedTopicList(session);
		String imageFilePath = "D:/Saurav/Images_Shantineketan/678_1.JPG";
		if (topicList != null) {
			List<JTopic> result = topicList.getResult();
			if (result == null || result.isEmpty())
				return;
			int count = 0;
			for (JTopic r : result) {
				if (count > 1)
					break;
				String topicId = r.getId();
				Pagination<JPack> page = testGetAllPacksInTopic(session, topicId);
				if(page == null)
					continue;
				List<JPack> packs = page.getResult();
				if(packs == null || packs.isEmpty())
					continue;
				System.out.println(JSONUtil.serialize(testAddImageToPack(session, 
						topicId, packs.get(0).getId(), imageFilePath)));
				break;
			}
		}
	}
	
	private void addVideoToPack(TestSession session) throws Exception {
		Pagination<JTopic> topicList = testUserFollowedTopicList(session);
		String videoFilePath = "D:/Saurav/VM/packpack/65547c86-b2ba-448f-8f32-4206a7d49376.mp4";//"D:/Saurav/Lord_Ganesh.mp4";
		if (topicList != null) {
			List<JTopic> result = topicList.getResult();
			if (result == null || result.isEmpty())
				return;
			int count = 0;
			for (JTopic r : result) {
				if (count > 1)
					break;
				String topicId = r.getId();
				Pagination<JPack> page = testGetAllPacksInTopic(session, topicId);
				if(page == null)
					continue;
				List<JPack> packs = page.getResult();
				if(packs == null || packs.isEmpty())
					continue;
				System.out.println(JSONUtil.serialize(testAddVideoToPack(session, 
						topicId, packs.get(0).getId(), videoFilePath)));
				break;
			}
		}
	}
	
	private void uploadPackTest(TestSession session, boolean uploadNew) throws Exception {
		Pagination<JTopic> topicList = testUserFollowedTopicList(session);
		String[] files = new String[] {"D:/Saurav/Images_Shantineketan/123.JPG", "D:/Saurav/Images_Shantineketan/456.JPG"};
		if(topicList != null) {
			List<JTopic> result = topicList.getResult();
			if(result == null || result.isEmpty())
				return;
			int count = 0;
			for(JTopic r : result) {
				/*if(count > 1)
					break;*/
				String topicId = r.getId();
				if(uploadNew) {
					uploadIMagePackTest(session, topicId, files[count]);
				}
				System.out.println("*******************************************************");
				Pagination<JPack> page = testGetAllPacksInTopic(session, topicId);
				assert (page != null);
				assert (page.getResult() != null && !page.getResult().isEmpty());
				List<JPack> packs = page.getResult();
				assert (packs != null);
				for(JPack pack : packs) {
					String json = JSONUtil.serialize(pack);
					System.out.println(json);
					Pagination<JPackAttachment> page1 = getAllPackAttachments(session, topicId, pack.getId(), "FIRST_PAGE");
					if(page1 != null) {
						List<JPackAttachment> attachments = page1.getResult();
						if(attachments == null || attachments.isEmpty())
							continue;
						for(JPackAttachment attachment : attachments) {
							System.out.println(JSONUtil.serialize(attachment));
						}
					}
				}
				count++;
			}
		}
	}
	
	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		uploadPackTest(session, true);
	}
}