package com.squill.og.crawler.internal.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
		if (url == null) {
			return url;
		}
		return url.replaceAll("\\\\", "/").replaceAll("//", "/")
				.replaceFirst("http:/", "http://")
				.replaceFirst("https:/", "https://");
	}
	
	public static String cleanUTFCharacters(String text) {
		if(text == null) {
			return text;
		}
		return text.replaceAll("[^\\x00-\\x7F]","");
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
		Elements metaOgDescription = doc
				.select("meta[property=og:description]");
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

	public static void generateNewsFeedsHtmlPages(JRssFeeds feeds) {
		List<JRssFeed> rFeeds = feeds.getFeeds();
		for (JRssFeed rFeed : rFeeds) {
			String id = rFeed.getShareableUrl();
			if (id.endsWith("/")) {
				id = id.substring(0, id.length() - 1);
			}
			id = id.substring(id.lastIndexOf("/") + 1);
			try {
				String html = generateExternallySharedPage(id, rFeed);
				String htmlFolder = SystemPropertyUtil
						.getDefaultArchiveHtmlFolder();
				if (!htmlFolder.endsWith(File.separator)
						&& !htmlFolder.endsWith("/")) {
					htmlFolder = htmlFolder + File.separator;
				}
				Files.write(Paths.get(htmlFolder + id), html.getBytes(),
						StandardOpenOption.CREATE);
				String baseUrl = SystemPropertyUtil
						.getExternalSharedLinkBaseUrl();
				if (!baseUrl.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
					baseUrl = baseUrl + SystemPropertyUtil.URL_SEPARATOR;
				}
				rFeed.setShareableUrl(baseUrl + id);

				String today = today();
				html = generateFullTextPage(id, rFeed);
				htmlFolder = SystemPropertyUtil.getDefaultArchiveHtmlFolder();
				if (!htmlFolder.endsWith(File.separator)
						&& !htmlFolder.endsWith("/")) {
					htmlFolder = htmlFolder + File.separator + today;
					Path dirPath = Paths.get(htmlFolder);
					if (!Files.exists(dirPath)) {
						Files.createDirectories(dirPath);
					}
					htmlFolder = htmlFolder + File.separator;
				}
				Files.write(Paths.get(htmlFolder + id), html.getBytes(),
						StandardOpenOption.CREATE);
				rFeed.setSquillUrl(baseUrl + today
						+ SystemPropertyUtil.URL_SEPARATOR + id);
			} catch (PackPackException e) {
				$_LOG.error(e.getMessage(), e);
			} catch (IOException e) {
				$_LOG.error(e.getMessage(), e);
			}
		}
	}

	private static String today() {
		return DateTimeUtil.today().replaceAll("/", "_");
	}

	private static String generateFullTextPage(String id, JRssFeed feed)
			throws PackPackException {
		try {
			Markup markup = new Markup();
			MarkupGenerator.INSTANCE.generateMarkup(feed, markup);
			return markup.getContent();
		} catch (Exception e) {
			$_LOG.error("Promotion failed");
			$_LOG.error(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61,
					"Failed Generating Proxy Page for Shared Link", e);
		}
	}

	private static String generateExternallySharedPage(String id,
			JRssFeed feed) throws PackPackException {
		try {
			Markup markup = new Markup();
			JSharedFeed sh = new JSharedFeed();
			sh.setActualUrl(feed.getHrefSource());
			sh.setDescription(feed.getOgDescription());
			sh.setImageLink(feed.getOgImage());
			sh.setSummaryText(feed.getArticleSummaryText());
			sh.setTitle(feed.getOgTitle());
			MarkupGenerator.INSTANCE.generateMarkup(sh, markup);
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
