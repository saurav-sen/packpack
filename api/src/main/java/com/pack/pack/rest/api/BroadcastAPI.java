package com.pack.pack.rest.api;

import static com.pack.pack.util.SystemPropertyUtil.BROADCAST_API_PREFIX;

import javax.inject.Singleton;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path(BROADCAST_API_PREFIX)
public class BroadcastAPI {

	/*@POST
	@CompressWrite
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
	}*/
}