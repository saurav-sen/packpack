package com.squill.og.crawler.content.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.squill.og.crawler.IWebSite;
import com.squill.og.crawler.hooks.GeoLocation;
import com.squill.og.crawler.hooks.IArticleTextSummarizer;
import com.squill.og.crawler.hooks.IFeedClassificationResolver;
import com.squill.og.crawler.hooks.IGeoLocationResolver;
import com.squill.og.crawler.hooks.IHtmlContentHandler;
import com.squill.og.crawler.hooks.ISpiderSession;
import com.squill.og.crawler.hooks.ITaxonomyResolver;
import com.squill.og.crawler.internal.utils.CoreConstants;
import com.squill.og.crawler.internal.utils.FeedClassifierUtil;
import com.squill.og.crawler.model.web.JGeoTag;
import com.squill.og.crawler.model.web.JRssFeed;
import com.squill.og.crawler.model.web.JRssFeeds;
import com.squill.og.crawler.model.web.JTaxonomy;
import com.squill.og.crawler.text.summarizer.TextSummarization;

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
	public JRssFeeds postComplete(ISpiderSession session) {
		if(feeds == null || feeds.isEmpty()) {
			LOG.warn("Skipping Uploading empty list of feeds recceived from og-crawler");
			return null;
		}
		Map<String, List<JRssFeed>> feedsMap = new HashMap<String, List<JRssFeed>>();
		feedsMap.putAll(feeds);
		feeds.clear();
		deDuplicateFeeds(feedsMap);
		JRssFeeds rssFeeds = postComplete(feedsMap, session);
		feedsMap.clear();
		return rssFeeds;
	}
	
	private void deDuplicateFeeds(Map<String, List<JRssFeed>> feedsMap) {
		
	}

	private JRssFeeds postComplete(Map<String, List<JRssFeed>> feedsMap, ISpiderSession session) {
		IWebSite currentWebSite = session.getCurrentWebSite();
		String domainUrl = currentWebSite.getDomainUrl();
		IGeoLocationResolver geoLocationResolver = currentWebSite.getTargetLocationResolver();
		ITaxonomyResolver taxonomyResolver = currentWebSite.getTaxonomyResolver();
		JRssFeeds rssFeeds = new JRssFeeds();
		try {
			Iterator<String> itr = feedsMap.keySet().iterator();
			while(itr.hasNext()) {
				String key = itr.next();
				List<JRssFeed> feeds = feedsMap.get(key);
				if(feeds == null)
					continue;
				for (JRssFeed feed : feeds) {
					String classifier = classifyFeedType(feed);
					if(classifier != null) {
						feed.setOgType(classifier);
					}
					IArticleTextSummarizer articleTextSummarizer = currentWebSite
							.getArticleTextSummarizer();
					if (articleTextSummarizer != null) {
						TextSummarization response = articleTextSummarizer
								.summarize(feed.getOgUrl(), feed.getOgTitle(),
										feed.getOgDescription());
						if (response != null) {
							feed.setArticleSummaryText(response
									.extractedAllSummary(false));
							feed.setFullArticleText(response.getText());
						}
					}
					if(geoLocationResolver != null) {
						GeoLocation[] geoLocations = geoLocationResolver.resolveGeoLocations(feed.getOgUrl(), domainUrl, feed);
						if(geoLocations != null && geoLocations.length > 0) {
							for(GeoLocation geoLocation : geoLocations) {
								JGeoTag geoTag = new JGeoTag();
								geoTag.setLatitude(geoLocation.getLatitude());
								geoTag.setLongitude(geoLocation.getLongitude());
								feed.getGeoTags().add(geoTag);
							}
						}
					}
					
					if(taxonomyResolver != null) {
						JTaxonomy[] taxonomies = taxonomyResolver.resolveTaxonomies(feed.getOgUrl(), feed.getOgTitle());
						if(taxonomies != null && taxonomies.length > 0) {
							for(JTaxonomy taxonomy : taxonomies) {
								feed.getTaxonomies().add(taxonomy);
							}
						}
					}
					
					rssFeeds.getFeeds().add(feed);
				}
			}
			/*IFeedUploader feedUploader = currentWebSite.getFeedUploader();
			if(feedUploader != null) {
				feedUploader.uploadBulk(rssFeeds);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rssFeeds;
	}
	
	protected IFeedClassificationResolver getClassificationResolver() {
		return new IFeedClassificationResolver() {

			@Override
			public String resolvePrimaryClassifierType(String feedTitle,
					String feedDescription, String url) {
				// TODO Need to integrate AI based classifier engine here
				return null;
			}
			
			@Override
			public List<String> resolveIPTCTypes(String feedTitle,
					String feedDescription, String url) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	private String classifyFeedType(JRssFeed feed) {
		String classifier = FeedClassifierUtil.classify(feed);
		if (classifier == null) {
			IFeedClassificationResolver classificationResolver = getClassificationResolver();
			if (classificationResolver != null) {
				return classificationResolver.resolvePrimaryClassifierType(
						feed.getOgTitle(), feed.getOgDescription(),
						feed.getOgUrl());
			}
		}
		return null;
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