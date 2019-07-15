package com.squill.og.crawler.internal.utils;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.og.crawler.external.feed.ExtFeedEntry;
import com.squill.utils.HtmlUtil;

/**
 * 
 * @author Saurav
 *
 */
public class WebFeedSpiderUtil {

	public static List<JRssFeed> converAll(List<ExtFeedEntry> extFeeds, String baseUrl)
			throws Exception {
		List<JRssFeed> feeds = new ArrayList<JRssFeed>();
		if (extFeeds != null && !extFeeds.isEmpty()) {
			for (ExtFeedEntry extFeed : extFeeds) {
				JRssFeed feed = new JRssFeed();
				feed.setOgTitle(extFeed.getTitle());
				feed.setCreatedBy(extFeed.getAuthor());
				feed.setFeedType(JRssFeedType.NEWS.name());
				feed.setOgDescription(extFeed.getDescription());
				feed.setHrefSource(extFeed.getLink());
				feed.setOgUrl(HtmlUtil.resolveAbsoluteOgUrl(baseUrl, extFeed.getLink()));

				String link = extFeed.getLink();
				String html = new HttpRequestExecutor().GET(link);
				JRssFeed f = readFromHtml(link, html);
				
				if (f != null) {
					feed = f;
				}
				feed.setId(extFeed.getGuid());
				feeds.add(feed);
			}
		}
		return feeds;
	}

	private static JRssFeed readFromHtml(String link, String html) throws Exception {
		String content = html;
		if(content == null || content.trim().isEmpty()) {
			content = new HttpRequestExecutor().GET(link);
		}
		Document doc = Jsoup.parse(content);

		String title = null;
		Elements metaOgTitle = doc.select("meta[property=og:title]");
		if (metaOgTitle != null) {
			title = metaOgTitle.attr("content");
		}

		if (title == null) {
			metaOgTitle = doc.select("meta[property=twitter:title]");
			if (metaOgTitle != null) {
				title = metaOgTitle.attr("content");
			}
		}

		String pageTile = null;
		Elements docTile = doc.select("title");
		if (docTile != null) {
			pageTile = docTile.val();
		}

		if (title == null) {
			title = pageTile;
		}

		if (title == null)
			return null;

		String description = null;
		Elements metaOgDescription = doc
				.select("meta[property=og:description]");
		if (metaOgDescription != null) {
			description = metaOgDescription.attr("content");
		}

		if (description == null) {
			metaOgDescription = doc
					.select("meta[property=twitter:description]");
			if (metaOgDescription != null) {
				description = metaOgDescription.attr("content");
			}
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

		String imageUrl = null;
		Elements metaOgImage = doc.select("meta[property=og:image]");
		if (metaOgImage != null) {
			imageUrl = metaOgImage.attr("content");
		}

		if (imageUrl == null) {
			metaOgImage = doc.select("meta[property=twitter:image]");
			if (metaOgImage != null) {
				imageUrl = metaOgImage.attr("content");
			}
		}

		String hrefUrl = null;
		Elements metaOgUrl = doc.select("meta[property=og:url]");
		if (metaOgUrl != null) {
			hrefUrl = metaOgUrl.attr("content");
		}

		if (hrefUrl == null) {
			metaOgUrl = doc.select("meta[property=twitter:url]");
			if (metaOgUrl != null) {
				hrefUrl = metaOgUrl.attr("content");
			}
		}

		JRssFeed feed = new JRssFeed();
		feed.setUploadTime(System.currentTimeMillis());
		feed.setOgTitle(title);
		feed.setOgDescription(description);
		feed.setOgImage(HtmlUtil.resolveAbsoluteOgUrl(link, imageUrl));
		feed.setOgUrl(HtmlUtil.resolveAbsoluteOgUrl(link,
				(hrefUrl != null ? hrefUrl : link)));
		feed.setHrefSource(HtmlUtil.resolveAbsoluteOgUrl(link,
				(hrefUrl != null ? hrefUrl : link)));

		return feed;
	}
}
