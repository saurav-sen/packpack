package com.squill.og.crawler.opennlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("openNlpSentenceDetector")
@Scope("singleton")
public class OpenNlpSentenceDetector implements ISentenceDetector {

	private SentenceDetectorME detector;
	
	private static final Logger LOG = LoggerFactory.getLogger(OpenNlpSentenceDetector.class);
	
	public OpenNlpSentenceDetector() {
		InputStream sentenceModelIn = null;
		try {
			/*sentenceModelIn = SentenceDetector.class
					.getResourceAsStream("/en-sent.bin");*/
			sentenceModelIn = new FileInputStream(new File("../conf/en-sent.bin"));
			SentenceModel sentenceModel = new SentenceModel(sentenceModelIn);
			detector = new SentenceDetectorME(sentenceModel);
		} catch(Exception e) {
			LOG.debug(e.getMessage(), e);
		} finally {
			try {
				if (sentenceModelIn != null) {
					sentenceModelIn.close();
				}
			} catch (Exception e) {
			}
		}
	}
	
	@Override
	public String[] detectSentences(String text) throws Exception {
		if(detector == null) {
			LOG.debug("SentenceDetector NOT initialized properly");
			return new String[] {text};
		}
		return detector.sentDetect(text);
	}

	@Override
	public String[] tokenize(String sentence) throws Exception {
		if(detector == null) {
			LOG.debug("SentenceDetector NOT initialized properly");
			return new String[] {sentence};
		}
		return SimpleTokenizer.INSTANCE.tokenize(sentence);
	}
}