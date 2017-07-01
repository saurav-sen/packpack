package com.pack.pack.client.api.test;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;
import static com.pack.pack.client.api.test.TestConstants.BASE_URL_2;

import java.util.List;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.PromoteStatus;

/**
 * 
 * @author Saurav
 *
 */
public class TopicPromoteTest extends BaseTest {

	private void testPromoteTopic(TestSession session) throws Exception {
		String url = randomSelectAndPromoteTopic(session);
		System.out.println(url);
	}

	@SuppressWarnings({ "unchecked" })
	private String randomSelectAndPromoteTopicByCategory(TestSession session, String category)
			throws Exception {
		API api = APIBuilder.create(BASE_URL)
				.setAction(COMMAND.GET_USER_FOLLOWED_TOPIC_LIST)
				.setOauthToken(session.getOauthToken())
				.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE")
				.addApiParam(APIConstants.User.ID, session.getUserId())
				.addApiParam(APIConstants.Topic.CATEGORY, "music").build();
		Pagination<JTopic> page = (Pagination<JTopic>) api.execute();
		List<JTopic> result = page.getResult();
		if (!result.isEmpty()) {
			JTopic topic = result.get(0);
			api = APIBuilder.create(BASE_URL_2)
					.setAction(COMMAND.PROMOTE_TOPIC).setOauthToken(session.getOauthToken())
					.addApiParam(APIConstants.Topic.ID, topic.getId())
					.addApiParam(APIConstants.User.ID, session.getUserId()).build();
			PromoteStatus promoStatus = (PromoteStatus) api.execute();
			return promoStatus.getPublicUrl();
		}
		return null;
	}

	private String randomSelectAndPromoteTopic(TestSession session) throws Exception {
		String url = randomSelectAndPromoteTopicByCategory(session, "music");
		if (url == null) {
			url = randomSelectAndPromoteTopicByCategory(session, "photography");
		}
		return url;
	}
	
	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		testPromoteTopic(session);
	}
}