package com.squill.news.reader;

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
	
	public static JRssFeeds convert(NewsFeeds newsFeeds, String feedType) {
		JRssFeeds result = new JRssFeeds();
		List<NewsFeed> articles = newsFeeds.getArticles();
		for(NewsFeed article : articles) {
			JRssFeed r = convert(article, feedType);
			result.getFeeds().add(r);
		}
		return result;
	}
	
	public static JRssFeed convert(NewsFeed article, String feedType) {
		JRssFeed result = new JRssFeed();
		DateTime dateTime = new DateTime();
		/*if(article.getPublishedAt() != null) {
			String publishedAt = article.getPublishedAt().trim();
			dateTime = DateTime.parse(publishedAt);	
		}*/
		result.setFeedType(feedType);
		result.setId(String.valueOf(dateTime.getMillis()));
		result.setUploadTime(dateTime.getMillis());
		result.setOgTitle(article.getTitle());
		result.setOgDescription(article.getDescription());
		result.setOgImage(article.getUrlToImage());
		result.setOgUrl(article.getUrl());
		result.setHrefSource(article.getUrl());
		result.setCreatedBy(article.getAuthor());
		//result.setFeedType(RssFeedType.NEWS.name());
		return result;
	}
}