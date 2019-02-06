package com.pack.pack.ml.rest.api;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.dto.BookmarkDTO;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.ext.text.summerize.WebDocumentParser;
import com.pack.pack.services.registry.ServiceRegistry;
import com.squill.feed.web.model.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/bookmark")
public class BookmarkResource {

	private static final Logger $LOG = LoggerFactory
			.getLogger(BookmarkResource.class);

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JRssFeed processArticleLink(String json) throws PackPackException {
		BookmarkDTO dto = JSONUtil.deserialize(json, BookmarkDTO.class, true);
		WebDocumentParser parser = ServiceRegistry.INSTANCE
				.findService(WebDocumentParser.class);
		JRssFeed feed = parser.setUrl(dto.getHyperlink()).parse();
		if(feed.getHtmlSnippet() != null && !feed.getHtmlSnippet().trim().isEmpty()) {
			feed.setFullArticleText(feed.getHtmlSnippet());
		}
		$LOG.info("********************************************************************************************");
		$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(feed)));
		$LOG.info("********************************************************************************************");
		return feed;
	}
}
