package com.squill.og.crawler.internal.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.markup.gen.IMarkup;
import com.pack.pack.markup.gen.MarkupGenerator;
import com.pack.pack.model.web.JSharedFeed;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.SystemPropertyUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;

/**
 * 
 * @author Saurav
 *
 */
public class HtmlUtil {
	
	private static final Logger $_LOG = LoggerFactory.getLogger(HtmlUtil.class);
	
	private HtmlUtil() {
	}
	
	public static String cleanIllegalCharacters4mUrl(String url) {
		if(url == null) {
			return url;
		}
		return url.replaceAll("\\\\", "/").replaceAll("//", "/")
				.replaceFirst("http:/", "http://")
				.replaceFirst("https:/", "https://");
	}

	public static JRssFeed parse4mHtml(String htmlContent) {
		Document doc = Jsoup.parse(htmlContent);

		String title = null;
		Elements metaOgTitle = doc.select("meta[property=og:title]");
		if (metaOgTitle != null) {
			title = metaOgTitle.attr("content");
		}
		
		String pageTile = null;
		Elements docTile = doc.select("title");
		if (docTile != null) {
			pageTile = docTile.val();
		}

		if (title == null) {
			title = pageTile;
		}

		String description = null;
		Elements metaOgDescription = doc.select("meta[property=og:description]");
		if (metaOgDescription != null) {
			description = metaOgDescription.attr("content");
		}
		
		String pageDescription = null;
		Elements docDescription = doc.select("meta[name=description]");
		if (docDescription != null) {
			pageDescription = docDescription.attr("content");
		}

		if (description == null) {
			description = pageDescription;
		} else if (pageDescription != null
				&& pageDescription.length() > description.length()) {
			description = pageDescription;
		}

		String type = null;
		Elements metaOgType = doc.select("meta[property=og:type]");
		if (metaOgType != null) {
			type = metaOgType.attr("content");
		}

		String imageUrl = null;
		Elements metaOgImage = doc.select("meta[property=og:image]");
		if (metaOgImage != null) {
			imageUrl = metaOgImage.attr("content");
		}

		String hrefUrl = null;
		Elements metaOgUrl = doc.select("meta[property=og:url]");
		if (metaOgUrl != null) {
			hrefUrl = metaOgUrl.attr("content");
		}

		JRssFeed feed = new JRssFeed();
		feed.setOgTitle(title);
		feed.setOgDescription(description);
		feed.setOgImage(imageUrl);
		feed.setOgUrl(hrefUrl);
		feed.setHrefSource(hrefUrl);
		feed.setOgType(type);
		
		return feed;
	}
	
	public static void generateNewsFeedsHtmlProxyPages(JRssFeeds feeds, TTL ttl, long batchId,
			boolean sendNotification) {
		List<JRssFeed> newFeeds = feeds.getFeeds();
		for (JRssFeed newFeed : newFeeds) {
			String id = newFeed.getShareableUrl();
			if(id.endsWith("/")) {
				id = id.substring(0, id.length() - 1);
			}
			id = id.substring(id.lastIndexOf("/") + 1);
			try {
				String html = generateExternallySharedProxyPage(id);
				String htmlFolder = SystemPropertyUtil
						.getDefaultArchiveHtmlFolder();
				if (!htmlFolder.endsWith(File.separator)
						&& !htmlFolder.endsWith("/")) {
					htmlFolder = htmlFolder + File.separator;
				}
				Files.write(Paths.get(htmlFolder + id), html.getBytes(),
						StandardOpenOption.CREATE_NEW);
			} catch (PackPackException e) {
				$_LOG.error(e.getMessage(), e);
			} catch (IOException e) {
				$_LOG.error(e.getMessage(), e);
			}
		}
	}
	
	private static String generateExternallySharedProxyPage(String id)
			throws PackPackException {
		try {
			Markup markup = new Markup();
			MarkupGenerator.INSTANCE.generateMarkup(id, JSharedFeed.class,
					markup);
			return markup.getContent();
		} catch (Exception e) {
			$_LOG.error("Promotion failed");
			$_LOG.error(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61,
					"Failed Generating Proxy Page for Shared Link", e);
		}
	}
	
	private static class Markup implements IMarkup {

		@SuppressWarnings("unused")
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
