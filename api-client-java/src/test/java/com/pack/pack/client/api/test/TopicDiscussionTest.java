package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;


public class TopicDiscussionTest extends UserFollowedTopicListTest {

	@SuppressWarnings("unchecked")
	public void testDiscussionAdd() {
		try {
			Pagination<JTopic> page = testUserFollowedTopicList();
			JTopic topic = page.getResult().get(0);
			API api = APIBuilder.create().setAction(COMMAND.GET_ALL_DISCUSSIONS_FOR_TOPIC)
					.setOauthToken(oAuthToken)
					.addApiParam(APIConstants.Topic.ID, topic.getId())
					.addApiParam(APIConstants.User.ID, userId)
					.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE").build();
			Pagination<JDiscussion> page1 = (Pagination<JDiscussion>)api.execute();
			int count0 = page1.getResult() == null ? 0 : page1.getResult().size();
			
			api = APIBuilder.create().setAction(COMMAND.START_DISCUSSION_ON_TOPIC)
					.setOauthToken(oAuthToken)
					.addApiParam(APIConstants.User.ID, userId)
					.addApiParam(APIConstants.Topic.ID, topic.getId())
					.addApiParam(APIConstants.Discussion.TITLE, "")
					.addApiParam(APIConstants.Discussion.CONTENT, "&lt;b&gt;Ert&lt;/b&gt;&lt;div&gt;&lt;ul&gt;&lt;li&gt;&lt;i&gt;&lt;u&gt;Ty&lt;/u&gt;&lt;/i&gt;&lt;/li&gt;&lt;li&gt;Gg&lt;/li&gt;&lt;li&gt;Hj&lt;/li&gt;&lt;li&gt;G&lt;/li&gt;&lt;/ul&gt;&lt;ol&gt;&lt;li&gt;GH&lt;/li&gt;&lt;li&gt;Gg&lt;/li&gt;&lt;li&gt;Hj&lt;/li&gt;&lt;/ol&gt;Ghj&lt;/div&gt;")
					.build();
			JDiscussion discussion =(JDiscussion)api.execute();
			assert(discussion != null);

			api = APIBuilder.create().setAction(COMMAND.GET_ALL_DISCUSSIONS_FOR_TOPIC)
					.setOauthToken(oAuthToken)
					.addApiParam(APIConstants.Topic.ID, topic.getId())
					.addApiParam(APIConstants.User.ID, userId)
					.addApiParam(APIConstants.PageInfo.PAGE_LINK, null).build();
			page1 = (Pagination<JDiscussion>)api.execute();
			int count1 = page1.getResult() == null ? 0 : page1.getResult().size();
			assert(count0+1 == count1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		TopicDiscussionTest test = new TopicDiscussionTest();
		test.beforeTest();
		test.testDiscussionAdd();
	}
}
