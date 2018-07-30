package com.squill.og.crawler.opennlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;

@Component("openNlpSentenceDetector")
@Scope("singleton")
public class OpenNlpSentenceDetector implements ISentenceDetector {

	private SentenceDetectorME detector;
	
	@PostConstruct
	public OpenNlpSentenceDetector init() throws Exception {
		OpenNlpSentenceDetector sentenceDetector = new OpenNlpSentenceDetector();
		InputStream sentenceModelIn = null;
		try {
			/*sentenceModelIn = SentenceDetector.class
					.getResourceAsStream("/en-sent.bin");*/
			sentenceModelIn = new FileInputStream(new File("../conf/en-sent.bin"));
			SentenceModel sentenceModel = new SentenceModel(sentenceModelIn);
			sentenceDetector.detector = new SentenceDetectorME(sentenceModel);
		} finally {
			try {
				if (sentenceModelIn != null) {
					sentenceModelIn.close();
				}
			} catch (Exception e) {
			}
		}
		return sentenceDetector;
	}

	@Override
	public String[] detectSentences(String text) throws Exception {
		return detector.sentDetect(text);
	}

	@Override
	public String[] tokenize(String sentence) throws Exception {
		return SimpleTokenizer.INSTANCE.tokenize(sentence);
	}
}