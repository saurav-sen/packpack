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
import static com.pack.pack.util.SystemPropertyUtil.ATTACHMENT;
import static com.pack.pack.util.SystemPropertyUtil.IMAGE;
import static com.pack.pack.util.SystemPropertyUtil.VIDEO;
import static com.pack.pack.util.SystemPropertyUtil.PROFILE;
import static com.pack.pack.util.SystemPropertyUtil.URL_SEPARATOR;
import static com.pack.pack.util.SystemPropertyUtil.IMAGE_ATTACHMENT_URL_SUFFIX;
import static com.pack.pack.util.SystemPropertyUtil.VIDEO_ATTACHMENT_URL_SUFFIX;
import static com.pack.pack.util.SystemPropertyUtil.PROFILE_IMAGE_URL_SUFFIX;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("/" + ATTACHMENT)
public class AttachmentResource {
	
	private static Logger logger = LoggerFactory.getLogger(AttachmentResource.class);
	
	@GET
	@Path(PROFILE + URL_SEPARATOR + IMAGE)
	@Produces({"image/png", "image/jpg"})
	public Response getProfilePictureImage(@Context UriInfo uriInfo) throws PackPackException {
		try {
			String profilePictureHome = SystemPropertyUtil.getProfilePictureHome();
			String path = uriInfo.getPath();
			int index = path.indexOf(PROFILE_IMAGE_URL_SUFFIX) + PROFILE_IMAGE_URL_SUFFIX.length();
			path = path.substring(index);
			path = profilePictureHome + path;
			path = path.replaceAll(URL_SEPARATOR, File.separator);
			File imageFile = new File(path);
			return ImageUtil.buildResponse(imageFile);
		} catch (FileNotFoundException e) {
			logger.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}

	@GET
	@Path(IMAGE)
	@Produces({"image/png", "image/jpg"})
	public Response getImageAttachment(@Context UriInfo uriInfo) throws PackPackException {
		try {
			String imageHome = SystemPropertyUtil.getImageHome();
			String path = uriInfo.getPath();
			int index = path.indexOf(IMAGE_ATTACHMENT_URL_SUFFIX) + IMAGE_ATTACHMENT_URL_SUFFIX.length();
			path = path.substring(index);
			path = imageHome + path;
			path = path.replaceAll(URL_SEPARATOR, File.separator);
			File imageFile = new File(path);
			return ImageUtil.buildResponse(imageFile);
		} catch (FileNotFoundException e) {
			logger.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}
	
	@GET
	@Path(VIDEO)
	//@Produces({"image/png", "image/jpg"})
	public Response getVideoAttachment(@Context UriInfo uriInfo) throws PackPackException {
		try {
			String videoHome = SystemPropertyUtil.getVideoHome();
			String path = uriInfo.getPath();
			int index = path.indexOf(VIDEO_ATTACHMENT_URL_SUFFIX) + VIDEO_ATTACHMENT_URL_SUFFIX.length();
			path = path.substring(index);
			path = videoHome + path;
			path = path.replaceAll(URL_SEPARATOR, File.separator);
			File videoFile = new File(path);
			return ImageUtil.buildResponse(videoFile);
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
				story, topicId, userId, null, PackAttachmentType.IMAGE, true);
	}
	//http://javapapers.com/android/android-get-address-with-street-name-city-for-location-with-geocoding/
	
	@PUT
	@Path("images/topic/{topicId}/pack/{packId}/usr/{userId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value = MediaType.APPLICATION_JSON)
	public JPack modifyPack_addImage(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@PathParam("topicId") String topicId,
			@PathParam("packId") String packId,
			@PathParam("userId") String userId) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.updatePack(file, aboutFile.getFileName(),
				PackAttachmentType.IMAGE, packId, topicId, userId);
	}
	
	@PUT
	@Path("video/topic/{topicId}/usr/{userId}")
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
				story, topicId, userId, null, PackAttachmentType.IMAGE, true);
	}
	
	@PUT
	@Path("video/topic/{topicId}/pack/{packId}/usr/{userId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value = MediaType.APPLICATION_JSON)
	public JPack modifyPack_addVideo(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@PathParam("topicId") String topicId,
			@PathParam("packId") String packId,
			@PathParam("userId") String userId) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.updatePack(file, aboutFile.getFileName(),
				PackAttachmentType.VIDEO, packId, topicId, userId);
	}
}