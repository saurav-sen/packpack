package com.pack.pack.test.util;

import java.util.ArrayList;
import java.util.List;

import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.model.Topic;
import com.pack.pack.model.UserTopicMap;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.couchdb.TopicRepositoryService;
import com.pack.pack.services.couchdb.UserTopicMapRepositoryService;
import com.pack.pack.services.registry.ServiceRegistry;

public class ChangeExistingTopicCategories {

	public static void main(String[] args) throws Exception {
		System.setProperty("service.registry.test", "true");
		ServiceRegistry.INSTANCE.init();
		
		String[] categories = new String[] { CommonConstants.HOME,
				CommonConstants.LIFESTYLE, CommonConstants.ART,
				CommonConstants.PHOTOGRAPHY, CommonConstants.MUSIC,
				CommonConstants.EDUCATION, CommonConstants.FUN,
				CommonConstants.SPIRITUAL, CommonConstants.OTHERS };
		TopicRepositoryService service = ServiceRegistry.INSTANCE.findService(TopicRepositoryService.class);
		List<Topic> topics = new ArrayList<Topic>();//.getAll();
		for(String c : categories) {
			Pagination<Topic> page = service.getAllTopicsByCategoryName(c, "FIRST_PAGE");
			if(page == null)
				continue;
			List<Topic> list = page.getResult();
			if(list != null && !list.isEmpty()) {
				topics.addAll(list);
			}
		}
		
		for(Topic topic : topics) {
			String category = topic.getCategory();
			if(category == null || category.trim().isEmpty()) {
				System.err.println("Empty category <topicId>" + topic.getId() + "</topicId><name>" + topic.getName() + "</name>");
			}
			String primaryCategory = CommonConstants.resolvePrimaryCategory(category);
			if(primaryCategory == null) {
				System.err.println("Failed to resolve : " + category);
				continue;
			}
			System.out.println(category + "=" + primaryCategory);
			topic.setCategory(primaryCategory);
			topic.setSubCategory(category);
			service.update(topic);
		}
		
		UserTopicMapRepositoryService service0 = ServiceRegistry.INSTANCE.findService(UserTopicMapRepositoryService.class);
		List<UserTopicMap> all = service0.getAll();
		if(all != null && !all.isEmpty()) {
			for(UserTopicMap a : all) {
				String category = a.getTopicCategory();
				if (CommonConstants.LIFESTYLE.equalsIgnoreCase(category)
						|| CommonConstants.ART.equalsIgnoreCase(category)
						|| CommonConstants.PHOTOGRAPHY
								.equalsIgnoreCase(category)
						|| CommonConstants.MUSIC.equalsIgnoreCase(category)
						|| CommonConstants.EDUCATION.equalsIgnoreCase(category)
						|| CommonConstants.FUN.equalsIgnoreCase(category)
						|| CommonConstants.SPIRITUAL.equalsIgnoreCase(category)) {
					a.setTopicCategory(CommonConstants.OTHERS);
				}
				service0.update(a);
			}
		}
	}

}
