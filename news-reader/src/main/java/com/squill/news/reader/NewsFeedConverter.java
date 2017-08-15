package com.squill.news.reader;

import java.util.List;

import org.joda.time.DateTime;

import com.pack.pack.model.RssFeedType;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.JRssFeedType;
import com.pack.pack.model.web.JRssFeeds;

/**
 * 
 * @author Saurav
 *
 */
public class NewsFeedConverter {
	
	public static JRssFeeds convert(NewsFeeds newsFeeds) {
		JRssFeeds result = new JRssFeeds();
		List<NewsFeed> articles = newsFeeds.getArticles();
		for(NewsFeed article : articles) {
			JRssFeed r = convert(article);
			result.getFeeds().add(r);
		}
		return result;
	}
	
	public static JRssFeed convert(NewsFeed article) {
		JRssFeed result = new JRssFeed();
		String publishedAt = article.getPublishedAt().trim();
		DateTime dateTime = DateTime.parse(publishedAt);			
		result.setFeedType(JRssFeedType.NEWS.name());
		result.setId(String.valueOf(dateTime.getMillis()));
		result.setUploadTime(dateTime.getMillis());
		result.setOgTitle(article.getTitle());
		result.setOgDescription(article.getDescription());
		result.setOgImage(article.getUrlToImage());
		result.setOgUrl(article.getUrl());
		result.setHrefSource(article.getUrl());
		result.setCreatedBy(article.getAuthor());
		result.setFeedType(RssFeedType.NEWS.name());
		return result;
	}
}