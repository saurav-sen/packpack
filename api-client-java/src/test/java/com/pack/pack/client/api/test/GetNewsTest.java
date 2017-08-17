package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.Pagination;

/**
 * 
 * @author Saurav
 *
 */
public class GetNewsTest extends BaseTest {

	public void test(TestSession session) {
		try {
			API api = APIBuilder.create(session.getBaseUrl()).setAction(COMMAND.GET_ALL_NEWS_FEEDS)
					.setOauthToken(session.getOauthToken())
					.addApiParam(APIConstants.User.ID, session.getUserId())
					.addApiParam(APIConstants.PageInfo.PAGE_LINK, "FIRST_PAGE")
					.build();
			Pagination<JRssFeed> page = (Pagination<JRssFeed>) api.execute();
			/*List<JRssFeed> result = page.getResult();
			Collections.sort(result, new Comparator<JRssFeed>() {
				public int compare(JRssFeed o1, JRssFeed o2) {
					long l = Long.parseLong(o2.getId()) - Long.parseLong(o1.getId());
					if(l == 0) {
						return 0;
					}
					if(l > 0) {
						return 1;
					}
					return -1;
				};
			});*/
			System.out.println(JSONUtil.serialize(page.getResult()));
			System.out.println("*****************************************");
			while (!page.getResult().isEmpty()) {
				api = APIBuilder
						.create(session.getBaseUrl())
						.setAction(COMMAND.GET_ALL_NEWS_FEEDS)
						.setOauthToken(session.getOauthToken())
						.addApiParam(APIConstants.User.ID, session.getUserId())
						.addApiParam(APIConstants.PageInfo.PAGE_LINK,
								page.getNextLink()).build();
				page = (Pagination<JRssFeed>) api.execute();
				System.out.println(JSONUtil.serialize(page.getResult()));
			}
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
		GetNewsTest test = new GetNewsTest();
		TestSession session = new TestSession(0, TestWorkflow.BASE_URL, TestWorkflow.BASE_URL_2);
		String oauthToken = SignInUtil.signIn(session);
		session.setOauthToken(oauthToken);
		JUser user = new UserInfoTest().getUserInfo(session);
		session.setUserId(user.getId());
		test.test(session);
	}
}