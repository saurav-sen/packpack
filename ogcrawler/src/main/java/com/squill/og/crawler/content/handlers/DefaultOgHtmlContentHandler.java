package com.squill.og.crawler.content.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.og.crawler.ILink;
import com.squill.og.crawler.hooks.IHtmlContentHandler;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.internal.utils.CoreConstants;
import com.squill.og.crawler.model.web.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
@Component("defaultOgHtmlContentHandler")
@Scope("prototype")
public class DefaultOgHtmlContentHandler implements IHtmlContentHandler {

	private Map<String, List<JRssFeed>> feeds = new HashMap<String, List<JRssFeed>>();

	private int flushFrequency = 50;

	private int thresholdFrequency = 10;

	private Map<String, Object> metaInfoMap = new HashMap<String, Object>(2);
	
	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultOgHtmlContentHandler.class);
	
	@Override
	public void preProcess(ILink link, ISpiderSession session) {
	}

	@Override
	public void postProcess(String htmlContent, ILink link, ISpiderSession session) {
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
		
		String preClassifiedFeedType = (String) getMetaInfo(CoreConstants.PRE_CLASSIFIED_FEED_TYPE);
		if (preClassifiedFeedType != null) {
			feed.setPreClassifiedType(preClassifiedFeedType);
		}
		
		String domainUrl = link.getRoot().getDomainUrl();
		List<JRssFeed> list = feeds.get(domainUrl);
		if(list == null) {
			list = new ArrayList<JRssFeed>();
			feeds.put(domainUrl, list);
		}
		list.add(feed);
		/*
		 * try { System.out.println(JSONUtil.serialize(feed)); } catch
		 * (PackPackException e) { e.printStackTrace(); }
		 */
	}
	
	@Override
	public Map<String, List<JRssFeed>> getCollectiveFeeds(ISpiderSession session) {
		if(feeds == null || feeds.isEmpty()) {
			LOG.warn("Skipping Uploading empty list of feeds recceived from og-crawler");
			return null;
		}
		Map<String, List<JRssFeed>> feedsMap = new HashMap<String, List<JRssFeed>>();
		feedsMap.putAll(feeds);
		feeds.clear();
		return feedsMap;
	}

	@Override
	public int getThresholdFrequency() {
		return thresholdFrequency;
	}

	@Override
	public void setThresholdFrequency(int thresholdFrequency) {
		this.thresholdFrequency = thresholdFrequency;
	}

	@Override
	public int getFlushFrequency() {
		return flushFrequency;
	}

	@Override
	public void setFlushFrequency(int flushFrequency) {
		this.flushFrequency = flushFrequency;
	}

	protected Object getMetaInfo(String key) {
		return metaInfoMap.get(key);
	}

	@Override
	public void addMetaInfo(String key, Object value) {
		metaInfoMap.put(key, value);
	}
}