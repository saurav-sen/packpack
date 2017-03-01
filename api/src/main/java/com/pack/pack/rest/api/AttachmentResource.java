package com.pack.pack.rest.api;

import static com.pack.pack.util.SystemPropertyUtil.IMAGE;
import static com.pack.pack.util.SystemPropertyUtil.PROFILE;
import static com.pack.pack.util.SystemPropertyUtil.TOPIC;
import static com.pack.pack.util.SystemPropertyUtil.URL_SEPARATOR;
import static com.pack.pack.util.SystemPropertyUtil.VIDEO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.IPackService;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.PackAttachmentType;
import com.pack.pack.rest.api.security.interceptors.CompressRead;
import com.pack.pack.rest.api.security.interceptors.CompressWrite;
import com.pack.pack.rest.web.util.ImageUtil;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
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

	private static Logger logger = LoggerFactory
			.getLogger(AttachmentResource.class);
	
	@GET
	@CompressWrite
	@Path(TOPIC + URL_SEPARATOR + IMAGE + URL_SEPARATOR + "{topicId}"
			+ URL_SEPARATOR + "{fileName}")
	@Produces({ "image/png", "image/jpg" })
	public Response getTopicWallpaperImage(@PathParam("topicId") String topicId,
			@PathParam("fileName") String fileName, @QueryParam("thumbnail") String thumnail,
			@QueryParam("w") int width, @QueryParam("h") int height) throws PackPackException {
		/*boolean isThumbnail = false;
		try {
			isThumbnail = thumnail != null ? Boolean.parseBoolean(thumnail.trim()) : false;
		} catch (Exception e) {
			// ignore
		}*/
		try {
			String topicWallpaperHome = SystemPropertyUtil.getTopicWallpaperHome();
			StringBuilder path = new StringBuilder(topicWallpaperHome);
			if (!topicWallpaperHome.endsWith(File.separator)) {
				path = path.append(File.separator);
			}
			path.append(topicId);
			path.append(File.separator);
			/*if(isThumbnail) {
				path.append("thumbnail");
				path.append(File.separator);
			}*/
			path.append(fileName);
			File imageFile = new File(path.toString());
			if(width > 0 && height > 0) {
				BufferedImage image = ImageIO.read(imageFile);
				image = Scalr.resize(image, Method.QUALITY,
						Mode.AUTOMATIC, width, height,
						Scalr.OP_ANTIALIAS);
				return ImageUtil.buildResponse(image);
			}
			else {
				return ImageUtil.buildResponse(imageFile);
			}
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}

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
			logger.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}
	
	/*@GET
	@Path(IMAGE + URL_SEPARATOR + "{topicId}" + URL_SEPARATOR + "{packId}"
			+ URL_SEPARATOR + "thumbnail" + URL_SEPARATOR + "{fileName}")
	@Produces({ "image/png", "image/jpg" })
	public Response getThumbnailImageAttachment(@PathParam("topicId") String topicId,
			@PathParam("packId") String packId,
			@PathParam("fileName") String fileName, @QueryParam("w") int width, 
			@QueryParam("h") int height) throws PackPackException {
		return getImageAttachment(topicId, packId, fileName, width, height);
	}*/

	@GET
	@CompressWrite
	@Path(IMAGE + URL_SEPARATOR + "{topicId}" + URL_SEPARATOR + "{packId}"
			+ URL_SEPARATOR + "{fileName}")
	@Produces({ "image/png", "image/jpg" })
	public Response getOriginalImageAttachment(@PathParam("topicId") String topicId,
			@PathParam("packId") String packId, @PathParam("fileName") String fileName, 
			@QueryParam("w") int width, @QueryParam("h") int height) throws PackPackException {
		return getImageAttachment(topicId, packId, fileName, width, height);
	}
	
	private Response getImageAttachment(String topicId,
			String packId, String fileName, int width, int height) throws PackPackException {
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
			/*if(isThumbnail) {
				path.append("thumbnail");
				path.append(File.separator);
			}*/
			path.append(fileName);
			File imageFile = new File(path.toString());
			if(width > 0 && height > 0) {
				BufferedImage image = ImageIO.read(imageFile);
				image = Scalr.resize(image, Method.QUALITY,
						Mode.AUTOMATIC, width, height,
						Scalr.OP_ANTIALIAS);
				return ImageUtil.buildResponse(image);
			}
			else {
				return ImageUtil.buildResponse(imageFile);
			}
		} catch (FileNotFoundException e) {
			logger.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}
	
	@GET
	@CompressWrite
	@Path(VIDEO + URL_SEPARATOR + "{topicId}" + URL_SEPARATOR + "{packId}"
			+ URL_SEPARATOR + "thumbnail" + URL_SEPARATOR + "{fileName}")
	// @Produces({"image/png", "image/jpg"})
	public Response getThumbnailVideoAttachment(@PathParam("topicId") String topicId,
			@PathParam("packId") String packId,
			@PathParam("fileName") String fileName) throws PackPackException {
		return getVideoAttachment(topicId, packId, fileName, true);
	}

	@GET
	@CompressWrite
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

	@PUT
	@CompressRead
	@CompressWrite
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
		//String fileName = aboutFile.getFileName();		
		String fileName = UUID.randomUUID().toString() + ".jpg";
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.uploadPack(file, fileName, title, description, story,
				topicId, userId, null, PackAttachmentType.IMAGE, true);
	}

	// http://javapapers.com/android/android-get-address-with-street-name-city-for-location-with-geocoding/

	@PUT
	@CompressRead
	@CompressWrite
	@Path("image/topic/{topicId}/pack/{packId}/usr/{userId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value = MediaType.APPLICATION_JSON)
	public JPackAttachment modifyPack_addImage(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@FormDataParam("title") String title,
			@FormDataParam("description") String description,
			@PathParam("topicId") String topicId,
			@PathParam("packId") String packId,
			@PathParam("userId") String userId) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		String fileName = UUID.randomUUID().toString() + ".jpg";
		return service.updatePack(file, fileName, PackAttachmentType.IMAGE,
				packId, topicId, userId, title, description, true);
	}

	@PUT
	@CompressRead
	@CompressWrite
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
		//String fileName = aboutFile.getFileName();
		String fileName = UUID.randomUUID().toString() + ".mp4";
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.uploadPack(file, fileName, title, description, story,
				topicId, userId, null, PackAttachmentType.VIDEO, true);
	}

	@PUT
	@CompressRead
	@CompressWrite
	@Path("video/topic/{topicId}/pack/{packId}/usr/{userId}")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value = MediaType.APPLICATION_JSON)
	public JPackAttachment modifyPack_addVideo(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@FormDataParam("title") String title,
			@FormDataParam("description") String description,
			@FormDataParam("isCompressed") String isCompressed,
			@PathParam("topicId") String topicId,
			@PathParam("packId") String packId,
			@PathParam("userId") String userId) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		String fileName = UUID.randomUUID().toString() + ".mp4";
		boolean parseBoolean = false;
		try {
			parseBoolean = Boolean.parseBoolean(isCompressed.trim());
		} catch (Exception e) {
			logger.debug("modifyPack_addVideo", e.getMessage(), e);
			parseBoolean = false;
		}
		logger.debug("modifyPack_addVideo :: isCompressed = " + parseBoolean);
		return service.updatePack(file, fileName, PackAttachmentType.VIDEO,
				packId, topicId, userId, title, description, parseBoolean);
	}
}