package com.squill.og.crawler.named.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NamedEntitiesExtractor {

	private SentenceDetector sentenceDetector;
	private LocationFinder locationFinder;
	private PersonFinder personFinder;
	private OrganizationFinder organizationFinder;

	private KeywordFinder keywordFinder;

	private static NamedEntitiesExtractor __INSTANCE;

	private static Lock lock = new ReentrantLock();

	private NamedEntitiesExtractor() {
		sentenceDetector = new SentenceDetector();
		locationFinder = new LocationFinder();
		personFinder = new PersonFinder();
		organizationFinder = new OrganizationFinder();
		keywordFinder = new KeywordFinder();
	}

	public static NamedEntitiesExtractor init() throws Exception {
		while (!lock.tryLock(1, TimeUnit.SECONDS)) {
		}
		lock.lock();
		try {
			if (__INSTANCE == null) {
				__INSTANCE = new NamedEntitiesExtractor();
				__INSTANCE.sentenceDetector.init();
				__INSTANCE.locationFinder.init();
				__INSTANCE.personFinder.init();
				__INSTANCE.organizationFinder.init();
				__INSTANCE.keywordFinder.init();
			}
		} finally {
			lock.unlock();
		}
		return __INSTANCE;
	}

	public NamedEntities extractNamedEntitiesFromUrl(String url)
			throws Exception {
		AylienEntitiesResponse aylienEntitiesResponse = new AylienEntityFinder()
				.findNames(url);
		String text = aylienEntitiesResponse.getText();
		AylienEntities aylienEntities = aylienEntitiesResponse.getEntities();
		NamedEntities namedEntities = extractNamedEntitiesFromText(text);
		namedEntities.setPerson(merge(aylienEntities.getPerson(),
				namedEntities.getPerson()));
		namedEntities.setLocation(merge(aylienEntities.getLocation(),
				namedEntities.getLocation()));
		namedEntities.setOrganization(merge(aylienEntities.getOrganization(),
				namedEntities.getOrganization()));
		namedEntities.setKeyword(merge(aylienEntities.getKeyword(),
				namedEntities.getKeyword()));
		return namedEntities;
	}

	private List<String> merge(List<String> set1, List<String> set2) {
		Set<String> set3 = new HashSet<String>();
		set3.addAll(set1);
		set3.addAll(set2);
		return new ArrayList<String>(set3);
	}

	public NamedEntities extractNamedEntitiesFromText(String text)
			throws Exception {
		NamedEntities result = new NamedEntities();
		String[] sentences = sentenceDetector.detectSentences(text);
		if (sentences == null || sentences.length == 0)
			return result;
		Set<String> locations = new HashSet<String>();
		Set<String> persons = new HashSet<String>();
		Set<String> organizations = new HashSet<String>();
		Set<String> keywords = new HashSet<String>();
		for (String sentence : sentences) {
			String[] tokens = sentenceDetector.tokenize(sentence);
			if (tokens != null && tokens.length > 0) {
				locationFinder.findNames(tokens, locations);
				personFinder.findNames(tokens, persons);
				organizationFinder.findNames(tokens, organizations);
				keywordFinder.findKeywords(tokens, keywords);
			}
		}
		result.setLocation(new ArrayList<String>(locations));
		result.setPerson(new ArrayList<String>(persons));
		result.setOrganization(new ArrayList<String>(organizations));
		result.setKeyword(new ArrayList<String>(keywords));
		return result;
	}
}
