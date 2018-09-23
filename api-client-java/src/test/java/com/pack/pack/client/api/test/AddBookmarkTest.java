package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JUser;
import com.squill.feed.web.model.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public class AddBookmarkTest {

	public void test(TestSession session, String webLink) throws Exception {
		API api = APIBuilder.create(session.getBaseUrl2())
				.setAction(COMMAND.PROCESS_BOOKMARK)
				.setUserName(session.getUserName())
				.addApiParam(APIConstants.Bookmark.WEB_LINK, webLink)
				.addApiParam(APIConstants.User.USERNAME, session.getUserName())
				.build();
		JRssFeed feed = (JRssFeed) api.execute();
		System.out.println(JSONUtil.serialize(feed));
	}

	public static void main(String[] args) throws Exception {
		TestSession session = new TestSession(0, TestWorkflow.BASE_URL,
				TestWorkflow.BASE_URL_2);
		session.setUserName(session.getUserName());
		JUser user = new UserInfoTest().getUserInfo(session);
		session.setUserId(user.getId());
		String webLink = "https://phys.org/news/2018-09-japan-space-robots-asteroid-survey.html";
		new AddBookmarkTest().test(session, webLink);
	}
}
