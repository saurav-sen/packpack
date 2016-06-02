package com.pack.pack.client.api.test;

import java.util.List;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;



public class Main {
	
	//private static final String ANDROID_APP_CLIENT_KEY = "53e8a1f2-7568-4ac8-ab26-45738ca02599";
	//private static final String ANDROID_APP_CLIENT_SECRET = "b1f6d761-dcb7-482b-a695-ab17e4a29b25";
	
	public static final String USERNAME = "sourabhnits@gmail.com";
	public static final String PASSWORD = "P@ckp@K#123";
	
	public static void main(String[] args) throws Exception {
		//new SignUpUserTest().signUp();
		//addTopicTest();
		//testUserFOllowedTopicList();
		uploadPackTest(false);
		//addImageToPack();
	}
	
	private static void addImageToPack() throws Exception {
		PackUploadTest test = new PackUploadTest();
		test.beforeTest();
		Pagination<JTopic> topicList = test.testUserFollowedTopicList();
		String imageFilePath = "D:/Saurav/Images_Shantineketan/678_1.JPG";
		if (topicList != null) {
			List<JTopic> result = topicList.getResult();
			if (result == null || result.isEmpty())
				return;
			int count = 0;
			for (JTopic r : result) {
				if (count > 1)
					break;
				String topicId = r.getId();
				Pagination<JPack> page = test.testGetAllPacksInTopic(topicId);
				if(page == null)
					continue;
				List<JPack> packs = page.getResult();
				if(packs == null || packs.isEmpty())
					continue;
				System.out.println(JSONUtil.serialize(test.testAddImageToPack(
						topicId, packs.get(0).getId(), imageFilePath)));
				break;
			}
		}
	}
	
	private static void uploadPackTest(boolean uploadNew) throws Exception {
		PackUploadTest test = new PackUploadTest();
		test.beforeTest();
		Pagination<JTopic> topicList = test.testUserFollowedTopicList();
		String[] files = new String[] {"D:/Saurav/Images_Shantineketan/123.JPG", "D:/Saurav/Images_Shantineketan/456.JPG"};
		if(topicList != null) {
			List<JTopic> result = topicList.getResult();
			if(result == null || result.isEmpty())
				return;
			int count = 0;
			for(JTopic r : result) {
				if(count > 1)
					break;
				String topicId = r.getId();
				if(uploadNew) {
					test.uploadIMagePackTest(topicId, files[count]);
				}
				System.out.println("*******************************************************");
				Pagination<JPack> page = test.testGetAllPacksInTopic(topicId);
				assert (page != null);
				assert (page.getResult() != null && !page.getResult().isEmpty());
				List<JPack> packs = page.getResult();
				assert (packs != null);
				for(JPack pack : packs) {
					String json = JSONUtil.serialize(pack);
					System.out.println(json);
					Pagination<JPackAttachment> page1 = test.getAllPackAttachments(topicId, pack.getId(), "FIRST_PAGE");
					if(page1 != null) {
						List<JPackAttachment> attachments = page1.getResult();
						if(attachments == null || attachments.isEmpty())
							continue;
						for(JPackAttachment attachment : attachments) {
							System.out.println(JSONUtil.serialize(attachment));
						}
					}
				}
				count++;
			}
		}
	}
	
	private static Pagination<JTopic> testUserFOllowedTopicList() throws Exception {
		UserFollowedTopicListTest userFollowedTopicListTest = new UserFollowedTopicListTest();
		userFollowedTopicListTest.beforeTest();
		return userFollowedTopicListTest.testUserFollowedTopicList();
	}
	
	private static void addTopicTest() throws Exception {
		AddTopicTest addTopicTest = new AddTopicTest();
		addTopicTest.beforeTest();
		addTopicTest.createNewTopic();
	}

	public static void main1(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*SignUpUserTest signUpUserTest = new SignUpUserTest();
		signUpUserTest.signUp();*/
		
		UserFollowedTopicListTest userFollowedTopicListTest = new UserFollowedTopicListTest();
		userFollowedTopicListTest.beforeTest();
		userFollowedTopicListTest.testUserFollowedTopicList();
		//addTopicTest.createNewTopic();
		
		/*OAuth1ClientCredentials consumerCredentials = new OAuth1ClientCredentials(
				ANDROID_APP_CLIENT_KEY, ANDROID_APP_CLIENT_SECRET);
		OAuth1RequestFlow authFlow = OAuth1Support.builder(consumerCredentials,
				BASE_URL).build();
		String authorizationUri = authFlow.start();
		System.out.println(authorizationUri);

		String query = URI.create(authorizationUri).getQuery();
		int index = query.indexOf("oauth_token=");
		String requestToken = query.substring(index + "oauth_token=".length());
		System.out.println("Request Token: " + requestToken);
		
		String verifier = authFlow.authorize(requestToken, USERNAME, PASSWORD);
		System.out.println("Verifier: " + verifier);
		
		AccessToken accessToken = authFlow.finish(requestToken, verifier);
		System.out.println("Access Token: " + accessToken.getToken());*/
	}

}
