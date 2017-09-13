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
public class BroadcastResource {

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
	
	/*@POST
	@Path("share/external")
	@Consumes(value = MediaType.TEXT_PLAIN)
	@Produces(value = MediaType.APPLICATION_JSON)
	public JRssFeed getShareableResourceInformation(String externalPublicLink)
			throws Exception {
		JRssFeed feed = doCrawlExternalPublicLink(externalPublicLink);
		String shareableUrl = UrlShortener.calculateShortenShareableUrl(feed);
		feed.setOgUrl(shareableUrl);
		return feed;
	}*/
	
	/*private JRssFeed doCrawlExternalPublicLink(String externalPublicLink) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpGet GET = new HttpGet(externalPublicLink);
		HttpResponse response = client.execute(GET);
		int statusCode = response.getStatusLine().getStatusCode();
		while(statusCode == 302) {
			Header header = response.getFirstHeader("location");
			String url = header.getValue();
			GET = new HttpGet(url);
			response = client.execute(GET);
			statusCode = response.getStatusLine().getStatusCode();
		}
		if(statusCode == 200) {
			String htmlContent = EntityUtils.toString(response.getEntity());
			Document doc = Jsoup.parse(htmlContent);
			
			String title = null;
			Elements metaOgTitle = doc.select("meta[property=og:title]");
			if (metaOgTitle != null) {
				title = metaOgTitle.attr("content");
			}

			String description = null;
			Elements metaOgDescription = doc.select("meta[property=og:description]");
			if (metaOgDescription != null) {
				description = metaOgDescription.attr("content");
			}

			String imageUrl = null;
			Elements metaOgImage = doc.select("meta[property=og:image]");
			if (metaOgImage != null) {
				imageUrl = metaOgImage.attr("content");
			}

			String resourceUrl = externalPublicLink;
			
			JRssFeed feed = new JRssFeed();
			feed.setOgTitle(title);
			feed.setOgDescription(description);
			feed.setOgImage(imageUrl);
			feed.setOgUrl(resourceUrl);
			return feed;
		}
		return null;
	}*/
}