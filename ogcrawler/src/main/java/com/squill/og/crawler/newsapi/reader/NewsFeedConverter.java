package com.squill.og.crawler.newsapi.reader;

import java.util.List;

import org.joda.time.DateTime;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
public class NewsFeedConverter {
	
	static JRssFeeds convert(NewsFeeds newsFeeds, DefaultFeedTypeResolver typeResolver) {
		JRssFeeds result = new JRssFeeds();
		List<NewsFeed> articles = newsFeeds.getArticles();
		for(NewsFeed article : articles) {
			JRssFeed r = convert(article, typeResolver);
			result.getFeeds().add(r);
		}
		return result;
	}
	
	static JRssFeed convert(NewsFeed article, DefaultFeedTypeResolver typeResolver) {
		JRssFeed result = new JRssFeed();
		DateTime dateTime = new DateTime();
		result.setId(String.valueOf(dateTime.getMillis()));
		result.setUploadTime(dateTime.getMillis());
		result.setOgTitle(article.getTitle());
		result.setOgDescription(article.getDescription());
		result.setOgImage(article.getUrlToImage());
		result.setOgUrl(article.getUrl());
		result.setHrefSource(article.getUrl());
		result.setCreatedBy(article.getAuthor());
		
		result.setFeedType(typeResolver.resolveDefaultFeedType(article).name());
		
		return result;
	}
}