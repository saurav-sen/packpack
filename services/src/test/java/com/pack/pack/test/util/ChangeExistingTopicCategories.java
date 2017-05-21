package com.pack.pack.test.util;

import java.util.ArrayList;
import java.util.List;

import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.model.Topic;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.couchdb.TopicRepositoryService;
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
		}
	}

}
