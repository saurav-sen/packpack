package com.pack.pack.client.api.test;

import java.util.List;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

/**
 * 
 * @author Saurav
 *
 */
public class FollowTopicTest extends UserFollowedTopicListTest {

	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		test(session);
	}
	
	public void test(TestSession session) throws Exception {
		Pagination<JTopic> pagination = testUserFollowedTopicList(session);
		List<JTopic> result = pagination.getResult();
		for(JTopic t : result) {
			System.out.println(JSONUtil.serialize(t));
		}
	}
}