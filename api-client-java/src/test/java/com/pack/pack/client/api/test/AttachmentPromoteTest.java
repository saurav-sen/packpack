package com.pack.pack.client.api.test;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;
import static com.pack.pack.client.api.test.TestConstants.BASE_URL_2;

import java.util.List;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.PromoteStatus;

/**
 * 
 * @author Saurav
 *
 */
public class AttachmentPromoteTest extends BaseTest {

	public static void main(String[] args) throws Exception {
		new AttachmentPromoteTest().testPromoteAttachment();
	}

	public void testPromoteAttachment() throws Exception {
		beforeTest();
		String url = randomSelectAndPromoteAttachment();
		System.out.println(url);
	}

	private String promoteAttachment(JPackAttachment attachment)
			throws Exception {
		API api = APIBuilder
				.create(BASE_URL_2)
				.setAction(COMMAND.PROMOTE_PACK_ATTACHMENT)
				.setOauthToken(oAuthToken)
				.addApiParam(APIConstants.PackAttachment.ID, attachment.getId())
				.addApiParam(APIConstants.User.ID, userId).build();
		PromoteStatus promoStatus = (PromoteStatus) api.execute();
		return promoStatus.getPublicUrl();
	}

	@SuppressWarnings("unchecked")
	private JPackAttachment selectFirstAttachment(String packId, String topicId)
			throws Exception {
		JPackAttachment a = null;
		API api = APIBuilder.create(BASE_URL)
				.setAction(COMMAND.GET_ALL_ATTACHMENTS_IN_PACK)
				.setOauthToken(oAuthToken)
				.addApiParam(APIConstants.Pack.ID, packId)
				.addApiParam(APIConstants.Topic.ID, topicId)
				.addApiParam(APIConstants.User.ID, userId)
				.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE")
				.build();
		Pagination<JPackAttachment> page = (Pagination<JPackAttachment>) api
				.execute();
		List<JPackAttachment> result = page.getResult();
		if (!result.isEmpty()) {
			a = result.get(0);
		}
		return a;
	}

	@SuppressWarnings("unchecked")
	private String randomSelectAndPromoteAttachment(String topicId)
			throws Exception {
		API api = APIBuilder.create(BASE_URL)
				.setAction(COMMAND.GET_ALL_PACKS_IN_TOPIC)
				.setOauthToken(oAuthToken)
				.addApiParam(APIConstants.User.ID, userId)
				.addApiParam(APIConstants.Topic.ID, topicId)
				.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE")
				.build();
		Pagination<JPack> page = (Pagination<JPack>) api.execute();
		List<JPack> result = page.getResult();
		for (JPack pack : result) {
			JPackAttachment a = selectFirstAttachment(pack.getId(), topicId);
			if (a != null) {
				return promoteAttachment(a);
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	private String randomSelectAndPromoteAttachmentByCategory(String category)
			throws Exception {
		API api = APIBuilder.create(BASE_URL)
				.setAction(COMMAND.GET_USER_FOLLOWED_TOPIC_LIST)
				.setOauthToken(oAuthToken)
				.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE")
				.addApiParam(APIConstants.User.ID, userId)
				.addApiParam(APIConstants.Topic.CATEGORY, "music").build();
		Pagination<JTopic> page = (Pagination<JTopic>) api.execute();
		List<JTopic> result = page.getResult();
		for (JTopic topic : result) {
			String str = randomSelectAndPromoteAttachment(topic.getId());
			if (str != null) {
				return str;
			}
		}
		return null;
	}

	private String randomSelectAndPromoteAttachment() throws Exception {
		String url = randomSelectAndPromoteAttachmentByCategory("music");
		if (url == null) {
			url = randomSelectAndPromoteAttachmentByCategory("photography");
		}
		return url;
	}
}
