package com.pack.pack.rest.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.IPackService;
import com.pack.pack.model.PackAttachmentType;
import com.pack.pack.model.web.JPack;
import com.pack.pack.rest.web.util.ImageUtil;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
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
	@Path("image")
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
	
	@PUT
	@Path("image/topic/{topicId}/usr/{userId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value = MediaType.APPLICATION_JSON)
	public JPack uploadImagePack(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@FormDataParam("title") String title,
			@FormDataParam("description") String description,
			@FormDataParam("story") String story,
			@PathParam("topicId") String topicId,
			@PathParam("userId") String userId) throws PackPackException {
		String fileName = aboutFile.getFileName();
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.uploadPack(file, fileName, title, description,
				story, topicId, userId, null, PackAttachmentType.IMAGE);
	}
	
	@PUT
	@Path("images/pack/{packId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value = MediaType.APPLICATION_JSON)
	public JPack modifyPack_addImage(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@PathParam("packId") String packId) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.updatePack(file, aboutFile.getFileName(),
				PackAttachmentType.IMAGE, packId);
	}
	
	@PUT
	@Path("video/topic/{topicId}usr/{userId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value =MediaType.APPLICATION_JSON)
	public JPack uploadVideoPack(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@FormDataParam("title") String title,
			@FormDataParam("description") String description,
			@FormDataParam("story") String story,
			@PathParam("topicId") String topicId,
			@PathParam("userId") String userId) throws PackPackException {
		String fileName = aboutFile.getFileName();
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.uploadPack(file, fileName, title, description,
				story, topicId, userId, null, PackAttachmentType.IMAGE);
	}
	
	@PUT
	@Path("video/pack/{packId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value = MediaType.APPLICATION_JSON)
	public JPack modifyPack_addVideo(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@PathParam("packId") String packId) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.updatePack(file, aboutFile.getFileName(),
				PackAttachmentType.VIDEO, packId);
	}
}