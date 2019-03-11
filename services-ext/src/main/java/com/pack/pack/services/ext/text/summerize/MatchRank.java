package com.pack.pack.services.ext.text.summerize;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

enum MatchRank {

	NO_MATCH(0f), LOW(0.01f), MEDIUM(0.41f), HIGH(0.61f);
	
	private float percentMatch;
	
	private MatchRank(float percentMatch) {
		this.percentMatch = percentMatch;
	}
	
	static MatchRank checkIntersection(String[] array1, String[] array2) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(String a : array1) {
			map.put(a.toLowerCase(), 0);
		}
		for(String a : array2) {
			if(map.get(a.toLowerCase()) != null) {
				int i = map.get(a.toLowerCase());
				i++;
				map.put(a.toLowerCase(), i);
			}
		}
		int count = 0;
		Iterator<Integer> itr = map.values().iterator();
		while(itr.hasNext()) {
			count = count + itr.next();
		}
		int len = array1.length;
		float percentageMatch = (float) count / (float) len;
		if (percentageMatch > 0.6f) {
			return MatchRank.HIGH.setPercentMatch(percentageMatch);
		} else if (percentageMatch > 0.4f && percentageMatch < 0.6f) {
			return MatchRank.MEDIUM.setPercentMatch(percentageMatch);
		} else if(count == 0) {
			return MatchRank.MEDIUM.setPercentMatch(0.0f);
		} else {
			return MatchRank.LOW.setPercentMatch(percentageMatch);
		}
	}

	static MatchRank checkMatch(String[][] wordMatrix, String elementText) {
		elementText = elementText.trim();
		int len = wordMatrix.length; // This is a SQUARE matrix
		if(len == 0)
			return MatchRank.NO_MATCH;
		String entireSentence = wordMatrix[0][len - 1];
		if (elementText.isEmpty()
				|| elementText.length() < entireSentence.length())
			return MatchRank.NO_MATCH;
		StringBuilder partialMatches = new StringBuilder();
		for (int i = 0; i < len; i++) {
			for (int j = len - 1; j >= i; j--) {
				String text1 = wordMatrix[i][j].trim();
				String str = elementText.replaceAll("[^a-zA-Z0-9\\s]", "");
				if (str.contains(text1 + " ") || str.contains(" " + text1)
				/* || text1.contains(elementText) */) {
					float percentageMatch = (float) text1.length()
							/ (float) entireSentence.length();
					if (percentageMatch > 0.6f) {
						return MatchRank.HIGH.setPercentMatch(percentageMatch);
					} else if (percentageMatch > 0.4f && percentageMatch < 0.6f) {
						return MatchRank.MEDIUM.setPercentMatch(percentageMatch);
					} else if (!partialMatches.toString().contains(text1)) {
						String[] words = text1.split(" ");
						for (String word : words) {
							if (!partialMatches.toString().contains(word)) {
								partialMatches.append(word);
								partialMatches.append(" ");
							}
						}
					}
				}
			}
		}
		if (partialMatches.toString().isEmpty())
			return MatchRank.NO_MATCH;
		float percentageMatch = (float) partialMatches.length()
				/ (float) entireSentence.length();
		if (percentageMatch > 0.6f) {
			return MatchRank.HIGH.setPercentMatch(percentageMatch);
		} else if (percentageMatch > 0.4f && percentageMatch < 0.6f) {
			return MatchRank.MEDIUM.setPercentMatch(percentageMatch);
		}
		return MatchRank.LOW.setPercentMatch(percentageMatch);
	}

	public float getPercentMatch() {
		return percentMatch;
	}

	public MatchRank setPercentMatch(float percentMatch) {
		this.percentMatch = percentMatch;
		return this;
	}
}