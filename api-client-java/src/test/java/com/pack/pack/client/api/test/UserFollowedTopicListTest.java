package com.pack.pack.client.api.test;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;

import java.util.List;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

/**
 * 
 * @author Saurav
 *
 */
public class UserFollowedTopicListTest extends BaseTest {
	
	@SuppressWarnings({ "unchecked" })
	public Pagination<JTopic> testUserFollowedTopicList() {
		Pagination<JTopic> page = null;
		try {
			API api = APIBuilder.create(BASE_URL)
					.setAction(COMMAND.GET_USER_FOLLOWED_TOPIC_LIST)
					.setOauthToken(oAuthToken)
					.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE")
					.addApiParam(APIConstants.User.ID, userId)
					.addApiParam(APIConstants.Topic.CATEGORY, "photography")
					.build();
			page = (Pagination<JTopic>)api.execute();
			List<JTopic> result = page.getResult();
			for(JTopic r : result) {
				String json = JSONUtil.serialize(r);
				System.out.println(json);
				//System.out.println(r.getName() + "::" + r.getCategory() + "::" + r.getDescription());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
}