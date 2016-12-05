package com.squill.og.crawler.content.handlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.JRssFeeds;
import com.pack.pack.model.web.TTL;
import com.pack.pack.oauth.OAuthConstants;
import com.pack.pack.services.exception.PackPackException;
import com.squill.og.crawler.IHtmlContentHandler;
import com.squill.og.crawler.ILink;

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

		feeds.add(feed);
		/*
		 * try { System.out.println(JSONUtil.serialize(feed)); } catch
		 * (PackPackException e) { e.printStackTrace(); }
		 */
	}

	@Override
	public void postComplete() {
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
				categorize(feed);
				rssFeeds.getFeeds().add(feed);
			}
			DefaultHttpClient client = new DefaultHttpClient();
			String baseUrl = System.getProperty("base.url");
			baseUrl = baseUrl + "/home/bulk_upload";
			HttpPut PUT = new HttpPut(baseUrl);
			PUT.addHeader(OAuthConstants.AUTHORIZATION_HEADER, OAuthConstants.RSS_FEED_UPLOAD_API_KEY);
			String json = JSONUtil.serialize(rssFeeds);
			PUT.setEntity(new StringEntity(json));
			HttpResponse response = client.execute(PUT);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (PackPackException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void categorize(JRssFeed feed) {
		TTL ttl = new TTL();
		ttl.setTime((short) 1);
		ttl.setUnit(TimeUnit.DAYS);
		
		/*IRssFeedService service = ServiceRegistry.INSTANCE
				.findCompositeService(IRssFeedService.class);
		service.upload(feed, ttl);*/
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
}
