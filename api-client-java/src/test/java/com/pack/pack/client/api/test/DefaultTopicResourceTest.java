package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.Pagination;
import com.squill.feed.web.model.JRssFeed;

public class DefaultTopicResourceTest extends BaseTest {

	@SuppressWarnings("unchecked")
	public void test(TestSession session) {
		try {
			API api = APIBuilder.create(session.getBaseUrl()).setAction(COMMAND.GET_ALL_REFRESHMENT_FEEDS)
					.setUserName(session.getUserName())
					.addApiParam(APIConstants.User.ID, session.getUserId())
					.addApiParam(APIConstants.PageInfo.PAGE_NO, 0)
					.build();
			Pagination<JRssFeed> page = (Pagination<JRssFeed>) api.execute();
			System.out.println(JSONUtil.serialize(page.getResult()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		test(session);
	}
	
	public static void main(String[] args) throws Exception {
		DefaultTopicResourceTest test = new DefaultTopicResourceTest();
		TestSession session = new TestSession(0, TestWorkflow.BASE_URL, TestWorkflow.BASE_URL_2);
		//String oauthToken = SignInUtil.signIn(session);
		session.setUserName(TestDataSet.getInstance().getUserEmail(session.getSeqNo()));
		JUser user = new UserInfoTest().getUserInfo(session);
		session.setUserId(user.getId());
		test.test(session);
	}
}
