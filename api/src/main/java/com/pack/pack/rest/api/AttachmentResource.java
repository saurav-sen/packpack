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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.model.Pack;
import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.PackAttachmentType;
import com.pack.pack.model.web.JPack;
import com.pack.pack.rest.web.util.AttachmentUtil;
import com.pack.pack.services.couchdb.PackRepositoryService;
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
			return AttachmentUtil.buildResponse(imageFile);
		} catch (FileNotFoundException e) {
			logger.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}
	
	@PUT
	@Path("images/topic/{topicId}/usr/{userId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value =MediaType.APPLICATION_JSON)
	public JPack uploadImagePack(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@FormDataParam("title") String title,
			@FormDataParam("description") String description,
			@FormDataParam("story") String story,
			@PathParam("topicId") String topicId,
			@PathParam("userId") String userId) throws PackPackException {
		String location = SystemPropertyUtil.getImageHome() + File.separator + topicId;
		File f = new File(location);
		if(!f.exists()) {
			f.mkdir();
		}
		Pack pack = new Pack();
		pack.setCreationTime(new DateTime(DateTimeZone.getDefault()));
		pack.setStory(story);
		pack.setTitle(title);
		pack.setCreatorId(userId);
		PackRepositoryService service = ServiceRegistry.INSTANCE.findService(PackRepositoryService.class);
		service.add(pack);
		location = location + File.separator + pack.getId();
		f = new File(location);
		if(!f.exists()) {
			f.mkdir();
		}
		location = location + aboutFile.getFileName();
		AttachmentUtil.storeUploadedAttachment(file, location);
		PackAttachment attachment = new PackAttachment();
		attachment.setAttachmentUrl(location);
		attachment.setType(PackAttachmentType.IMAGE);
		return null;
	}
	
	@PUT
	@Path("images/pack/{packId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value =MediaType.APPLICATION_JSON)
	public JPack modifyPack_addImage(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@PathParam("packId") String packId) throws PackPackException {
		return null;
	}
	
	@PUT
	@Path("video/topic/{topicId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value =MediaType.APPLICATION_JSON)
	public JPack uploadVideoPack(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@FormDataParam("title") String title,
			@FormDataParam("description") String description,
			@FormDataParam("story") String story,
			@PathParam("topicId") String topicId) throws PackPackException {
		return null;
	}
	
	@PUT
	@Path("video/pack/{packId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value =MediaType.APPLICATION_JSON)
	public JPack modifyPack_addVideo(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@PathParam("packId") String packId) throws PackPackException {
		String imageHome = SystemPropertyUtil.getImageHome();
		
		//AttachmentUtil.storeUploadedAttachment(file, uploadedFileLocation);
		return null;
	}
}