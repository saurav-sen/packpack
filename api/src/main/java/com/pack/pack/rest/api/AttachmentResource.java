package com.pack.pack.rest.api;

import java.io.File;
import java.io.FileNotFoundException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.rest.web.util.ImageUtil;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("/attachment")
public class AttachmentResource {
	
	private static Logger logger = LoggerFactory.getLogger(AttachmentResource.class);

	@GET
	@Path("images")
	@Produces({"image/png", "image/jpg"})
	public Response getImageAttachment(@Context UriInfo uriInfo) throws PackPackException {
		try {
			String imageHome = SystemPropertyUtil.getImageHome();
			String path = uriInfo.getPath();
			int index = path.indexOf("attachment/images") + "attachment/images".length();
			path = path.substring(index);
			path = imageHome + path;
			File imageFile = new File(path);
			return ImageUtil.buildResponse(imageFile);
		} catch (FileNotFoundException e) {
			logger.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}
}