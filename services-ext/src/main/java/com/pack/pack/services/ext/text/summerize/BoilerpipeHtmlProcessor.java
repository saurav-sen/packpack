package com.pack.pack.services.ext.text.summerize;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import com.kohlschutter.boilerpipe.BoilerpipeExtractor;
import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.Image;
import com.kohlschutter.boilerpipe.extractors.CommonExtractors;
import com.kohlschutter.boilerpipe.sax.HTMLHighlighter;
import com.kohlschutter.boilerpipe.sax.ImageExtractor;
import com.pack.pack.util.LanguageUtil;

/**
 * 
 * @author Saurav
 *
 */
public class BoilerpipeHtmlProcessor {

	private BoilerpipeHtmlProcessor() {
	}

	public static BoilerpipeHtmlProcessor newInstance() {
		return new BoilerpipeHtmlProcessor();
	}

	public WebDocument process(String htmlWithJsCss, String html, String title, String description,
			String imageUrl, String ogUrl, String inputUrl, String srcUrl)
			throws IOException, BoilerpipeProcessingException, SAXException {
		BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
		HTMLHighlighter hh = HTMLHighlighter.newHighlightingInstance();
		String highlightedHtml = hh.process(html, extractor);
		Document doc = Jsoup.parse(highlightedHtml);
		if (imageUrl == null || imageUrl.trim().isEmpty()) {
			ImageExtractor imageExtractor = ImageExtractor.INSTANCE;
			List<Image> imgUrls = imageExtractor.process(html, extractor);
			if (imgUrls != null && !imgUrls.isEmpty()) {
				// automatically sorts them by decreasing area, i.e. most
				// probable true positives come first
				Collections.sort(imgUrls);
				Iterator<Image> itr = imgUrls.iterator();
				while ((imageUrl == null || imageUrl.trim().isEmpty())
						&& itr.hasNext()) {
					Image image = itr.next();
					imageUrl = image.getSrc();
				}
			}
		}
		if (ogUrl == null || ogUrl.trim().isEmpty()) {
			ogUrl = srcUrl;
		}
		if (inputUrl == null || inputUrl.trim().isEmpty()) {
			inputUrl = srcUrl;
		}
		Elements elements = doc.body().getElementsByClass(
				HTMLHighlighter.BOILERPIPE_MARK_CSS_CLASS_NAME);
		if(elements == null || elements.isEmpty()) {
			return new WebDocument(title, description, imageUrl, ogUrl, inputUrl,
					doc, htmlWithJsCss.toString()).setSuccess(true);
		}
		Iterator<Element> itr = elements.iterator();
		int size = 0;
		int sumLength = 0;
		while (itr.hasNext()) {
			Element element = itr.next();
			int length = element.text().length();
			sumLength = sumLength + length;
			size++;
		}
		int meanLength = sumLength / size;
		double SD = 0.0;
		itr = elements.iterator();
		while (itr.hasNext()) {
			Element element = itr.next();
			int length = element.text().length();
			int l = Math.abs(meanLength - length);
			SD = SD + (l * l);
		}
		SD = SD / size;
		SD = Math.sqrt(SD);
		itr = elements.iterator();
		SD = SD * 0.9f;
		while (itr.hasNext()) {
			Element element = itr.next();
			int length = element.text().length();
			if (length < SD) {
				element.removeClass(HTMLHighlighter.BOILERPIPE_MARK_CSS_CLASS_NAME);
				element.remove();
			}
		}

		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<div>");
		
		if(title != null && !title.trim().isEmpty()) {
			Elements elements2 = doc.body().getElementsByTag("h1");
			
			if(elements2 != null) {
				itr = elements2.iterator();
				while(itr.hasNext()) {
					Element element = itr.next();
					String text = element.text();
					
					List<String> words = LanguageUtil.getWords(text);
					int len = words.size();
					String[][] wordMatrix = new String[len][len];
					for (int i = 0; i < len; i++) {
						StringBuilder desc = new StringBuilder();
						for (int j = 0; j < len; j++) {
							if (j < i) {
								wordMatrix[i][j] = "";
							} else {
								desc.append(words.get(j));
								wordMatrix[i][j] = desc.toString().replaceAll("[^a-zA-Z0-9\\s]", "");
								desc.append(" ");
							}
						}
					}
					
					MatchRank matchRank = MatchRank.checkMatch(wordMatrix, title);
					if(matchRank == MatchRank.HIGH) {
						title = text;
						if(element.hasClass(HTMLHighlighter.BOILERPIPE_MARK_CSS_CLASS_NAME)) {
							element.addClass(HTMLHighlighter.BOILERPIPE_MARK_CSS_NON_ARTICLE_CLASS_NAME);
						} else {
							Elements allElements = element.getAllElements();
							if(allElements != null && !allElements.isEmpty()) {
								Iterator<Element> itr2 = allElements.iterator();
								while(itr2.hasNext()) {
									Element el = itr2.next();
									if(el.hasClass(HTMLHighlighter.BOILERPIPE_MARK_CSS_CLASS_NAME)) {
										el.addClass(HTMLHighlighter.BOILERPIPE_MARK_CSS_NON_ARTICLE_CLASS_NAME);
									}
								}
							}
							element.addClass(HTMLHighlighter.BOILERPIPE_MARK_CSS_CLASS_NAME);
						}
					}
				}
			}
		}		
		
		/*Elements elements2 = doc.body().getElementsByTag("a");
		itr = elements2.iterator();
		while(itr.hasNext()) {
			Element element = itr.next();
			String href = element.attr("href");
			if(href != null && !href.trim().isEmpty() && !AdBlocker.INSTANCE.isAdvertisement(href)) {
				element.addClass(HTMLHighlighter.BOILERPIPE_MARK_CSS_CLASS_NAME);
			}
		}*/
		elements = doc.body().getElementsByClass(
				HTMLHighlighter.BOILERPIPE_MARK_CSS_CLASS_NAME);
		itr = elements.iterator();
		while (itr.hasNext()) {
			Element element = itr.next();
			if(element.hasClass(HTMLHighlighter.BOILERPIPE_MARK_CSS_NON_ARTICLE_CLASS_NAME)) {
				continue;
			}
			htmlBuilder.append("<p>");
			htmlBuilder.append(element.outerHtml());
			htmlBuilder.append("</p>");
			//htmlBuilder.append("<br/>");
		}
		htmlBuilder.append("</div>");
		return new WebDocument(title, description, imageUrl, ogUrl, inputUrl,
				doc, htmlBuilder.toString()).setSuccess(true);
	}
}