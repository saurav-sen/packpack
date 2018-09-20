package com.pack.pack.services.ext.doc.clasification.test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.util.SystemPropertyUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;

import static com.pack.pack.services.ext.text.summerize.STOP_WORDS.STOP_WORDS;

public class ArchiveReader {
	
	private List<JRssFeed> feeds = new ArrayList<JRssFeed>();

	public List<JRssFeed> readAllTitles() throws IOException {
		Files.newDirectoryStream(Paths.get(SystemPropertyUtil.getDefaultArchiveFolder()), path -> path.toString().endsWith(".json")).forEach(this::readFeedsFromFile);
		return feeds;
	}
	
	private void readFeedsFromFile(Path path) {
		try {
			String json = new String(Files.readAllBytes(path), Charset.forName("UTF-8"));
			JRssFeeds feeds = JSONUtil.deserialize(json, JRssFeeds.class, true);
			feeds.getFeeds().forEach(this::readFeed);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (PackPackException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private void readFeed(JRssFeed feed) {
		feed.setOgTitle(filterStopwords(feed.getOgTitle()));
		feeds.add(feed);
	}
	
	private String filterStopwords(String text) {
		text = text + " ";
		String lowerCase = text.toLowerCase();
		String[] split = lowerCase.split(" ");
		Object OBJ = new Object();
		Map<String, Object> map = new HashMap<String, Object>();
		for(String s : split) {
			map.put(s, OBJ);
		}
		for (int i = 0; i < STOP_WORDS.length; i++) {
			if(map.containsKey(STOP_WORDS[i])) {
				text = text.replaceAll("(?i)" + Pattern.quote(STOP_WORDS[i]) + "\\s+", "");
			}
		}
		text = text.replaceAll("[^a-zA-Z0-9\\s]", "");
		return text;
	}
	
	public static void main1(String[] args) {
		System.out.println(Pattern.quote("from"));
	}
	
	public static void main(String[] args) throws Exception {
		List<JRssFeed> feeds = new ArchiveReader().readAllTitles();
		int count = 0;
		for(JRssFeed feed : feeds) {
			StringBuilder str = new StringBuilder();
			str.append(feed.getOgTitle());
			//str.append(",");
			//List<JConcept> concepts = feed.getConcepts();
			/*if(!concepts.isEmpty()) {
				str.append(concepts.get(0).getDbpediaRef());
			}*/
			/*for(JConcept concept : concepts) {
				str.append(concept.getDbpediaRef());
				str.append("|");
			}*/
			System.out.println(str.toString());
			count++;
		}
		System.out.println(count);
	}
}
