package com.pack.pack.markup.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.pack.pack.IPackService;
import com.pack.pack.markup.gen.model.AttachmentPageModel;
import com.pack.pack.markup.gen.util.PromotedFileUtil;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.security.util.EncryptionUtil;
import com.pack.pack.services.registry.ServiceRegistry;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 
 * @author Saurav
 *
 */
public class PackAttachmentPageGenerator implements IMarkupGenerator {

	@Override
	public <T> void generateAndUpload(String entityId) throws Exception {
		IPackService packService = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		JPackAttachment attachment = packService
				.getPackAttachmentById(entityId);
		AttachmentPageModel model = AttachmentPageModel.build(attachment);
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("attachmentId", attachment.getId());
		dataModel.put("model", model);
		generateAttachmentDetailsPage(dataModel);
	}

	private void generateAttachmentDetailsPage(Map<String, Object> dataModel)
			throws Exception {
		Writer writer = null;
		Configuration cfg = new Configuration();
		try {
			cfg.setClassForTemplateLoading(TopicPageGenerator.class,
					"/com/pack/pack/markup/topic/page");
			Template template = cfg.getTemplate("attachment_detail.ftl");
			String attachmentId = (String) dataModel.get("attachmentId");
			String encryptedAttachmentId = EncryptionUtil
					.encryptTextUsingSystemKey(attachmentId);
			encryptedAttachmentId = String.valueOf(encryptedAttachmentId.hashCode());
			String path = PromotedFileUtil
					.calculatePathForPromotedAttachmentPage(encryptedAttachmentId);
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

	public static void main(String[] args) {
		String str = "https://www.youtube.com/watch?v=YjSUSPzJiAU";
		String[] split = str.split("v=");
		System.out.println("https://www.youtube.com/embed/" + split[1]);
	}
}