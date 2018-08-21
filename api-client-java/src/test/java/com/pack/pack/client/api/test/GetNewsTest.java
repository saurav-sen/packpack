package com.pack.pack.client.api.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.PageUtil;
import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.Pagination;
import com.squill.feed.web.model.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public class GetNewsTest extends BaseTest {

	@SuppressWarnings("unchecked")
	public void test(TestSession session, COMMAND command) {
		Set<JRssFeed> set = new HashSet<JRssFeed>();
		Set<Long> set1 = new HashSet<Long>();
		try {
			API api = APIBuilder.create(session.getBaseUrl()).setAction(command)
					.setUserName(session.getUserName())
					.addApiParam(APIConstants.User.ID, session.getUserId())
					/*.addApiParam(APIConstants.PageInfo.PAGE_LINK, CommonConstants.NULL_PAGE_LINK)*/
					.addApiParam(APIConstants.PageInfo.PAGE_LINK, PageUtil.buildNextPageLink(1532849367789L)) /*PageUtil.buildNextPageLink(0))*/
					.build();
			Pagination<JRssFeed> page = (Pagination<JRssFeed>) api.execute();
			/*List<JRssFeed> result = page.getResult();
			Collections.sort(result, new Comparator<JRssFeed>() 
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
			int count = 0;
			while (!page.getResult().isEmpty()) {
				long timestamp = page.getTimestamp();
				if(!set1.add(timestamp)) {
					System.err.println("[FAILED]:: Duplicate Timestamp.");
				}
				List<JRssFeed> result = page.getResult();
				for(JRssFeed r : result) {
					if(!set.add(r)) {
						System.err.println("[FAILED]:: Duplicate Feed Received.");
					}
				}
				count++;
				//System.out.println("Previous --> " + page.getPreviousLink());
				System.out.println(JSONUtil.serialize(page.getResult()));
				//System.out.println("Next --> " + page.getNextLink());
				System.out.println("Next --> " + PageUtil.buildNextPageLink(page.getTimestamp()));
				System.out.println("*****************************************");
				api = APIBuilder
						.create(session.getBaseUrl())
						.setAction(command)
						.setUserName(session.getUserName())
						.addApiParam(APIConstants.User.ID, session.getUserId())
						.addApiParam(APIConstants.PageInfo.PAGE_LINK,
								PageUtil.buildNextPageLink(page.getTimestamp())).build();
				/*api = APIBuilder
						.create(session.getBaseUrl())
						.setAction(command)
						.setOauthToken(session.getOauthToken())
						.addApiParam(APIConstants.User.ID, session.getUserId())
						.addApiParam(APIConstants.PageInfo.PAGE_LINK,
								page.getNextLink()).build();*/
				page = (Pagination<JRssFeed>) api.execute();
			}
			
			System.out.println("Total Number Of Pages = " + count);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void execute(TestSession session) throws Exception {
		super.execute(session);
		test(session, COMMAND.GET_ALL_NEWS_FEEDS);
		test(session, COMMAND.GET_ALL_SPORTS_NEWS_FEEDS);
		test(session, COMMAND.GET_ALL_SCIENCE_AND_TECHNOLOGY_NEWS_FEEDS);
		test(session, COMMAND.GET_ALL_ARTICLES_FEEDS);
	}
	
	public static void main(String[] args) throws Exception {
		GetNewsTest test = new GetNewsTest();
		TestSession session = new TestSession(0, TestWorkflow.BASE_URL, TestWorkflow.BASE_URL_2);
		new SignUpUserTest().signUp(session);
		//String oauthToken = SignInUtil.signIn(session);
		session.setUserName(session.getUserName());
		JUser user = new UserInfoTest().getUserInfo(session);
		session.setUserId(user.getId());
		/*test.test(session, COMMAND.GET_ALL_NEWS_FEEDS);
		test.test(session, COMMAND.GET_ALL_SPORTS_NEWS_FEEDS);
		test.test(session, COMMAND.GET_ALL_SCIENCE_AND_TECHNOLOGY_NEWS_FEEDS);*/
		test.test(session, COMMAND.GET_ALL_ARTICLES_FEEDS);
	}
}