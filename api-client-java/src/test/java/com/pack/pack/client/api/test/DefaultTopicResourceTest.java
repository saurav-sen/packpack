package com.pack.pack.client.api.test;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.Pagination;

public class DefaultTopicResourceTest extends BaseTest {

	public void test() {
		try {
			API api = APIBuilder.create(BASE_URL).setAction(COMMAND.GET_ALL_PROMOTIONAL_FEEDS)
					.setOauthToken(oAuthToken)
					.addApiParam(APIConstants.User.ID, userId)
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		DefaultTopicResourceTest test = new DefaultTopicResourceTest();
		test.beforeTest();
		test.test();
	}
}
