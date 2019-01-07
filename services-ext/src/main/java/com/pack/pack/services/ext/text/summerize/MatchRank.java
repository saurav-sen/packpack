package com.pack.pack.services.ext.text.summerize;

enum MatchRank {

	NO_MATCH, LOW, MEDIUM, HIGH;

	static MatchRank checkMatch(String[][] wordMatrix, String elementText) {
		elementText = elementText.trim();
		int len = wordMatrix.length; // This is a SQUARE matrix
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
						return MatchRank.HIGH;
					} else if (percentageMatch > 0.4f && percentageMatch < 0.6f) {
						return MatchRank.MEDIUM;
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
			return MatchRank.HIGH;
		} else if (percentageMatch > 0.4f && percentageMatch < 0.6f) {
			return MatchRank.MEDIUM;
		}
		return MatchRank.LOW;
	}
}