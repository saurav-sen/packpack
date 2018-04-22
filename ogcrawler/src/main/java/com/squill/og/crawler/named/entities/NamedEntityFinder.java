package com.squill.og.crawler.named.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Set;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Span;

import org.jsoup.Jsoup;

import com.squill.og.crawler.app.SystemPropertyKeys;

public abstract class NamedEntityFinder {

	private String modelFilePath;

	private NameFinderME nameFinder;
	
	private POSTagger posTagger;
	
	protected NamedEntityFinder(String modelFilePath) {
		this.modelFilePath = modelFilePath;
	}

	public void init() throws Exception {
		InputStream inputStream = null;
		InputStream posModelIn = null;
		try {
			inputStream = new FileInputStream(new File(System.getProperty(SystemPropertyKeys.WEB_CRAWLERS_CONFIG_DIR) + File.separator + modelFilePath));
			TokenNameFinderModel model = new TokenNameFinderModel(inputStream);
			nameFinder = new NameFinderME(model);
			
			posModelIn = new FileInputStream(new File(
					System.getProperty(SystemPropertyKeys.WEB_CRAWLERS_CONFIG_DIR)
							+ File.separator + "en-pos-maxent.bin"));
			POSModel posModel = new POSModel(posModelIn);
			posTagger = new POSTaggerME(posModel);
		} finally {
			try {
				if(inputStream != null) {
					inputStream.close();
				}
				if(posModelIn != null) {
					posModelIn.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public void findNames(String[] tokens, Set<String> names) throws Exception {
		//String[] tokens = sentence.split(" ");
		if(nameFinder == null)
			return;
		Span nameSpans[] = nameFinder.find(tokens);
		if (nameSpans != null && nameSpans.length > 0) {
			for (Span nameSpan : nameSpans) {
				int start = nameSpan.getStart();
				int end = nameSpan.getEnd() + 1;
				StringBuilder str = new StringBuilder();
				for(int i=start; i < end && i < tokens.length;) {
					str.append(tokens[i]);
					i++;
					if(i < end) {
						str.append(" ");
					}
				}
				//names.add(nameSpan.toString());
				names.add(doTextCleanup(str.toString()));
			}
		}
	}
	
	private String doTextCleanup(String namedEntity) {
		String SENTENCE = namedEntity.replaceAll("'s", "").replaceAll("-", " ")
				.replaceAll("[^a-zA-Z0-9 ]", "");
		SENTENCE = "<html>" + SENTENCE + "</html>";
		SENTENCE = Jsoup.parse(SENTENCE).text();
		return SENTENCE;
	}

	protected POSTagger getPosTagger() {
		return posTagger;
	}
}
