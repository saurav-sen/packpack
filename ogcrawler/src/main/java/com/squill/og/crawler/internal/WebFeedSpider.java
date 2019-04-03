package com.squill.og.crawler.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.og.crawler.IRssSite;
import com.squill.og.crawler.Spider;
import com.squill.og.crawler.SpiderSession;
import com.squill.og.crawler.external.feed.ExtFeed;
import com.squill.og.crawler.external.feed.ExtFeedEntry;
import com.squill.og.crawler.external.feed.ExternalRssFeedParser;
import com.squill.og.crawler.hooks.IFeedHandler;
import com.squill.og.crawler.hooks.IFeedUploader;
import com.squill.og.crawler.hooks.IWebLinkTrackerService;
import com.squill.og.crawler.internal.utils.HttpRequestExecutor;

/**
 * 
 * @author Saurav
 *
 */
public class WebFeedSpider implements Spider {

	private IRssSite rssSite;
	
	private static Logger LOG = LoggerFactory.getLogger(WebFeedSpider.class);
	
	private SpiderSession session;
	
	private IFeedHandler feedHandler;
	
	public WebFeedSpider(IRssSite rssSite, long crawlSchedulePeriodicTimeInMillis, IWebLinkTrackerService tracker, IFeedHandler feedHandler, SpiderSession session) {
		this.rssSite = rssSite;
		this.feedHandler = feedHandler;
		this.session = session;
	}

	@Override
	public void run() {
		if(session.isThresholdReached())
			return;
		IFeedUploader feedUploader = session.getFeedUploader();
		try {
			if (feedHandler == null) {
				session.begin(rssSite);
				feedUploader.beginEach(session, rssSite);
			}
			ExtFeed extFeed = new ExternalRssFeedParser(
					rssSite.getRssFeedUrl()).parse();
			Map<String, List<JRssFeed>> feedsMap = new HashMap<String, List<JRssFeed>>();
			List<JRssFeed> feeds = converAll(extFeed.getEntries());
			feedsMap.put(rssSite.getUniqueId(), feeds);
			if (feedHandler == null) {
				new AllInOneAITaskExecutor(session).executeTasks(feedsMap,
						rssSite);
				feedUploader.endEach(session, rssSite);
			} else {
				if(feedHandler.isExecuteAItaks()) {
					new AllInOneAITaskExecutor(session).executeTasks(feedsMap,
							rssSite);
				}
				feedHandler.handleReceived(feedsMap, session, rssSite);
			}
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		} finally {
			session.end(rssSite);
		}
	}
	
	private List<JRssFeed> converAll(List<ExtFeedEntry> extFeeds) throws Exception {
		List<JRssFeed> feeds = new ArrayList<JRssFeed>();
		if(extFeeds != null && !extFeeds.isEmpty()) {
			for(ExtFeedEntry extFeed : extFeeds) {
				JRssFeed feed = new JRssFeed();
				feed.setOgTitle(extFeed.getTitle());
				feed.setCreatedBy(extFeed.getAuthor());
				feed.setFeedType(JRssFeedType.NEWS.name());
				feed.setOgDescription(extFeed.getDescription());
				feed.setHrefSource(extFeed.getLink());
				feed.setOgUrl(extFeed.getLink());
				
				String ogTitle = feed.getOgTitle();
				if(ogTitle == null || ogTitle.trim().isEmpty()) {
					String html = new HttpRequestExecutor().GET(extFeed.getLink());
					feed = readFromHtml(html);
				}
				if(feed != null) {
					feed.setId(extFeed.getGuid());
					feeds.add(feed);
				}
			}
		}
		return feeds;
	}
	
	public JRssFeed readFromHtml(String link) throws Exception {
		String html = new HttpRequestExecutor().GET(link);
		Document doc = Jsoup.parse(html);

		String title = null;
		Elements metaOgTitle = doc.select("meta[property=og:title]");
		if (metaOgTitle != null) {
			title = metaOgTitle.attr("content");
		}
		
		if(title == null) {
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
		
		if(title == null)
			return null;

		String description = null;
		Elements metaOgDescription = doc.select("meta[property=og:description]");
		if (metaOgDescription != null) {
			description = metaOgDescription.attr("content");
		}
		
		if(description == null) {
			metaOgDescription = doc.select("meta[property=twitter:description]");
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
		
		if(imageUrl == null) {
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
		
		if(hrefUrl == null) {
			metaOgUrl = doc.select("meta[property=twitter:url]");
			if (metaOgUrl != null) {
				hrefUrl = metaOgUrl.attr("content");
			}
		}

		JRssFeed feed = new JRssFeed();
		feed.setUploadTime(System.currentTimeMillis());
		feed.setOgTitle(title);
		feed.setOgDescription(description);
		feed.setOgImage(imageUrl);
		feed.setOgUrl(hrefUrl != null ? hrefUrl : link);
		feed.setHrefSource(hrefUrl != null ? hrefUrl : link);
		
		return feed;
	}
}
