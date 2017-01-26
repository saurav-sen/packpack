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
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.IHtmlContentHandler;
import com.squill.og.crawler.internal.utils.CoreConstants;
import com.squill.og.crawler.internal.utils.FeedClassifierUtil;
import com.squill.og.crawler.model.web.FeedClassifier;
import com.squill.og.crawler.model.web.JRssFeed;
import com.squill.og.crawler.model.web.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
@Component("defaultOgHtmlContentHandler")
@Scope("prototype")
public class DefaultOgHtmlContentHandler implements IHtmlContentHandler {

	private List<JRssFeed> feeds = new ArrayList<JRssFeed>();

	private int flushFrequency = 50;

	private int thresholdFrequency = 10;

	private IFeedUploader feedUploader;
	
	private Map<String, Object> metaInfoMap = new HashMap<String, Object>(2);
	
	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultOgHtmlContentHandler.class);
	
	@Override
	public void preProcess(ILink link) {
	}

	@Override
	public void postProcess(String htmlContent, ILink link) {
		Document doc = Jsoup.parse(htmlContent);

		String title = null;
		Elements metaOgTitle = doc.select("meta[property=og:title]");
		if (metaOgTitle != null) {
			title = metaOgTitle.attr("content");
		}

		String description = null;
		Elements metaOgDescription = doc.select("meta[property=og:title]");
		if (metaOgDescription != null) {
			description = metaOgDescription.attr("content");
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

		feeds.add(feed);
		/*
		 * try { System.out.println(JSONUtil.serialize(feed)); } catch
		 * (PackPackException e) { e.printStackTrace(); }
		 */
	}

	@Override
	public void postComplete() {
		if(feeds == null || feeds.isEmpty()) {
			LOG.warn("Skipping Uploading empty list of feeds recceived from og-crawler");
			return;
		}
		List<JRssFeed> list = new ArrayList<JRssFeed>();
		list.addAll(feeds);
		feeds.clear();
		uploadAll(list);
		list.clear();
	}

	private void uploadAll(List<JRssFeed> feeds) {
		try {
			JRssFeeds rssFeeds = new JRssFeeds();
			for (JRssFeed feed : feeds) {
				classify(feed);
				rssFeeds.getFeeds().add(feed);
			}
			if(feedUploader != null) {
				feedUploader.uploadBulk(rssFeeds);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void classify(JRssFeed feed) {
		FeedClassifier classifier = FeedClassifierUtil.classify(feed);
		if (classifier != null) {
			feed.setOgType(classifier.name());
		}
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

	@Override
	public void flush() {
		postComplete();
	}

	@Override
	public void setFeedUploader(IFeedUploader feedUploader) {
		this.feedUploader = feedUploader;
	}

	protected Object getMetaInfo(String key) {
		return metaInfoMap.get(key);
	}

	@Override
	public void addMetaInfo(String key, Object value) {
		metaInfoMap.put(key, value);
	}
}