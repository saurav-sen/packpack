package com.pack.pack.markup.gen;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JTopic;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * @author Saurav
 *
 */
public class MarkupGenerator {

	public static final MarkupGenerator INSTANCE = new MarkupGenerator();

	private static Map<String, IMarkupGenerator> generatorsMap = new HashMap<String, IMarkupGenerator>();
	static {
		generatorsMap.put(JTopic.class.getName(), new TopicPageGenerator());
		generatorsMap.put(JPackAttachment.class.getName(),
				new PackAttachmentPageGenerator());
	}

	MarkupGenerator() {
	}

	public <T> void generateAndUpload(String entityId, Class<T> type)
			throws Exception {
		IMarkupGenerator generator = generatorsMap.get(type.getName());
		if (generator != null) {
			generator.generateAndUpload(entityId);
		}
	}
	
	public String generateWelcomeEmailHtmlContent(String userName, int userCount, String OTP) throws IOException, TemplateException {
		Writer writer = null;
		Configuration cfg = new Configuration();
		Map<String, Object> dataModel = new HashMap<String, Object>();
		try {
			dataModel.put("name", userName);
			String userCountStr = "new";
			if (userCount > 0) {
				int userCount_1 = userCount % 10;
				if (userCount_1 == 1) {
					userCountStr = userCount + " st";
				} else if (userCount_1 == 2) {
					userCountStr = userCount + " nd";
				} else if (userCount_1 == 3) {
					userCountStr = userCount + " rd";
				} else {
					userCountStr = userCount + " th";
				}
			}
			dataModel.put("count", userCountStr);
			dataModel.put("OTP", OTP);
			
			cfg.setClassForTemplateLoading(TopicPageGenerator.class,
					"/com/pack/pack/markup/notifications");
			Template template = cfg.getTemplate("welcome.ftl");
			
			writer = new StringWriter();
			template.process(dataModel, writer);
			writer.flush();
			String str = writer.toString();
			return str;
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	public String generatePasswordResetVerifierMail(String OTP) throws IOException, TemplateException {
		Writer writer = null;
		Configuration cfg = new Configuration();
		Map<String, Object> dataModel = new HashMap<String, Object>();
		try {
			dataModel.put("otp", OTP);
			
			cfg.setClassForTemplateLoading(TopicPageGenerator.class,
					"/com/pack/pack/markup/notifications");
			Template template = cfg.getTemplate("passwd_reset_token.ftl");
			
			writer = new StringWriter();
			template.process(dataModel, writer);
			writer.flush();
			String str = writer.toString();
			return str;
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}