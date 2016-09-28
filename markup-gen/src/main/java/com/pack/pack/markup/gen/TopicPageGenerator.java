package com.pack.pack.markup.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pack.pack.ITopicService;
import com.pack.pack.IUserService;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.SystemPropertyUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TopicPageGenerator implements IMarkupGenerator {

	TopicPageGenerator() {
	}

	public static void main(String[] args) throws Exception {
		/*
		 * JTopic topic = new JTopic(); topic.setName("My Topic0");
		 * INSTANCE.generateTopicPage(topic, user, packs);
		 */
	}

	private void generateTopicDetailsPage(Map<String, Object> dataModel)
			throws Exception {
		Writer writer = null;
		Configuration cfg = new Configuration();
		try {
			cfg.setClassForTemplateLoading(TopicPageGenerator.class,
					"/com/pack/pack/markup/topic/page");
			Template template = cfg.getTemplate("topic_detail.ftl");
			String path = SystemPropertyUtil.getWebPagesRootPath();
			if(path != null) {
				if(!path.endsWith(File.separator)) {
					path = path + File.separator;
				}
				path = path + "topics/page/" + dataModel.get("topicName") + "_"
						+ dataModel.get("topicId").hashCode() + ".html";
			}
			writer = new FileWriter(new File(path));
			template.process(dataModel, writer);
			writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void generateTopicDetailsPage(JTopic topic, JUser user, List<JPack> packs)
			throws Exception {
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("topicId", topic.getId());
		dataModel.put("topicName", topic.getName());
		dataModel.put("topicDescription", topic.getDescription());
		dataModel.put("topiclongitude", topic.getLongitude());
		dataModel.put("topicLatitude", topic.getLatitude());
		dataModel.put("ownerFullName", user.getName());
		dataModel.put("ownerEmail", user.getUsername());
		dataModel.put("packs", packs);
		generateTopicDetailsPage(dataModel);
	}
	
	private void modifyTopicListPage(JTopic topic, JUser owner) {
		String path = SystemPropertyUtil.getWebPagesRootPath();
		if (path != null) {
			if (!path.endsWith(File.separator)) {
				path = path + File.separator;
			}
			path = path + "topics/" + topic.getCategory().hashCode() + ".html";
		}
	}

	private void promoteTopic(String topicId) throws Exception {
		ITopicService topicService = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		JTopic topic = topicService.getTopicById(topicId);
		String ownerId = topic.getOwnerId();
		IUserService userService = ServiceRegistry.INSTANCE
				.findService(IUserService.class);
		JUser user = userService.findUserById(ownerId);
		modifyTopicListPage(topic, user);
		Pagination<JPack> page = topicService.getAllPacks(topicId, null);
		List<JPack> packs = page.getResult();
		generateTopicDetailsPage(topic, user, packs);
	}

	@Override
	public <T> void generateAndUpload(String entityId) throws Exception {
		promoteTopic(entityId);
	}
}
