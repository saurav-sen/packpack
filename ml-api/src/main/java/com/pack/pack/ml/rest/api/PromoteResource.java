package com.pack.pack.ml.rest.api;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.markup.gen.MarkupGenerator;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.PromoteStatus;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.EntityPromoteDTO;
import com.pack.pack.security.util.EncryptionUtil;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/promote")
public class PromoteResource {

	private static final Logger LOG = LoggerFactory
			.getLogger(PromoteResource.class);

	@PUT
	@Path("usr/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PromoteStatus promote(@PathParam("userId") String userId, String json)
			throws PackPackException {
		PromoteStatus status = new PromoteStatus();
		try {
			LOG.info("Preparing to promote");
			EntityPromoteDTO dto = JSONUtil.deserialize(json,
					EntityPromoteDTO.class, true);
			String entityId = dto.getId();
			String entityType = dto.getType();
			Class<?> type = resolveEntityType(entityType);
			LOG.info("Promote called for TYPE=" + entityType + " & ID="
					+ entityId);
			MarkupGenerator.INSTANCE.generateAndUpload(entityId, type);
			status.setStatus(StatusType.OK);
			status.setPublicUrl(calculatePublicUrl(entityId, entityType));
			LOG.info("Successfully promoted");
		} catch (PackPackException e) {
			LOG.error("Promotion failed");
			LOG.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			LOG.error("Promotion failed");
			LOG.error(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61,
					"Failed Promoting item requested", e);
		}
		return status;
	}

	private Class<?> resolveEntityType(String entityTypeName)
			throws PackPackException {
		if (JTopic.class.getName().equals(entityTypeName)) {
			return JTopic.class;
		} else if (JPack.class.getName().equals(entityTypeName)) {
			return JPack.class;
		} else if (JPackAttachment.class.getName().equals(entityTypeName)) {
			return JPackAttachment.class;
		}
		throw new PackPackException(ErrorCodes.PACK_ERR_71,
				"[ERROR::Promotion failed] Cause type of entity '"
						+ entityTypeName + "' is not known");
	}

	private String calculatePublicUrl(String entityId, String entityTypeName) {
		String appBaseUrl = SystemPropertyUtil.getBaseURL_2();
		if (!appBaseUrl.endsWith("/")) {
			appBaseUrl = appBaseUrl + "/";
		}
		String encryptEntityId = EncryptionUtil
				.encryptTextUsingSystemKey(entityId);
		int hashCode = encryptEntityId.hashCode();
		encryptEntityId = String.valueOf(Math.abs(hashCode));
		if (hashCode < 0) {
			encryptEntityId = encryptEntityId + "_c";
		}
		if (JTopic.class.getName().equals(entityTypeName)) {
			return appBaseUrl + "public/topic/" + encryptEntityId;
		} else if (JPack.class.getName().equals(entityTypeName)) {
			return appBaseUrl + "public/pack/" + encryptEntityId;
		} else if (JPackAttachment.class.getName().equals(entityTypeName)) {
			return appBaseUrl + "public/attachment/" + encryptEntityId;
		}
		return null;
	}
}