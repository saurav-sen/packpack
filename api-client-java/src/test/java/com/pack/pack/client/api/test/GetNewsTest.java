package com.pack.pack.client.api.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.PageUtil;
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
	public int test(TestSession session, COMMAND command) {
		int total = 0;
		Set<JRssFeed> set = new HashSet<JRssFeed>();
		try {
			API api = APIBuilder.create(session.getBaseUrl()).setAction(command)
					.setUserName(session.getUserName())
					.addApiParam(APIConstants.User.ID, session.getUserId())
					.addApiParam(APIConstants.PageInfo.PAGE_LINK, PageUtil.buildNextPageLink(0)) /*PageUtil.buildNextPageLink(0))*/
					.build();
			Pagination<JRssFeed> page = (Pagination<JRssFeed>) api.execute();
			total = total + page.getResult().size();
			//System.out.println(JSONUtil.serialize(page.getResult()));
			Set<String> nextLinksSet = new HashSet<String>();
			int count = 0;
			while (!page.getResult().isEmpty()) {
				List<JRssFeed> result = page.getResult();
				Map<String, List<JRssFeed>> map = new HashMap<String, List<JRssFeed>>();
				for(JRssFeed r : result) {
					if(!set.add(r)) {
						List<JRssFeed> list = map.get(r.getOgUrl());
						if(list == null) {
							list = new ArrayList<JRssFeed>();
							map.put(r.getOgUrl(), list);
						}
						list.add(r);
						System.out.println("[FAILED]:: Duplicate Feed Received.");
					}
				}
				
				/*if(!map.isEmpty()) {
					Iterator<String> itr1 = map.keySet().iterator();
					while(itr1.hasNext()) {
						String id = itr1.next();
						System.out.println(id);
						List<JRssFeed> list = map.get(id);
						System.out.println(JSONUtil.serialize(list));
					}
				}*/
				map.clear();
				count++;
				total = total + page.getResult().size();
				//System.out.println(JSONUtil.serialize(page.getResult()));
				if(!nextLinksSet.add(PageUtil.buildNextPageLink(page.getTimestamp()))) {
					System.out.println("Duplicate Links");
				}
				System.out.println("Next --> " + PageUtil.buildNextPageLink(page.getTimestamp()));
				System.out.println("*****************************************");
				api = APIBuilder
						.create(session.getBaseUrl())
						.setAction(command)
						.setUserName(session.getUserName())
						.addApiParam(APIConstants.User.ID, session.getUserId())
						.addApiParam(APIConstants.PageInfo.PAGE_LINK,
								PageUtil.buildNextPageLink(page.getTimestamp())).build();
				page = (Pagination<JRssFeed>) api.execute();
			}
			
			System.out.println("Total Number Of Pages = " + count);
			System.out.println("Total Number of Feeds for " + command.name() + " = " + total);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return total;
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
		//new SignUpUserTest().signUp(session);
		//String oauthToken = SignInUtil.signIn(session);
		session.setUserName(session.getUserName());
		JUser user = new UserInfoTest().getUserInfo(session);
		session.setUserId(user.getId());
		int total = test.test(session, COMMAND.GET_ALL_NEWS_FEEDS);
		total = total + test.test(session, COMMAND.GET_ALL_SPORTS_NEWS_FEEDS);
		total = total + test.test(session, COMMAND.GET_ALL_SCIENCE_AND_TECHNOLOGY_NEWS_FEEDS);
		total = total + test.test(session, COMMAND.GET_ALL_ARTICLES_FEEDS);
		System.out.println("Grand Total = " + total);
	}
}