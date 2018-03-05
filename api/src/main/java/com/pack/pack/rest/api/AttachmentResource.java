package com.pack.pack.rest.api;

import static com.pack.pack.util.SystemPropertyUtil.IMAGE;
import static com.pack.pack.util.SystemPropertyUtil.PROFILE;
import static com.pack.pack.util.SystemPropertyUtil.URL_SEPARATOR;

import java.io.File;
import java.io.FileNotFoundException;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.rest.api.security.interceptors.CompressWrite;
import com.pack.pack.rest.web.util.ImageUtil;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/attachment")
public class AttachmentResource {

	private static Logger LOG = LoggerFactory
			.getLogger(AttachmentResource.class);
	
	@GET
	@CompressWrite
	@Path(PROFILE + URL_SEPARATOR + IMAGE + URL_SEPARATOR + "{userId}"
			+ URL_SEPARATOR + "{fileName}")
	@Produces({ "image/png", "image/jpg" })
	public Response getProfilePictureImage(@PathParam("userId") String userId,
			@PathParam("fileName") String fileName) throws PackPackException {
		try {
			String profilePictureHome = SystemPropertyUtil
					.getProfilePictureHome();
			StringBuilder path = new StringBuilder(profilePictureHome);
			if (!profilePictureHome.endsWith(File.separator)) {
				path = path.append(File.separator);
			}
			path.append(userId);
			path.append(File.separator);
			path.append(fileName);
			File imageFile = new File(path.toString());
			return ImageUtil.buildResponse(imageFile);
		} catch (FileNotFoundException e) {
			LOG.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}
}