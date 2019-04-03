package com.squill.og.crawler.test;

import com.squill.og.crawler.external.feed.ExtFeed;
import com.squill.og.crawler.external.feed.ExternalRssFeedParser;

public class ExternalRssFeedParserTest {

	public static void main(String[] args) {
		ExtFeed extFeed = new ExternalRssFeedParser(
				"https://www.dnaindia.com/feeds/latest.xml").parse();
		System.out.println(extFeed);
	}

}
