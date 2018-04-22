package com.squill.og.crawler.named.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Set;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;

import com.squill.og.crawler.app.SystemPropertyKeys;

public class KeywordFinder {

	private POSTagger posTagger;

	private class POSTagBasedTokenFilter {
		boolean isInclude(String token, String tag) {
			//return tag.startsWith("NN");// || tag.startsWith("VB") ||
										// tag.startsWith("JJ");
			return tag.equals("NNP");
		}
	}

	public KeywordFinder init() throws Exception {
		InputStream posModelIn = null;
		try {
			posModelIn = new FileInputStream(new File(
					System.getProperty(SystemPropertyKeys.WEB_CRAWLERS_CONFIG_DIR)
							+ File.separator + "en-pos-maxent.bin"));
			POSModel posModel = new POSModel(posModelIn);
			posTagger = new POSTaggerME(posModel);
		} finally {
			if (posModelIn != null) {
				posModelIn.close();
			}
		}
		return this;
	}

	public void findKeywords(String[] tokens, Set<String> keywords)
			throws Exception {
		String[] tags = posTagger.tag(tokens);
		POSTagBasedTokenFilter filter = new POSTagBasedTokenFilter();
		for (int i = 0; i < tags.length; i++) {
			String tag = tags[i];
			System.out.print(tokens[i] + "\\" + tag + " ");
			if (filter.isInclude(tokens[i], tag)) {
				keywords.add(tokens[i]);
			}
		}
	}
}
