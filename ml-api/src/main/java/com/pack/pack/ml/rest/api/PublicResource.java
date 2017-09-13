package com.pack.pack.ml.rest.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.ITopicService;
import com.pack.pack.markup.gen.IMarkup;
import com.pack.pack.markup.gen.MarkupGenerator;
import com.pack.pack.markup.gen.util.PromotedFileUtil;
import com.pack.pack.model.web.JSharedFeed;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JTopics;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/public")
public class PublicResource {

	private static final Logger LOG = LoggerFactory
			.getLogger(PublicResource.class);

	@GET
	@Path("ext/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String getExternallySharedProxyPage(@PathParam("id") String id)
			throws PackPackException {
		try {
			Markup markup = new Markup();
			MarkupGenerator.INSTANCE.generateMarkup(id, JSharedFeed.class,
					markup);
			return markup.getContent();
		} catch (Exception e) {
			LOG.error("Promotion failed");
			LOG.error(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61,
					"Failed Generating Proxy Page for Shared Link", e);
		}
	}

	@GET
	@Path("visions/top")
	@Produces(MediaType.APPLICATION_JSON)
	public JTopics GET_topPromotedVisions() throws PackPackException {
		JTopics result = new JTopics();
		ITopicService service = ServiceRegistry.INSTANCE
				.findCompositeService(ITopicService.class);
		List<JTopic> topics = service.getAllHotTopics();
		if (topics != null && !topics.isEmpty()) {
			for (JTopic topic : topics) {
				result.getTopics().add(topic);
			}
		}
		return result;
	}

	@GET
	@Path("topic/{encryptedTopicId}")
	@Produces(MediaType.TEXT_HTML)
	public String promotedTopicPage(
			@PathParam("encryptedTopicId") String encryptedTopicId)
			throws PackPackException {
		String result = null;
		/*
		 * String topicId = EncryptionUtil
		 * .decryptTextWithSystemKey(encryptedTopicId); ITopicService
		 * topicService = ServiceRegistry.INSTANCE
		 * .findCompositeService(ITopicService.class); JTopic topic =
		 * topicService.getTopicById(topicId); String path =
		 * PromotedFileUtil.calculatePathForTopicDetailsPage( encryptedTopicId,
		 * topic.getCategory());
		 */
		String path = PromotedFileUtil
				.calculatePathForTopicDetailsPage(encryptedTopicId);
		BufferedReader buffReader = null;
		StringWriter stringWriter = new StringWriter();
		BufferedWriter buffWriter = new BufferedWriter(stringWriter);
		try {
			buffReader = new BufferedReader(new FileReader(new File(path)));
			String line = buffReader.readLine();
			while (line != null) {
				buffWriter.write(line);
				buffWriter.newLine();
				line = buffReader.readLine();
			}
			buffWriter.flush();
			result = stringWriter.toString();
		} catch (FileNotFoundException e) {
			LOG.debug(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage(),
					e);
		} catch (IOException e) {
			LOG.debug(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage(),
					e);
		} finally {
			try {
				if (buffReader != null) {
					buffReader.close();
				}
				if (buffWriter != null) {
					buffWriter.close();
				}
			} catch (IOException e) {
				LOG.debug(e.getMessage(), e);
			}
		}
		return result;
	}

	@GET
	@Path("pack/{encryptedPackId}")
	@Produces(MediaType.TEXT_HTML)
	public String promotedPackPage(
			@PathParam("encryptedPackId") String encryptedPackId)
			throws PackPackException {
		String result = null;
		String path = PromotedFileUtil
				.calculatePathForPackDetailsPage(encryptedPackId);
		BufferedReader buffReader = null;
		StringWriter stringWriter = new StringWriter();
		BufferedWriter buffWriter = new BufferedWriter(stringWriter);
		try {
			buffReader = new BufferedReader(new FileReader(new File(path)));
			String line = buffReader.readLine();
			while (line != null) {
				buffWriter.write(line);
				buffWriter.newLine();
			}
			buffWriter.flush();
			result = stringWriter.toString();
		} catch (FileNotFoundException e) {
			LOG.debug(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage(),
					e);
		} catch (IOException e) {
			LOG.debug(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage(),
					e);
		} finally {
			try {
				if (buffReader != null) {
					buffReader.close();
				}
				if (buffWriter != null) {
					buffWriter.close();
				}
			} catch (IOException e) {
				LOG.debug(e.getMessage(), e);
			}
		}
		return result;
	}

	@GET
	@Path("attachment/{encryptedAttachmentId}")
	@Produces(MediaType.TEXT_HTML)
	public String promotedPackAttachmentPage(
			@PathParam("encryptedAttachmentId") String encryptedAttachmentId)
			throws PackPackException {
		String result = null;
		String path = PromotedFileUtil
				.calculatePathForPromotedAttachmentPage(encryptedAttachmentId);
		BufferedReader buffReader = null;
		StringWriter stringWriter = new StringWriter();
		BufferedWriter buffWriter = new BufferedWriter(stringWriter);
		try {
			buffReader = new BufferedReader(new FileReader(new File(path)));
			String line = buffReader.readLine();
			while (line != null) {
				buffWriter.write(line);
				buffWriter.newLine();
				line = buffReader.readLine();
			}
			buffWriter.flush();
			result = stringWriter.toString();
		} catch (FileNotFoundException e) {
			LOG.debug(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage(),
					e);
		} catch (IOException e) {
			LOG.debug(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61, e.getMessage(),
					e);
		} finally {
			try {
				if (buffReader != null) {
					buffReader.close();
				}
				if (buffWriter != null) {
					buffWriter.close();
				}
			} catch (IOException e) {
				LOG.debug(e.getMessage(), e);
			}
		}
		return result;
	}

	private class Markup implements IMarkup {

		private String contentType;

		private String content;

		private Markup() {
		}

		@Override
		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		@Override
		public void setContent(String content) {
			this.content = content;
		}

		private String getContent() {
			return content;
		}
	}
}
