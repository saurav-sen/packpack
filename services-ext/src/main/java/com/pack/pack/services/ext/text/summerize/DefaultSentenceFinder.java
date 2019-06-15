package com.pack.pack.services.ext.text.summerize;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.util.LanguageUtil;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
public class DefaultSentenceFinder implements ISentenceFinder {
	
	private SentenceDetectorME detector;

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultSentenceFinder.class);
	
	public static ISentenceFinder INSTANCE = new DefaultSentenceFinder(SystemPropertyUtil.getOpenNlpConfDir());
	
	private DefaultSentenceFinder(String confDir) {
		InputStream sentenceModelIn = null;
		try {
			/*
			 * sentenceModelIn = SentenceDetector.class
			 * .getResourceAsStream("/en-sent.bin");
			 */
			String confFile = confDir;
			if(!confFile.endsWith(File.separator) && !confFile.endsWith("/")) {
				confFile = confFile + File.separator;
			}
			confFile = confFile + "en-sent.bin";
			sentenceModelIn = new FileInputStream(new File(confFile));
			SentenceModel sentenceModel = new SentenceModel(sentenceModelIn);
			detector = new SentenceDetectorME(sentenceModel);
		} catch (Exception e) {
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

	private String[] findSentences0(String text) {
		text = text.replace("Mr.", "Mr").replace("Ms.", "Ms")
				.replace("Dr.", "Dr").replace("Jan.", "Jan")
				.replace("Feb.", "Feb").replace("Mar.", "Mar")
				.replace("Apr.", "Apr").replace("Jun.", "Jun")
				.replace("Jul.", "Jul").replace("Aug.", "Aug")
				.replace("Sep.", "Sep").replace("Spet.", "Sept")
				.replace("Oct.", "Oct").replace("Nov.", "Nov")
				.replace("Dec.", "Dec").replace("St.", "St")
				.replace("Prof.", "Prof").replace("Mrs.", "Mrs")
				.replace("Gen.", "Gen").replace("Corp.", "Corp")
				.replace("Mrs.", "Mrs").replace("Sr.", "Sr")
				.replace("Jr.", "Jr").replace("cm.", "cm")
				.replace("Ltd.", "Ltd").replace("Col.", "Col")
				.replace("vs.", "vs").replace("Capt.", "Capt")
				.replace("Univ.", "University").replace("Sgt.", "Sgt")
				.replace("ft.", "ft").replace("in.", "in")
				.replace("Ave.", "Ave").replace("Univ.", "University")
				.replace("Lt.", "Lt").replace("etc.", "etc")
				.replace("mm.", "mm").replace("\n\n", "").replace("\n", "")
				.replace("\r", "");
		// solved! now fix alphabet letters like A. B. etc...use a regex
		text = text.replaceAll("([A-Z])\\.", "$1");

		// split using ., !, ?, and omit decimal numbers
		String pattern = "(?<!\\d)\\.(?!\\d)|(?<=\\d)\\.(?!\\d)|(?<!\\d)\\.(?=\\d)";
		Pattern pt = Pattern.compile(pattern);

		String[] sentences = pt.split(text);
		return sentences;
	}
	
	@Override
	public String[] findSentences(String text) {
		if(detector == null) {
			LOG.debug("SentenceDetector NOT initialized properly");
			return findSentences0(text);
		}
		return detector.sentDetect(text);
	}

	@Override
	public String[] findWords(String sentence) throws Exception {
		List<String> list = LanguageUtil.getWords(sentence);
		return list.toArray(new String[list.size()]);
	}

}
