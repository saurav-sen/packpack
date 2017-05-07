package com.pack.pack.ml.rest.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

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
import com.pack.pack.markup.gen.util.PromotedFileUtil;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.security.util.EncryptionUtil;
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
}
