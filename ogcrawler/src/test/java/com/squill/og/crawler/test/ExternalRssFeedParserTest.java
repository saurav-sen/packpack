package com.squill.og.crawler.test;

import java.util.Locale;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

public class ExternalRssFeedParserTest {

	public static void main(String[] args) {
		/*ExtFeed extFeed = new ExternalRssFeedParser(
				"https://www.firstpost.com/rss/world.xml").parse();
		ExtFeedEntry extFeedEntry = extFeed.getEntries().get(1);
		String pubDate = extFeedEntry.getPubDate();
		System.out.println(pubDate);*/
		
		DateTimeParser[] parsers = {
				DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z")
						.getParser(),
				DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss ZZZ")
						.getParser(),
				DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss a")
						.getParser() };

		// create a formatter using the parsers array
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				.append(null, parsers) // use parsers array
				.toFormatter().withLocale(Locale.ENGLISH).withOffsetParsed();
		
		System.out.println(formatter.parseDateTime("Wed, 02 Oct 2002 13:00:00 GMT"));
		System.out.println(formatter.parseDateTime("Wed, 02 Oct 2002 15:00:00 +0200"));
		String pubDate = "Thursday,April 11,2019 10:23 am";
		System.out.println(formatter.parseDateTime(pubDate));
			
		//System.out.println(extFeed);
	}

}
