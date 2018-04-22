package com.squill.og.crawler.newsapi.reader;

import java.util.List;

import org.joda.time.DateTime;

import com.squill.og.crawler.model.web.JRssFeed;
import com.squill.og.crawler.model.web.JRssFeeds;

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
		DateTime dateTime = new DateTime();
		result.setId(String.valueOf(dateTime.getMillis()));
		//result.setUploadTime(dateTime.getMillis());
		result.setOgTitle(article.getTitle());
		result.setOgDescription(article.getDescription());
		result.setOgImage(article.getUrlToImage());
		result.setOgUrl(article.getUrl());
		result.setHrefSource(article.getUrl());
		//result.setCreatedBy(article.getAuthor());
		return result;
	}
}