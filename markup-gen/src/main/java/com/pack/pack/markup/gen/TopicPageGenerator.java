package com.pack.pack.markup.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pack.pack.IPackService;
import com.pack.pack.ITopicService;
import com.pack.pack.IUserService;
import com.pack.pack.markup.gen.model.AttachmentModel;
import com.pack.pack.markup.gen.model.PackModel;
import com.pack.pack.markup.gen.util.PromotedFileUtil;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.security.util.EncryptionUtil;
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
			String topicId = (String) dataModel.get("topicId");
			// String topicCategory = (String) dataModel.get("topicCategory");
			String encryptedTopicId = EncryptionUtil
					.encryptTextUsingSystemKey(topicId);
			int hashCode = encryptedTopicId.hashCode();
			encryptedTopicId = String.valueOf(Math.abs(hashCode));
			if (hashCode < 0) {
				encryptedTopicId = encryptedTopicId + "_c";
			}
			/*
			 * String path = PromotedFileUtil.calculatePathForTopicDetailsPage(
			 * encryptedTopicId, topicCategory);
			 */
			String path = PromotedFileUtil
					.calculatePathForTopicDetailsPage(encryptedTopicId);
			if (path != null) {
				writer = new FileWriter(new File(path));
				template.process(dataModel, writer);
				writer.flush();
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void generateTopicDetailsPage(JTopic topic, JUser user,
			List<PackModel> packs) throws Exception {
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("topicId", topic.getId());
		dataModel.put("topicName", topic.getName());
		dataModel.put("topicCategory", topic.getCategory());
		dataModel.put("topicWallpaperUrl", topic.getWallpaperUrl());
		dataModel.put("topicDescription", topic.getDescription());
		dataModel.put("topiclongitude", topic.getLongitude());
		dataModel.put("topicLatitude", topic.getLatitude());
		dataModel.put("ownerFullName", user.getName());
		dataModel.put("ownerEmail", user.getUsername());
		dataModel.put("packs", packs);
		dataModel.put("jsBaseUrl", SystemPropertyUtil.getJSBaseURL());
		generateTopicDetailsPage(dataModel);
	}

	private void modifyTopicListPage(JTopic topic, JUser owner)
			throws Exception {
		String path = SystemPropertyUtil.getWebPagesRootPath();
		if (path == null) {
			return;
		}
		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		path = path + "topics";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
		path = path + File.separator + topic.getCategory();
		file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
		path = path + File.separator + "index.html";
		file = new File(path);
		if (!file.exists()) {
			Writer writer = null;
			Configuration cfg = new Configuration();
			try {
				Map<String, Object> dataModel = new HashMap<String, Object>();
				cfg.setClassForTemplateLoading(TopicPageGenerator.class,
						"/com/pack/pack/markup/topic");
				Template template = cfg.getTemplate("topics_list.ftl");
				writer = new FileWriter(file);
				template.process(dataModel, writer);
				writer.flush();
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		}

		{
			Document document = Jsoup.parse(file, null);
			Element div = document.getElementById(topic.getId());
			if (div != null)
				return;

			FileWriter fileWriter = null;
			StringWriter strWriter = null;
			Configuration cfg = new Configuration();
			try {
				Map<String, Object> dataModel = new HashMap<String, Object>();
				dataModel.put("topic", topic);
				dataModel.put("pageHref", topic.getId().hashCode()
						+ "/index.html");
				cfg.setClassForTemplateLoading(TopicPageGenerator.class,
						"/com/pack/pack/markup/topic");
				Template template = cfg.getTemplate("new_topic_link.ftl");
				strWriter = new StringWriter();
				template.process(dataModel, strWriter);
				strWriter.flush();
				String content = strWriter.toString();
				Document newEl = Jsoup.parse(content);
				Elements elements = document.getElementsByTag("body");
				Element body = elements.get(0);
				body.appendChild(newEl);
				String outerHtml = document.outerHtml();
				fileWriter = new FileWriter(file);
				fileWriter.write(outerHtml);
				fileWriter.flush();
			} finally {
				if (strWriter != null) {
					strWriter.close();
				}
				if (fileWriter != null) {
					fileWriter.close();
				}
			}
		}
	}

	private void promoteTopic(String topicId) throws Exception {
		ITopicService topicService = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		JTopic topic = topicService.getTopicById(topicId);
		String ownerId = topic.getOwnerId();
		IUserService userService = ServiceRegistry.INSTANCE
				.findCompositeService(IUserService.class);
		JUser user = userService.findUserById(ownerId);
		// modifyTopicListPage(topic, user);
		Pagination<JPack> page = topicService.getAllPacks(topicId, null);
		List<JPack> packs = page.getResult();
		List<PackModel> packModels = new ArrayList<PackModel>();
		if (packs != null && !packs.isEmpty()) {
			IPackService packService = ServiceRegistry.INSTANCE
					.findCompositeService(IPackService.class);
			for (JPack pack : packs) {
				PackModel packModel = PackModel.build(pack);
				Pagination<JPackAttachment> attachmentsPage = packService
						.loadPackAttachments(user.getId(), topicId,
								pack.getId(), null);
				if (attachmentsPage == null)
					continue;
				List<JPackAttachment> attachments = attachmentsPage.getResult();
				if (attachments == null || attachments.isEmpty())
					continue;
				for (JPackAttachment attachment : attachments) {
					AttachmentModel attachmentModel = AttachmentModel
							.build(attachment);
					packModel.getAttachments().add(attachmentModel);
				}
				packModels.add(packModel);
			}
		}
		generateTopicDetailsPage(topic, user, packModels);
	}

	@Override
	public <T> void generateAndUpload(String entityId) throws Exception {
		promoteTopic(entityId);
	}
}