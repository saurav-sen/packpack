package com.squill.og.crawler.named.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;

import com.squill.og.crawler.app.SystemPropertyKeys;

public class SentenceDetector {

	private SentenceDetectorME detector;

	// private TokenizerME tokenizer;

	// private POSTagger posTagger;

	public void init() throws Exception {
		InputStream sentenceModelIn = null;
		// InputStream tokenModelIn = null;
		// InputStream posModelIn = null;
		try {
			sentenceModelIn = new FileInputStream(new File(
					System.getProperty(SystemPropertyKeys.WEB_CRAWLERS_CONFIG_DIR)
							+ File.separator + "en-sent.bin"));
			/*
			 * sentenceModelIn = SentenceDetector.class
			 * .getResourceAsStream("/META_INF/en-sent.bin");
			 */
			SentenceModel sentenceModel = new SentenceModel(sentenceModelIn);
			detector = new SentenceDetectorME(sentenceModel);

			// tokenModelIn = new FileInputStream(new
			// File(System.getProperty(Startup.WEB_CRAWLERS_CONFIG_DIR) +
			// File.separator + "en-token.bin"));
			// TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
			// tokenizer = new TokenizerME(tokenModel);

			// posModelIn = new FileInputStream(new File(
			// System.getProperty(Startup.WEB_CRAWLERS_CONFIG_DIR)
			// + File.separator + "en-pos-maxent.bin"));
			// POSModel posModel = new POSModel(posModelIn);
			// posTagger = new POSTaggerME(posModel);
		} finally {
			try {
				if (sentenceModelIn != null) {
					sentenceModelIn.close();
				}

				// if (tokenModelIn != null) {
				// tokenModelIn.close();
				// }

				// if (posModelIn != null) {
				// posModelIn.close();
				// }
			} catch (Exception e) {
			}
		}
	}

	// private String doTextCleanup(String sentence) {
	// String SENTENCE = sentence.replaceAll("'s", "").replaceAll("-", " ")
	// .replaceAll("[^a-zA-Z0-9 ]", "");
	// SENTENCE = "<html>" + SENTENCE + "</html>";
	// SENTENCE = Jsoup.parse(SENTENCE).text();
	// System.out.println(SENTENCE);
	// return SENTENCE;
	// }

	public String[] detectSentences(String text) throws Exception {
		return detector.sentDetect(text);
	}

	// private Set<String> extractRelevantTokens(String[] tokens,
	// POSTagBasedTokenFilter filter) {
	// Set<String> tokensSet = new HashSet<String>();
	// String[] tags = posTagger.tag(tokens);
	//
	// // System.out.println(sentence);
	// for (int i = 0; i < tags.length; i++) {
	// String tag = tags[i];
	// System.out.println(tokens[i] + "\\" + tag + " ");
	// if (filter.isInclude(tokens[i], tag)) {
	// tokensSet.add(tokens[i]);
	// }
	// }
	//
	// return tokensSet;
	// }

	public String[] tokenize(String sentence) throws Exception {
		// String[] tokens = tokenizer.tokenize(sentence);
		String[] tokens = SimpleTokenizer.INSTANCE.tokenize(sentence);
		// Set<String> tokensSet = extractRelevantTokens(tokens,
		// new POSTagBasedTokenFilter());
		return tokens;// tokensSet.toArray(new String[tokensSet.size()]);
	}

	// private class POSTagBasedTokenFilter {
	// public boolean isInclude(String token, String tag) {
	// return tag.startsWith("NN");
	// }
	// }
}
