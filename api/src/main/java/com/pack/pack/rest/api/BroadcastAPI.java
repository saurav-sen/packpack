package com.pack.pack.rest.api;

import static com.pack.pack.util.SystemPropertyUtil.BROADCAST_API_PREFIX;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.pack.pack.IPackService;
import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.model.PackAttachmentType;
import com.pack.pack.model.web.JPack;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.rabbitmq.objects.BroadcastCriteria;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path(BROADCAST_API_PREFIX)
public class BroadcastAPI {

	@POST
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Produces(value=MediaType.APPLICATION_JSON)
	public JPack broadcastImagePack(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition aboutFile,
			@FormDataParam("title") String title,
			@FormDataParam("description") String description,
			@FormDataParam("story") String story,
			@FormDataParam("city") String city,
			@FormDataParam("state") String state,
			@FormDataParam("country") String country) throws PackPackException {
		String defaultTopicId = CommonConstants.DEFAULT_TOPIC_ID;
		IPackService service = ServiceRegistry.INSTANCE.findCompositeService(IPackService.class);
		JPack pack = service.uploadPack(file, aboutFile.getFileName(), title, description,
				story, defaultTopicId, null, null, PackAttachmentType.IMAGE, false);
		String packId = pack.getId();
		BroadcastCriteria criteria = new BroadcastCriteria();
		criteria.setCity(city);
		criteria.setCountry(country);
		criteria.setState(state);
		service.broadcastSystemPack(criteria, packId);
		return pack;
	}
}