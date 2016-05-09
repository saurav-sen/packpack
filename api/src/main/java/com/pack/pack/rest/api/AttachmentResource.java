package com.pack.pack.rest.api;

import static com.pack.pack.util.SystemPropertyUtil.ATTACHMENT;
import static com.pack.pack.util.SystemPropertyUtil.IMAGE;
import static com.pack.pack.util.SystemPropertyUtil.PROFILE;
import static com.pack.pack.util.SystemPropertyUtil.TOPIC;
import static com.pack.pack.util.SystemPropertyUtil.URL_SEPARATOR;
import static com.pack.pack.util.SystemPropertyUtil.VIDEO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
@Path("/" + ATTACHMENT)
public class AttachmentResource {

	private static Logger logger = LoggerFactory
			.getLogger(AttachmentResource.class);
	
	@GET
	@Path(TOPIC + URL_SEPARATOR + IMAGE + URL_SEPARATOR + "{topicId}"
			+ URL_SEPARATOR + "{fileName}")
	@Produces({ "image/png", "image/jpg" })
	public Response getTopicWallpaperImage(@PathParam("topicId") String topicId,
			@PathParam("fileName") String fileName, @QueryParam("thumnail") String thumnail) throws PackPackException {
		boolean isThumbnail = false;
		try {
			isThumbnail = thumnail != null ? Boolean.parseBoolean(thumnail.trim()) : false;
		} catch (Exception e) {
			// ignore
		}
		try {
			String topicWallpaperHome = SystemPropertyUtil.getTopicWallpaperHome();
			StringBuilder path = new StringBuilder(topicWallpaperHome);
			if (!topicWallpaperHome.endsWith(File.separator)) {
				path = path.append(File.separator);
			}
			path.append(topicId);
			path.append(File.separator);
			if(isThumbnail) {
				path.append("thumbnail");
				path.append(File.separator);
			}
			path.append(fileName);
			File imageFile = new File(path.toString());
			return ImageUtil.buildResponse(imageFile);
		} catch (FileNotFoundException e) {
			logger.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}

	@GET
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
			logger.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}
	
	@GET
	@Path(IMAGE + URL_SEPARATOR + "{topicId}" + URL_SEPARATOR + "{packId}"
			+ URL_SEPARATOR + "thumbnail" + URL_SEPARATOR + "{fileName}")
	@Produces({ "image/png", "image/jpg" })
	public Response getThumbnailImageAttachment(@PathParam("topicId") String topicId,
			@PathParam("packId") String packId,
			@PathParam("fileName") String fileName) throws PackPackException {
		return getImageAttachment(topicId, packId, fileName, true);
	}

	@GET
	@Path(IMAGE + URL_SEPARATOR + "{topicId}" + URL_SEPARATOR + "{packId}"
			+ URL_SEPARATOR + "{fileName}")
	@Produces({ "image/png", "image/jpg" })
	public Response getOriginalImageAttachment(@PathParam("topicId") String topicId,
			@PathParam("packId") String packId,
			@PathParam("fileName") String fileName) throws PackPackException {
		return getImageAttachment(topicId, packId, fileName, false);
	}
	
	private Response getImageAttachment(String topicId,
			String packId, String fileName, boolean isThumbnail) throws PackPackException {
		try {
			String imageHome = SystemPropertyUtil.getImageHome();
			StringBuilder path = new StringBuilder(imageHome);
			if (!imageHome.endsWith(File.separator)) {
				path.append(File.separator);
			}
			path.append(topicId);
			path.append(File.separator);
			path.append(packId);
			path.append(File.separator);
			if(isThumbnail) {
				path.append("thumbnail");
				path.append(File.separator);
			}
			path.append(fileName);
			File imageFile = new File(path.toString());
			return ImageUtil.buildResponse(imageFile);
		} catch (FileNotFoundException e) {
			logger.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}
	
	@GET
	@Path(VIDEO + URL_SEPARATOR + "{topicId}" + URL_SEPARATOR + "{packId}"
			+ URL_SEPARATOR + "thumbnail" + URL_SEPARATOR + "{fileName}")
	// @Produces({"image/png", "image/jpg"})
	public Response getThumbnailVideoAttachment(@PathParam("topicId") String topicId,
			@PathParam("packId") String packId,
			@PathParam("fileName") String fileName) throws PackPackException {
		return getVideoAttachment(topicId, packId, fileName, true);
	}

	@GET
	@Path(VIDEO + URL_SEPARATOR + "{topicId}" + URL_SEPARATOR + "{packId}"
			+ URL_SEPARATOR + "{fileName}")
	// @Produces({"image/png", "image/jpg"})
	public Response getOriginalVideoAttachment(@PathParam("topicId") String topicId,
			@PathParam("packId") String packId,
			@PathParam("fileName") String fileName) throws PackPackException {
		return getVideoAttachment(topicId, packId, fileName, false);
	}
	
	
	private Response getVideoAttachment(String topicId,
			String packId, String fileName, boolean isThumbnail) throws PackPackException {
		try {
			String videoHome = SystemPropertyUtil.getVideoHome();
			StringBuilder path = new StringBuilder(videoHome);
			if (!videoHome.endsWith(File.separator)) {
				path.append(File.separator);
			}
			path.append(topicId);
			path.append(File.separator);
			path.append(packId);
			path.append(File.separator);
			if(isThumbnail) {
				path.append("thumbnail");
				path.append(File.separator);
			}
			path.append(fileName);
			File videoFile = new File(path.toString());
			return ImageUtil.buildResponse(videoFile);
		} catch (FileNotFoundException e) {
			// logger.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}

	@POST
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
		return service.uploadPack(file, fileName, title, description, story,
				topicId, userId, null, PackAttachmentType.IMAGE, true);
	}

	// http://javapapers.com/android/android-get-address-with-street-name-city-for-location-with-geocoding/

	@PUT
	@Path("image/topic/{topicId}/pack/{packId}/usr/{userId}")
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

	@POST
	@Path("video/topic/{topicId}/usr/{userId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value = MediaType.APPLICATION_JSON)
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
		return service.uploadPack(file, fileName, title, description, story,
				topicId, userId, null, PackAttachmentType.IMAGE, true);
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