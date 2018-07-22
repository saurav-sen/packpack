package com.pack.pack.ml.rest.api;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.markup.gen.IMarkup;
import com.pack.pack.markup.gen.MarkupGenerator;
import com.pack.pack.model.web.JSharedFeed;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/public")
public class PublicResource {

	private static final Logger LOG = LoggerFactory
			.getLogger(PublicResource.class);

	@GET
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
	}

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
