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

	public static void main(String[] args) throws Exception {
		new TopicPromoteTest().testPromoteTopic();
	}

	public void testPromoteTopic() throws Exception {
		beforeTest();
		String url = randomSelectAndPromoteTopic();
		System.out.println(url);
	}

	@SuppressWarnings({ "unchecked" })
	private String randomSelectAndPromoteTopicByCategory(String category)
			throws Exception {
		API api = APIBuilder.create(BASE_URL)
				.setAction(COMMAND.GET_USER_FOLLOWED_TOPIC_LIST)
				.setOauthToken(oAuthToken)
				.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE")
				.addApiParam(APIConstants.User.ID, userId)
				.addApiParam(APIConstants.Topic.CATEGORY, "music").build();
		Pagination<JTopic> page = (Pagination<JTopic>) api.execute();
		List<JTopic> result = page.getResult();
		if (!result.isEmpty()) {
			JTopic topic = result.get(0);
			api = APIBuilder.create(BASE_URL_2)
					.setAction(COMMAND.PROMOTE_TOPIC).setOauthToken(oAuthToken)
					.addApiParam(APIConstants.Topic.ID, topic.getId())
					.addApiParam(APIConstants.User.ID, userId).build();
			PromoteStatus promoStatus = (PromoteStatus) api.execute();
			return promoStatus.getPublicUrl();
		}
		return null;
	}

	private String randomSelectAndPromoteTopic() throws Exception {
		String url = randomSelectAndPromoteTopicByCategory("music");
		if (url == null) {
			url = randomSelectAndPromoteTopicByCategory("photography");
		}
		return url;
	}
}