package com.pack.pack.client.api.test;

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

	/*public static void main(String[] args) throws Exception {
		new AttachmentPromoteTest().testPromoteAttachment();
	}*/

	private void testPromoteAttachment(TestSession session) throws Exception {
		String url = randomSelectAndPromoteAttachment(session);
		System.out.println(url);
	}

	private String promoteAttachment(TestSession session, JPackAttachment attachment)
			throws Exception {
		API api = APIBuilder
				.create(session.getBaseUrl2())
				.setAction(COMMAND.PROMOTE_PACK_ATTACHMENT)
				.setOauthToken(session.getOauthToken())
				.addApiParam(APIConstants.PackAttachment.ID, attachment.getId())
				.addApiParam(APIConstants.User.ID, session.getUserId()).build();
		PromoteStatus promoStatus = (PromoteStatus) api.execute();
		return promoStatus.getPublicUrl();
	}

	@SuppressWarnings("unchecked")
	private JPackAttachment selectFirstAttachment(TestSession session, String packId, String topicId)
			throws Exception {
		JPackAttachment a = null;
		API api = APIBuilder.create(session.getBaseUrl())
				.setAction(COMMAND.GET_ALL_ATTACHMENTS_IN_PACK)
				.setOauthToken(session.getOauthToken())
				.addApiParam(APIConstants.Pack.ID, packId)
				.addApiParam(APIConstants.Topic.ID, topicId)
				.addApiParam(APIConstants.User.ID, session.getUserId())
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
	private String randomSelectAndPromoteAttachment(TestSession session, String topicId)
			throws Exception {
		API api = APIBuilder.create(session.getBaseUrl())
				.setAction(COMMAND.GET_ALL_PACKS_IN_TOPIC)
				.setOauthToken(session.getOauthToken())
				.addApiParam(APIConstants.User.ID, session.getUserId())
				.addApiParam(APIConstants.Topic.ID, topicId)
				.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE")
				.build();
		Pagination<JPack> page = (Pagination<JPack>) api.execute();
		List<JPack> result = page.getResult();
		for (JPack pack : result) {
			JPackAttachment a = selectFirstAttachment(session, pack.getId(), topicId);
			if (a != null) {
				return promoteAttachment(session, a);
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	private String randomSelectAndPromoteAttachmentByCategory(TestSession session, String category)
			throws Exception {
		API api = APIBuilder.create(session.getBaseUrl())
				.setAction(COMMAND.GET_USER_FOLLOWED_TOPIC_LIST)
				.setOauthToken(session.getOauthToken())
				.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE")
				.addApiParam(APIConstants.User.ID, session.getUserId())
				.addApiParam(APIConstants.Topic.CATEGORY, "music").build();
		Pagination<JTopic> page = (Pagination<JTopic>) api.execute();
		List<JTopic> result = page.getResult();
		for (JTopic topic : result) {
			String str = randomSelectAndPromoteAttachment(session, topic.getId());
			if (str != null) {
				return str;
			}
		}
		return null;
	}

	private String randomSelectAndPromoteAttachment(TestSession session) throws Exception {
		String url = randomSelectAndPromoteAttachmentByCategory(session, "music");
		if (url == null) {
			url = randomSelectAndPromoteAttachmentByCategory(session, "photography");
		}
		return url;
	}
	
	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		testPromoteAttachment(session);
	}
}
