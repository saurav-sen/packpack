package com.squill.og.crawler.internal.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.squill.feed.web.model.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public class HtmlUtil {
	
	private HtmlUtil() {
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
}
