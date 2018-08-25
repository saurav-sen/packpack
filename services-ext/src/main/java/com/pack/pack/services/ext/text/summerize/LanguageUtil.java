package com.pack.pack.services.ext.text.summerize;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

final class LanguageUtil {

	private LanguageUtil() {
	}

	static List<String> getWords(String sentence) {
		List<String> words = new ArrayList<String>();
		BreakIterator breakIterator = BreakIterator.getWordInstance();
		breakIterator.setText(sentence);
		int lastIndex = breakIterator.first();
		while (BreakIterator.DONE != lastIndex) {
			int firstIndex = lastIndex;
			lastIndex = breakIterator.next();
			if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(sentence.charAt(firstIndex))) {
				words.add(sentence.substring(firstIndex, lastIndex));
			}
		}

		return words;
	}
	
	static String cleanHtmlInvisibleCharacters(String text) {
		return text.replaceAll("\\s+", " ").replaceAll("\\xA0", " ");
	}
}