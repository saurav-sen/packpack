package com.squill.og.crawler.named.entities;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;

public class PersonFinder extends NamedEntityFinder {

	public PersonFinder() {
		//super("en-ner-person.bin");
		super("nl-ner-person.bin");
	}
	
//	@Override
//	public void findNames(String[] tokens, Set<String> names) throws Exception {
//		super.findNames(tokens, names);
//		Set<String> result = filter(names);
//		names.clear();
//		names.addAll(result);
//	}
	
	public Set<String> filter(Set<String> names) {
		return extractRelevantTokens(names.toArray(new String[names.size()]), new POSTagBasedTokenFilter());
	}
	
	private Set<String> extractRelevantTokens(String[] tokens, POSTagBasedTokenFilter filter) {
		Set<String> result = new HashSet<String>();
		String[] tags = getPosTagger().tag(tokens);
		
		//System.out.println(sentence);
		for(int i=0; i<tags.length; i++) {
			String tag = tags[i];
			System.out.print(tokens[i] + "\\" + tag + " ");
			if(filter.isInclude(tokens[i], tag)) {
				result.add(tokens[i]);
			}
		}
		
		return result;
	}
	
	private String sentencify(String[] tokens) {
		StringBuilder str = new StringBuilder();
		int len = tokens.length;
		for(int i=0; i<len;) {
			str.append(tokens[i]);
			i++;
			if(i<len) {
				str.append(" ");
			}
		}
		return doTextCleanup(str.toString());
	}
	
	private String doTextCleanup(String sentence) {
		String SENTENCE = sentence.replaceAll("'s", "").replaceAll("-", " ").replaceAll("[^a-zA-Z0-9 ]", "");
		SENTENCE = "<html>" + SENTENCE + "</html>";
		SENTENCE = Jsoup.parse(SENTENCE).text();
		System.out.println(SENTENCE);
		return SENTENCE;
	}
	
	private class POSTagBasedTokenFilter {
		public boolean isInclude(String token, String tag) {
			return tag.startsWith("NN");
		}
	}
}