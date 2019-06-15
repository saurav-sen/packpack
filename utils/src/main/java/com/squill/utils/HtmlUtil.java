package com.squill.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.markup.gen.IMarkup;
import com.pack.pack.markup.gen.MarkupGenerator;
import com.pack.pack.model.web.JSharedFeed;
import com.pack.pack.model.web.ShortenUrlInfo;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.Base62;
import com.pack.pack.services.redis.UrlShortener;
import com.pack.pack.util.LanguageUtil;
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
		if (text == null) {
			return text;
		}
		//return LanguageUtil.foldToASCII(text);
		return LanguageUtil.foldToASCII(text.replaceAll("[^\\x00-\\x7F]", ""));
	}
	
	/*public static void main(String[] args) {
		System.out.println("লোকাল নেটওয়ার্কে চ্যাটিং পপম্যাসেঞ্জার দিয়ে");
		System.out.println(cleanUTFCharacters("লোকাল নেটওয়ার্কে চ্যাটিং পপম্যাসেঞ্জার দিয়ে."));
	}*/

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

		/*String type = null;
		Elements metaOgType = doc.select("meta[property=og:type]");
		if (metaOgType != null) {
			type = metaOgType.attr("content");
		}*/

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
		//feed.setOgType(type);
		
		try {
			URL url = new URL(hrefUrl);
			if(url.getHost().contains("youtube.") || url.getHost().contains("youtu.be")) {
				feed.setVideoUrl(hrefUrl);
			}
		} catch (MalformedURLException e) {
			$_LOG.error(e.getMessage(), e);
		}

		return feed;
	}

	public static boolean isSharedPageFileExists(JRssFeed rFeed) {
		String pageId = resolveHtmlPageId(rFeed);
		if (pageId == null)
			return false;
		String url = rFeed.getShareableUrl();
		if (url == null) {
			return false;
		}
		if (!url.endsWith(pageId)
				&& !url.endsWith(pageId + SystemPropertyUtil.URL_SEPARATOR)) {
			return false;
		}
		return isPageFileExists(url);
	}

	public static boolean isFullPageFileExists(JRssFeed rFeed) {
		String pageId = resolveHtmlPageId(rFeed);
		if (pageId == null)
			return false;
		String url = rFeed.getSquillUrl();
		if (url == null) {
			return false;
		}
		if (!url.endsWith(pageId)
				&& !url.endsWith(pageId + SystemPropertyUtil.URL_SEPARATOR)) {
			return false;
		}
		return isPageFileExists(url);
	}

	private static boolean isPageFileExists(String url) {
		if (url.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
			url = url.substring(0, url.length() - 1);
		}
		String baseUrl = SystemPropertyUtil.getExternalSharedLinkBaseUrl();
		if (!baseUrl.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
			baseUrl = baseUrl + SystemPropertyUtil.URL_SEPARATOR;
		}
		$_LOG.trace(url);
		String path = url.substring(baseUrl.length());
		path = path
				.replaceAll(SystemPropertyUtil.URL_SEPARATOR, File.separator);
		String htmlFolder = SystemPropertyUtil.getDefaultArchiveHtmlFolder();
		if (!htmlFolder.endsWith(File.separator) && !htmlFolder.endsWith("/")) {
			htmlFolder = htmlFolder + File.separator;
		}
		return Files.exists(Paths.get(htmlFolder + path));
	}

	public static String resolveHtmlPageId(JRssFeed rFeed) {
		String id = rFeed.getShareableUrl();
		String baseUrl = SystemPropertyUtil.getExternalSharedLinkBaseUrl();
		if (!baseUrl.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
			baseUrl = baseUrl + SystemPropertyUtil.URL_SEPARATOR;
		}
		if (!id.startsWith(baseUrl))
			return null;
		if (id.equals(baseUrl))
			return null;
		id = id.substring(baseUrl.length());
		if (id != null) {
			if (id.startsWith(SystemPropertyUtil.URL_SEPARATOR)) {
				int i = id.indexOf(SystemPropertyUtil.URL_SEPARATOR);
				if (i >= 0) {
					id = id.substring(i
							+ SystemPropertyUtil.URL_SEPARATOR.length());
				}
			}
			if (id.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
				id = id.substring(0,
						id.lastIndexOf(SystemPropertyUtil.URL_SEPARATOR));
			}
		}
		return id;
	}

	public static void generateNewsFeedsSharedHtmlPage(JRssFeed rFeed) {
		String id = rFeed.getShareableUrl();
		if (id == null) {
			boolean storeSharedFeed = false;
			ShortenUrlInfo shortenUrlInfo;
			try {
				shortenUrlInfo = UrlShortener.calculateShortenShareableUrl(
						rFeed,
						SystemPropertyUtil.getExternalSharedLinkBaseUrl(),
						storeSharedFeed);
				rFeed.setShareableUrl(shortenUrlInfo.getUrl());
			} catch (PackPackException e) {
				$_LOG.error(e.getMessage(), e);
				return;
			}
			id = rFeed.getShareableUrl();
		}
		if (id.endsWith("/")) {
			id = id.substring(0, id.length() - 1);
		}
		id = id.substring(id.lastIndexOf("/") + 1);
		try {
			String html = generateExternallySharedPage(rFeed);
			String htmlFolder = SystemPropertyUtil
					.getDefaultArchiveHtmlFolder();
			if (!htmlFolder.endsWith(File.separator)
					&& !htmlFolder.endsWith("/")) {
				htmlFolder = htmlFolder + File.separator;
			}
			while (Files.exists(Paths.get(htmlFolder + id))) {
				$_LOG.debug("ID Conflict for page generation. Resolving ... id = " + id);
				int decode = Base62.getDecoder().decode(id)
						+ new Random().nextInt();
				id = Base62.getEncoder().encode(decode);
			}
			Files.write(Paths.get(htmlFolder + id), html.getBytes(),
					StandardOpenOption.CREATE);
			String baseUrl = SystemPropertyUtil.getExternalSharedLinkBaseUrl();
			if (!baseUrl.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
				baseUrl = baseUrl + SystemPropertyUtil.URL_SEPARATOR;
			}
			rFeed.setShareableUrl(baseUrl + id);
		} catch (PackPackException e) {
			$_LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			$_LOG.error(e.getMessage(), e);
		}
	}

	public static void generateNewsFeedsFullHtmlPage(JRssFeed rFeed) {
		$_LOG.debug("generateNewsFeedsFullHtmlPage(...)");
		String id = rFeed.getShareableUrl();
		if (id == null) {
			boolean storeSharedFeed = false;
			ShortenUrlInfo shortenUrlInfo;
			try {
				shortenUrlInfo = UrlShortener.calculateShortenShareableUrl(
						rFeed,
						SystemPropertyUtil.getExternalSharedLinkBaseUrl(),
						storeSharedFeed);
				rFeed.setShareableUrl(shortenUrlInfo.getUrl());
			} catch (PackPackException e) {
				$_LOG.error(e.getMessage(), e);
				return;
			}
			id = rFeed.getShareableUrl();
		}
		if (id.endsWith("/")) {
			id = id.substring(0, id.length() - 1);
		}
		id = id.substring(id.lastIndexOf("/") + 1);
		try {
			String today = today();
			String html = generateFullTextPage(rFeed);
			String htmlFolder = SystemPropertyUtil
					.getDefaultArchiveHtmlFolder();
			if (!htmlFolder.endsWith(File.separator)
					&& !htmlFolder.endsWith("/")) {
				htmlFolder = htmlFolder + File.separator + today;
				Path dirPath = Paths.get(htmlFolder);
				if (!Files.exists(dirPath)) {
					Files.createDirectories(dirPath);
				}
				htmlFolder = htmlFolder + File.separator;
			}
			while (Files.exists(Paths.get(htmlFolder + id))) {
				$_LOG.debug("ID Conflict for page generation. Resolving ... id = " + id);
				int decode = Base62.getDecoder().decode(id)
						+ new Random().nextInt();
				id = Base62.getEncoder().encode(decode);
			}
			String baseUrl = SystemPropertyUtil.getExternalSharedLinkBaseUrl();
			if (!baseUrl.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
				baseUrl = baseUrl + SystemPropertyUtil.URL_SEPARATOR;
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

	public static void generateNewsFeedsHtmlPages(JRssFeeds feeds) {
		$_LOG.debug("generateNewsFeedsHtmlPages(...)");
		List<JRssFeed> rFeeds = feeds.getFeeds();
		for (JRssFeed rFeed : rFeeds) {
			String id = rFeed.getShareableUrl();
			if (id == null) {
				boolean storeSharedFeed = false;
				ShortenUrlInfo shortenUrlInfo;
				try {
					shortenUrlInfo = UrlShortener.calculateShortenShareableUrl(
							rFeed,
							SystemPropertyUtil.getExternalSharedLinkBaseUrl(),
							storeSharedFeed);
					rFeed.setShareableUrl(shortenUrlInfo.getUrl());
				} catch (PackPackException e) {
					$_LOG.error(e.getMessage(), e);
					continue;
				}
				id = rFeed.getShareableUrl();
			}
			if (id.endsWith("/")) {
				id = id.substring(0, id.length() - 1);
			}
			id = id.substring(id.lastIndexOf("/") + 1);
			try {
				String html = generateExternallySharedPage(rFeed);
				String htmlFolder = SystemPropertyUtil
						.getDefaultArchiveHtmlFolder();
				if (!htmlFolder.endsWith(File.separator)
						&& !htmlFolder.endsWith("/")) {
					htmlFolder = htmlFolder + File.separator;
				}
				while (Files.exists(Paths.get(htmlFolder + id))) {
					$_LOG.debug("ID Conflict for page generation. Resolving ...");
					int decode = Base62.getDecoder().decode(id)
							+ new Random().nextInt();
					id = Base62.getEncoder().encode(decode);
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
				html = generateFullTextPage(rFeed);
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
				while (Files.exists(Paths.get(htmlFolder + id))) {
					$_LOG.debug("ID Conflict for page generation. Resolving ...");
					int decode = Base62.getDecoder().decode(id)
							+ new Random().nextInt();
					id = Base62.getEncoder().encode(decode);
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

	private static String generateFullTextPage(JRssFeed feed)
			throws PackPackException {
		boolean resetImageToNULL = false;
		try {
			Markup markup = new Markup();
			String ogImage = feed.getOgImage();
			if (ogImage == null) {
				String url = feed.getOgUrl() != null ? feed.getOgUrl() : feed
						.getHrefSource();
				$_LOG.debug("[SharedPage Generation] ogImage is NULL for "
						+ url + " Setting it to default SquillShare.jpg");
				String baseUrl = SystemPropertyUtil
						.getExternalSharedLinkBaseUrl();
				if (!baseUrl.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
					baseUrl = baseUrl + SystemPropertyUtil.URL_SEPARATOR;
				}
				ogImage = baseUrl + "SquillShare.jpg";
				resetImageToNULL = true;
				feed.setOgImage(ogImage);
			}
			MarkupGenerator.INSTANCE.generateMarkup(feed, markup);
			return markup.getContent();
		} catch (Exception e) {
			$_LOG.error("Promotion failed");
			$_LOG.error(e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_61,
					"Failed Generating Proxy Page for Shared Link", e);
		} finally {
			if(resetImageToNULL) {
				feed.setOgImage(null);
			}
		}
	}

	private static String generateExternallySharedPage(JRssFeed feed)
			throws PackPackException {
		try {
			Markup markup = new Markup();
			JSharedFeed sh = new JSharedFeed();
			String url = feed.getOgUrl() != null ? feed.getOgUrl() : feed
					.getHrefSource();
			sh.setActualUrl(url);
			sh.setDescription(feed.getOgDescription());
			String ogImage = feed.getOgImage();
			if (ogImage == null) {
				$_LOG.debug("[SharedPage Generation] ogImage is NULL for "
						+ url + " Setting it to default SquillShare.jpg");
				String baseUrl = SystemPropertyUtil
						.getExternalSharedLinkBaseUrl();
				if (!baseUrl.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
					baseUrl = baseUrl + SystemPropertyUtil.URL_SEPARATOR;
				}
				ogImage = baseUrl + "SquillShare.jpg";
			}
			sh.setImageLink(ogImage);
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
