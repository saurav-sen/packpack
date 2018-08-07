package com.pack.pack.rest.api;

import java.net.URI;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.pack.pack.markup.gen.IMarkup;
import com.pack.pack.model.web.JSharedFeed;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.UrlShortener;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/sh")
public class sharedResources {

	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_HTML)
	public Response getExternallySharedProxyPage(@PathParam("id") String id)
			throws PackPackException {
		JSharedFeed sharedFeed = UrlShortener.readShortUrlInfo(id);
		String actualUrl = null;
		if(sharedFeed == null) {
			actualUrl = sharedFeed.getActualUrl();
		} else {
			// TODO -- generate 404 error page here (With nice message stating that, it has expired).
			actualUrl = SystemPropertyUtil.getBaseURL();
		}
		return Response.seeOther(URI.create(actualUrl)).build();
	}
	
	/*@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_HTML)
	public String getExternallySharedProxyPage(@PathParam("id") String id)
			throws PackPackException {
		try {
			Markup markup = new Markup();
			MarkupGenerator.INSTANCE.generateMarkup(id, JSharedFeed.class,
					markup);
			return markup.getContent();
		} catch (Exception e) {
			LOG.error("Promotion failed");
			LOG.error(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61,
					"Failed Generating Proxy Page for Shared Link", e);
		}
	}*/

	private class Markup implements IMarkup {

		private String contentType;

		private String content;

		private Markup() {
		}

		@Override
		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		@Override
		public void setContent(String content) {
			this.content = content;
		}

		private String getContent() {
			return content;
		}
	}
}
