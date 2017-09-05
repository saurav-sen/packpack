package com.pack.pack.client.api.test;

import java.util.List;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

/**
 * 
 * @author Saurav
 *
 */
public class TopicSharedFeedsTest extends UserFollowedTopicListTest {

	@SuppressWarnings("unchecked")
	public void testListSharedFeeds(TestSession session) {
		try {
			Pagination<JTopic> page = testUserFollowedTopicList(session);
			List<JTopic> result = page.getResult();
			for(JTopic topic : result) {
				API api = APIBuilder.create(session.getBaseUrl()).setAction(COMMAND.GET_ALL_SHARED_FEEDS_TO_TOPIC)
						.setOauthToken(session.getOauthToken())
						.addApiParam(APIConstants.Topic.ID, topic.getId())
						.addApiParam(APIConstants.User.ID, session.getUserId())
						.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE").build();
				Pagination<JPackAttachment> page1 = (Pagination<JPackAttachment>)api.execute();
				List<JPackAttachment> result2 = page1.getResult();
				for(JPackAttachment r : result2) {
					String json = JSONUtil.serialize(r);
					System.out.println(json);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		testListSharedFeeds(session);
	}
}
