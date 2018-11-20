package com.pack.pack.services.ext.text.summerize;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.services.ext.HttpRequestExecutor;
import com.pack.pack.util.LanguageUtil;
import com.squill.feed.web.model.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Lazy
@Scope("singleton")
public class WebDocumentParser {

	// private static final boolean returnIfTagNameNotFound = true;

	private static final String H1_TAG_NAME = "h1";

	private static final Logger $LOG = LoggerFactory
			.getLogger(WebDocumentParser.class);
	
	public JRssFeed parse(String url) {
		JRssFeed json = new JRssFeed();
		try {
			HttpGet GET = new HttpGet(url);
			HttpResponse response = new HttpRequestExecutor().GET(GET);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				return json;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return json;
			}
			String content = EntityUtils.toString(entity);
			json = parseHtmlPayload(content);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
			$LOG.error(e.getMessage(), e);
		}
		return json;
	}

	public JRssFeed parseHtmlPayload(String content) {
		JRssFeed json = new JRssFeed();
		try {
			$LOG.debug("Parsing HTML");
			WebDocument webDocument = parseHtml(content);
			/*InputStream input = new ByteArrayInputStream(webDocument
					.getFilteredHtml().getBytes());
			ContentHandler textHandler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			AutoDetectParser parser = new AutoDetectParser();
			ParseContext context = new ParseContext();
			BoilerpipeContentHandler handler2 = new BoilerpipeContentHandler(
					textHandler, LargestContentExtractor.getInstance());
			$LOG.debug("Parsing HTML using Boilerpipe Content Handler/AutoDetectParser");
			parser.parse(input, handler2, metadata, context);

			String article = textHandler.toString();*/
			String article = Jsoup.parse(new String(webDocument.getFilteredHtml()
					.getBytes())).body().text();
			if (article == null || article.trim().isEmpty()) {
				article = webDocument.getDocument().body().text();
			}

			String title = webDocument.getTitle();

			if (title != null) {
				title = LanguageUtil.cleanHtmlInvisibleCharacters(title);
				article.replaceAll(title.trim(), "");
			}

			article = LanguageUtil.cleanHtmlInvisibleCharacters(article);
			article = article.replaceAll("\\s+", " ").replaceAll("\\t+", " ").replaceAll("\\n+", "\n").replaceAll("\\r+", "\r");

			String ogImage = webDocument.getImageUrl();
			json.setOgImage(ogImage);
			/*String keywordsText = metadata.get("keywords");
			if (keywordsText != null) {
				String[] keywords = keywordsText.split(",");
				for (String keyword : keywords) {
					json.getKeywords().add(keyword.trim());
				}
			}*/

			String ogUrl = webDocument.getSourceUrl();
			json.setOgUrl(ogUrl);
			
			String inputUrl = webDocument.getInputUrl();
			if(inputUrl == null || inputUrl.trim().isEmpty()) {
				inputUrl = ogUrl;
			}
			json.setHrefSource(inputUrl);

			String ogDescription = webDocument.getDescription();
			json.setOgDescription(ogDescription);

			json.setOgTitle(title);
			if(webDocument.isSuccess()) {
				$LOG.debug("Parsing HTML was successful");
				json.setFullArticleText(article);

				/*Summarizer summarizer = new Summarizer();
				String summaryText = summarizer.Summarize(article, 3);

				json.setArticleSummaryText(summaryText);*/
			} else if($LOG.isDebugEnabled()){
				$LOG.debug("Parsing HTML didn't go well");
			}

			Summarizer summarizer = new Summarizer();
			String summaryText = summarizer.Summarize(article, 3);

			json.setArticleSummaryText(summaryText.replaceAll("\\s+", " ").replaceAll("\\t+", " ").replaceAll("\\n+", " ").replaceAll("\\r+", " "));
		} catch (IOException e) {
			$LOG.error(e.getMessage(), e);
		}

		return json;
	}

	private MatchRank checkMatch(String[][] wordMatrix, String elementText) {
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
						for(String word : words) {
							if (!partialMatches.toString().contains(word)) {
								partialMatches.append(word);
								partialMatches.append(" ");
							}
						}
					}
				}
			}
		}
		if(partialMatches.toString().isEmpty())
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
	
	private enum MatchRank {
		NO_MATCH, LOW, MEDIUM, HIGH
	}

	private WebElement findPrimaryElementByDescription(Document doc,
			String description) {
		String tagName = null;
		Element primaryElement = null;
		boolean isPrimaryElementHighMatched = false;
		String primaryElementClassName = null;
		int primaryElementDepth = 0;

		if (description == null || description.trim().isEmpty())
			return null;

		description = description.replaceAll("\\s+", " ").replaceAll("\\xA0",
				" ");
		List<String> words = LanguageUtil.getWords(description);
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

		String junk_detect_className = "squill_junk_detect";
		
		List<MarkedElement> markedElements = new LinkedList<MarkedElement>();
		Elements allElements = doc.body().getAllElements();
		Iterator<Element> itr = allElements.iterator();
		while (itr.hasNext()) {
			Element element = itr.next();
			String text = element.text().replaceAll("\\s+", " ")
					.replaceAll("\\xA0", " ");
			$LOG.debug(text);
			MatchRank matchRank = checkMatch(wordMatrix, text);
			switch (matchRank) {
			case NO_MATCH:
				if(primaryElement != null && element.parents().contains(primaryElement)) {
					element.addClass(junk_detect_className);
				}
				break;
			case HIGH:
			case MEDIUM:
				if (tagName == null) {
					tagName = element.tagName();
					primaryElement = element;
					primaryElementClassName = primaryElement.className();
					primaryElementDepth = depth(primaryElement, doc);
				} else if (isLeaf(element)) {
					tagName = element.tagName();
					primaryElement = element;
					primaryElementClassName = primaryElement.className();
					primaryElementDepth = depth(primaryElement, doc);
					return new WebElement()
							.setPrimaryElement(primaryElement)
							.setPrimaryElementClassName(primaryElementClassName)
							.setPrimaryElementDepth(primaryElementDepth)
							.setTagName(tagName);
				} else {
					int newDepth = depth(element, doc);
					if (newDepth > primaryElementDepth) {
						tagName = element.tagName();
						primaryElement = element;
						primaryElementClassName = primaryElement.className();
						primaryElementDepth = newDepth;
					}
				}
				break;
			case LOW:
				if(primaryElement != null && element.parents().contains(primaryElement)) {
					markedElements.add(new MarkedElement(primaryElement, primaryElementDepth));
					primaryElement = null;
					tagName = null;
					primaryElementDepth = 0;
				}
				break;
			}
		}

		if (primaryElement == null || tagName == null) {
			if(markedElements.isEmpty()) {
				return null;
			}
			float meanDepth = 0;
			for(MarkedElement markedElement : markedElements) {
				meanDepth = meanDepth + markedElement.getDepth();
			}
			meanDepth = meanDepth / markedElements.size();
			double variance = 0;
			for(MarkedElement markedElement : markedElements) {
				float diff = (markedElement.getDepth() - meanDepth);
				variance = variance + diff * diff;
			}
			variance = variance / markedElements.size();
			int SD = (int) Math.sqrt(variance);
			Iterator<MarkedElement> itr1 = markedElements.iterator();
			while(itr1.hasNext()) {
				MarkedElement markedElement = itr1.next();
				int diff = (int) Math.abs(markedElement.getDepth() - meanDepth);
				if(diff > SD) {
					itr1.remove();
				}
			}
			
			MarkedElement elWithMinHtml = null;
			int size = -1;
			for(MarkedElement markedElement : markedElements) {
				if(elWithMinHtml == null) {
					elWithMinHtml = markedElement;
					size = elWithMinHtml.getEl().getAllElements().size();
				} else if(markedElement.getEl().getAllElements().size() < size) {
					elWithMinHtml = markedElement;
					size = elWithMinHtml.getEl().getAllElements().size();
				}
			}
			
			primaryElement = elWithMinHtml.getEl();
			tagName = primaryElement.tagName();
			
			primaryElement.getElementsByClass(junk_detect_className).remove();
		}

		return new WebElement().setPrimaryElement(primaryElement)
				.setPrimaryElementClassName(primaryElementClassName)
				.setPrimaryElementDepth(primaryElementDepth)
				.setTagName(tagName);
	}
	
	private class MarkedElement {
		
		private Element el;
		
		private int depth;
		
		private MarkedElement(Element el, int depth) {
			this.el = el;
			this.depth = depth;
		}

		private Element getEl() {
			return el;
		}

		private int getDepth() {
			return depth;
		}
	}

	private WebElement findPrimaryElement(Document doc, String description,
			List<String> keywordsList) {
		WebElement result = findPrimaryElementByDescription(doc, description);
		if (result != null)
			return result;
		/*
		 * if (returnIfTagNameNotFound) { return null; }
		 */
		return null;
	}

	private int depth(Element element, Element commonAncestor) {
		int depth = 0;
		Element el = element;
		while (el != commonAncestor && el != null) {
			depth++;
			el = el.parent();
		}
		return depth;
	}

	private boolean isLeaf(Node node) {
		if(node.childNodes().isEmpty()) {
			return true;
		}
		Iterator<Node> itr = node.childNodes().iterator();
		while(itr.hasNext()) {
			Node next = itr.next();
			if(next instanceof Element) {
				return false;
			}
		}
		return true;
	}

	private Elements siblingElements(Element el) {
		Element parent = el.parent();
		if (parent == null)
			return new Elements();
		int size = parent.children().size();
		if (size == 0)
			return new Elements();
		return el.siblingElements();
	}

	private void removeStyleLinks(Document doc) {
		Elements elementsByTag = doc.getElementsByTag("style");
		if (elementsByTag != null && !elementsByTag.isEmpty()) {
			elementsByTag.remove();
		}
	}

	private void removeScripts(Document doc) {
		Elements elementsByTag = doc.getElementsByTag("script");
		if (elementsByTag != null && !elementsByTag.isEmpty()) {
			elementsByTag.remove();
		}
	}

	private void removeHeaders(Document doc) {
		Elements elementsByTag = doc.getElementsByTag("header");
		if (elementsByTag != null && !elementsByTag.isEmpty()) {
			elementsByTag.remove();
		}
	}

	private void removeFooters(Document doc) {
		Elements elementsByTag = doc.getElementsByTag("footer");
		if (elementsByTag != null && !elementsByTag.isEmpty()) {
			elementsByTag.remove();
		}
	}

	private static void removeComments(Node node) {
		for (int i = 0; i < node.childNodeSize();) {
			Node child = node.childNode(i);
			if (child.nodeName().equals("#comment"))
				child.remove();
			else {
				removeComments(child);
				i++;
			}
		}
	}
	
	private String readOgUrl(Document doc) {
		String url = null;
		Elements metaOgUrl = doc.select("meta[property=og:url]");
		if (metaOgUrl == null || metaOgUrl.isEmpty()) {
			metaOgUrl = doc.select("meta[property=twitter:url]");
		}
		if (metaOgUrl == null || metaOgUrl.isEmpty()) {
			metaOgUrl = doc.select("meta[name=og:url]");
		}
		if (metaOgUrl == null || metaOgUrl.isEmpty()) {
			metaOgUrl = doc.select("meta[name=twitter:url]");
		}
		if (metaOgUrl != null && !metaOgUrl.isEmpty()) {
			url = metaOgUrl.attr("content");
			if(url == null || url.trim().isEmpty()) {
				url = metaOgUrl.attr("content");
			}
		}
		if(url == null || url.trim().isEmpty()) {
			return null;
		}
		return url;
	}

	private String readOgTilte(Document doc) {
		String title = null;
		Elements metaOgTitle = doc.select("meta[property=og:title]");
		if (metaOgTitle == null || metaOgTitle.isEmpty()) {
			metaOgTitle = doc.select("meta[property=twitter:title]");
		}
		if (metaOgTitle == null || metaOgTitle.isEmpty()) {
			metaOgTitle = doc.select("meta[name=og:title]");
		}
		if (metaOgTitle == null || metaOgTitle.isEmpty()) {
			metaOgTitle = doc.select("meta[name=twitter:title]");
		}
		if (metaOgTitle == null || metaOgTitle.isEmpty()) {
			metaOgTitle = doc.select("meta[property=title]");
		}
		if (metaOgTitle == null || metaOgTitle.isEmpty()) {
			Elements els = doc.head().getElementsByTag("title");
			if(els != null && !els.isEmpty()) {
				return els.get(0).text();
			}
		}
		if (metaOgTitle != null && !metaOgTitle.isEmpty()) {
			title = metaOgTitle.attr("content");
			if(title == null) {
				title = metaOgTitle.attr("value");
			}
		}
		if(title == null || title.trim().isEmpty()) {
			return null;
		}
		return title;
	}

	private String readOgDescription(Document doc) {
		String description = null;
		Elements metaOgDescription = doc
				.select("meta[property=og:description]");
		if (metaOgDescription == null || metaOgDescription.isEmpty()) {
			metaOgDescription = doc
					.select("meta[property=twitter:description]");
		}
		if (metaOgDescription == null || metaOgDescription.isEmpty()) {
			metaOgDescription = doc
					.select("meta[name=og:description]");
		}
		if (metaOgDescription == null || metaOgDescription.isEmpty()) {
			metaOgDescription = doc
					.select("meta[name=twitter:description]");
		}
		if (metaOgDescription == null || metaOgDescription.isEmpty()) {
			metaOgDescription = doc.select("meta[property=description]");
		}
		if (metaOgDescription != null && !metaOgDescription.isEmpty()) {
			description = metaOgDescription.attr("content");
			if(description == null) {
				description = metaOgDescription.attr("value");
			}
		}
		if(description == null || description.trim().isEmpty()) {
			return null;
		}
		description = LanguageUtil.cleanHtmlInvisibleCharacters(description);
		return description;
	}
	
	private String readOgImage(Document doc) {
		String imageUrl = null;
		Elements metaOgImage = doc
				.select("meta[property=og:image]");
		if (metaOgImage == null || metaOgImage.isEmpty()) {
			metaOgImage = doc
					.select("meta[property=twitter:image]");
		}
		if (metaOgImage == null || metaOgImage.isEmpty()) {
			metaOgImage = doc
					.select("meta[name=og:image]");
		}
		if (metaOgImage == null || metaOgImage.isEmpty()) {
			metaOgImage = doc
					.select("meta[name=twitter:image]");
		}
		if (metaOgImage != null && !metaOgImage.isEmpty()) {
			imageUrl = metaOgImage.attr("content");
			if(imageUrl == null) {
				imageUrl = metaOgImage.attr("value");
			}
		}
		if(imageUrl == null || imageUrl.trim().isEmpty()) {
			return null;
		}
		imageUrl = LanguageUtil.cleanHtmlInvisibleCharacters(imageUrl);
		return imageUrl;
	}

	private List<String> readKeywordsList(Document doc) {
		Elements metaOgKeywords = doc.select("meta[name=keywords]");
		String keywordsText = metaOgKeywords.attr("content");
		List<String> keywordsList = new ArrayList<String>();
		if (keywordsText != null) {
			String[] keywords = keywordsText.split(",");
			for (String keyword : keywords) {
				keywordsList.add(keyword.trim());
			}
		}
		return keywordsList;
	}

	private Element findLowestCommonAncestor(Element el1, Element el2, Element treeBaseRoot) {
		Element p1 = el1;
		Element p2 = el2;
		if (p1 == null || p2 == null) {
			return null;
		}
		Element root = p1;
		StringBuilder str = new StringBuilder();
		while (p1 != null) {
			root = p1;
			str.append(p1.tagName());
			p1 = p1.parent();
			if (p1 != null) {
				str.append(",");
			}
		}
		String[] p1PathSequence = str.reverse().toString().split(",");
		str = new StringBuilder();
		while (p2 != null) {
			str.append(p2.tagName());
			p2 = p2.parent();
			if (p2 != null) {
				str.append(",");
			}
		}
		String[] p2PathSequence = str.reverse().toString().split(",");
		int i = 0;
		String p1Path = p1PathSequence[i];
		String p2Path = p2PathSequence[i];
		while (p1Path.equals(p2Path)) {
			i++;
			if (i >= p1PathSequence.length || i >= p2PathSequence.length) // Not a bug this is intentional
				return root;
			/*if (i >= p1PathSequence.length)
				return el1;
			else if (i >= p2PathSequence.length)
				return el2;*/
			p1Path = p1PathSequence[i];
			p2Path = p2PathSequence[i];
		}
		if (i == 0) {
			return root;
		}
		int count = p1PathSequence.length - i;
		i = p1PathSequence.length;
		p1 = el1;
		while (count > 0) {
			p1 = p1.parent();
			count--;
		}
		
		// Check if article exists above the hierarchy
		Element tmp = p1.parent();
		while (tmp != null && !tmp.equals(treeBaseRoot)) {
			if ("article".equals(tmp.tagName())) {
				String role = tmp.attr("role");
				if (role != null
						&& (role.equalsIgnoreCase("main") || role
								.equalsIgnoreCase("content"))) {
					p1 = tmp;
					break;
				}
			}
			tmp = tmp.parent();
		}
		
		return p1;
	}
	
	private AmpHtml parseAmpHtml(Document doc, String description) throws Exception {
		Elements elements = doc.getElementsByTag("link");
		if(elements != null && !elements.isEmpty()) {
			Iterator<Element> itr = elements.iterator();
			while(itr.hasNext()) {
				Element element = itr.next();
				String rel = element.attr("rel");
				if(rel == null)
					continue;
				if("amphtml".equalsIgnoreCase(rel.trim())) {
					String href = element.attr("href");
					if(href != null && !href.trim().isEmpty()) {
						HttpGet GET = new HttpGet(href);
						HttpResponse response = new HttpRequestExecutor().GET(GET);
						int statusCode = response.getStatusLine().getStatusCode();
						if (statusCode == 200) {
							HttpEntity entity = response.getEntity();
							if (entity != null) {
								String html = EntityUtils.toString(entity);
								$LOG.debug("Parsing HTML");
								return new AmpHtml(html, href);
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	private class AmpHtml {
		
		private String html;
		
		private String sourceUrl;
		
		private AmpHtml(String html, String sourceUrl) {
			this.html = html;
			this.sourceUrl = sourceUrl;
		}

		private String getHtml() {
			return html;
		}

		private String getSourceUrl() {
			return sourceUrl;
		}
	}
	
	private WebDocument parseHtml(String html) throws IOException {
		return doParseHtml(html, false);
	}

	private WebDocument doParseHtml(String html, boolean isAmpHtmlLink) throws IOException {
		Document doc = Jsoup.parse(html);
		//$LOG.debug(doc.body().text());
		String title = readOgTilte(doc);
		String description = readOgDescription(doc);
		String imageUrl = readOgImage(doc);
		List<String> keywordsList = readKeywordsList(doc);
		String ogUrl = readOgUrl(doc);
		String inputUrl = ogUrl;
		
		if (!isAmpHtmlLink) {
			try {
				AmpHtml ampHtml = parseAmpHtml(doc, description);
				if (ampHtml != null) {
					String html2 = ampHtml.getHtml();
					if (html2 != null && !html2.trim().isEmpty()) {
						Document doc1 = Jsoup.parse(html2);
						String title1 = readOgTilte(doc1);
						String description1 = readOgDescription(doc1);
						String imageUrl1 = readOgImage(doc1);
						if(title1 != null && !title1.trim().isEmpty()) {
							title = title1;
						}
						if(description1 != null && !description1.trim().isEmpty()) {
							description = description1;
						}
						if(imageUrl1 != null && !imageUrl1.trim().isEmpty()) {
							imageUrl = imageUrl1;
						}
						if(imageUrl == null || imageUrl.trim().isEmpty()) {
							Elements images = doc1.getElementsByTag("img");
							if(images != null && !images.isEmpty()) {
								String imgSrc = images.get(0).attr("src");
								if(imgSrc != null && !imgSrc.trim().isEmpty()) {
									imageUrl = imgSrc;
								}
							}
						}
						if(imageUrl == null || imageUrl.trim().isEmpty()) {
							Elements images = doc.getElementsByTag("img");
							if(images != null && !images.isEmpty()) {
								String imgSrc = images.get(0).attr("src");
								if(imgSrc != null && !imgSrc.trim().isEmpty()) {
									imageUrl = imgSrc;
								}
							}
						}
						/*removeComments(doc1);
						removeScripts(doc1);
						removeStyleLinks(doc1);
						return new WebDocument(title, description, imageUrl,
								ampHtml.getSourceUrl(), doc1).setSuccess(true);*/
						ogUrl = ampHtml.getSourceUrl();
						
						doc = doc1;
					}
				}
			} catch (Exception e) {
				$LOG.error(e.getMessage(), e);
			}
		}
		
		$LOG.debug("Removing Comments");
		removeComments(doc);
		$LOG.debug("Removing JS Skripts");
		removeScripts(doc);
		$LOG.debug("Removing CSS Style links");
		removeStyleLinks(doc);

		WebElement primaryElement = findPrimaryElement(doc, description,
				keywordsList);
		if (primaryElement == null) {
			$LOG.debug("Failed to get primary element based upon description");
			return new WebDocument(title, description, imageUrl, ogUrl, inputUrl, doc);
		}
		Element majorElement = primaryElement.getPrimaryElement();
		if (majorElement == null) {
			$LOG.debug("Failed to get primary/major element based upon description");
			return new WebDocument(title, description, imageUrl, ogUrl, inputUrl, doc);
		}

		Element h1Element = null;
		Elements h1Elements = doc.getElementsByTag(H1_TAG_NAME);
		if (h1Elements != null && !h1Elements.isEmpty()) {
			h1Element = h1Elements.get(0);
		}
		if (h1Element == null) {
			$LOG.debug("No <h1> tag found");
			return new WebDocument(title, description, imageUrl, ogUrl, inputUrl, doc);
		} else {
			title = h1Element.text();
		}

		$LOG.debug("Trying to find LCA");
		Element _LCA = findLowestCommonAncestor(majorElement, h1Element, doc.body());
		if (_LCA == null) {
			$LOG.debug("Failed to compute LCA node");
			return new WebDocument(title, description, imageUrl, ogUrl, inputUrl, doc);
		}
		
		title = h1Element.text();
		
		if(imageUrl == null) {
			Elements images = _LCA.getElementsByTag("img");
			if(images != null && !images.isEmpty()) {
				String imgSrc = images.get(0).attr("src");
				if(imgSrc != null && !imgSrc.trim().isEmpty()) {
					imageUrl = imgSrc;
				}
			}
		}

		Elements siblingElements = siblingElements(_LCA);
		Iterator<Element> itr = siblingElements.iterator();
		while (itr.hasNext()) {
			Element element = itr.next();
			if (_LCA.equals(element))
				continue;
			element.remove();
		}

		$LOG.debug("Cleaning up unrelated Sub Trees");
		cleanUpUnrelatedSubTrees(doc.body(), _LCA);
		cleanUpLCA(_LCA, majorElement);

		Elements elementsByTag = _LCA.getElementsByTag(H1_TAG_NAME);
		if (elementsByTag != null && !elementsByTag.isEmpty()) {
			title = LanguageUtil.cleanHtmlInvisibleCharacters(elementsByTag
					.get(0).text());
			elementsByTag.remove();
		}

		$LOG.debug("Removing headers");
		removeHeaders(doc);
		$LOG.debug("Removing footers");
		removeFooters(doc);

		html = "<html><body>" + _LCA.html() + "</body></html>";
		$LOG.trace("Final HTML to parse = ");
		Document document = Jsoup.parse(html);
		$LOG.trace(document.text());
		$LOG.trace(document.outerHtml());

		return new WebDocument(title, description, imageUrl, ogUrl, inputUrl, document).setSuccess(true);
	}

	private void cleanUpLCA(Element lca, Element majorElement) {
		String tagNameToConsiderOnly = null;
		Set<String> cssClassesToConsider = null;
		List<Node> childNodes = lca.childNodes();
		Element majorParent = null;
		if (childNodes != null && !childNodes.isEmpty()
				&& !childNodes.contains(majorElement)) {
			Iterator<Element> itr = majorElement.parents().iterator();
			while (itr.hasNext()) {
				Element element = itr.next();
				if (element == null)
					continue;
				if (childNodes.contains(element)) {
					majorParent = element;
					tagNameToConsiderOnly = element.tagName();
					Set<String> classNames = element.classNames();
					if (classNames != null && !classNames.isEmpty()) {
						cssClassesToConsider = classNames;
					}
				}
			}
		}

		if (tagNameToConsiderOnly != null) {
			Iterator<Element> itr = lca.children().iterator();
			while (itr.hasNext()) {
				Element subTree = itr.next();
				if (H1_TAG_NAME.equals(subTree.tagName()))
					continue;
				if (tagNameToConsiderOnly.equals(subTree.tagName())
						&& compareCssStyles(cssClassesToConsider,
								subTree.classNames()))
					continue;
				subTree.remove();
			}
			
			if (majorParent != null) {
				itr = lca.children().iterator();
				if (itr.hasNext()) {
					String xPath = calculateXPath(majorParent, majorElement);
					if (!xPath.trim().equals("")) {
						xPath = majorParent.tagName() + " > " + xPath;
						while (itr.hasNext()) {
							Element subTree = itr.next();
							if (H1_TAG_NAME.equals(subTree.tagName()))
								continue;
							if (isEQUAL(majorParent, subTree))
								continue;
							String html = "<html><body>" + subTree.html()
									+ "</body></html>";
							Document document = Jsoup.parse(html);
							Elements elements = document.select(xPath);
							if (elements == null || elements.isEmpty()) {
								subTree.remove();
							}
						}
					}
				}
			}
		}
		
		if (isLeaf(majorElement)) {
			String tagName0 = majorElement.tagName();
			if(tagName0 == null)
				return;
			Elements allElements = lca.getAllElements();
			if (allElements != null && !allElements.isEmpty()) {
				Iterator<Element> itr = allElements.iterator();
				while (itr.hasNext()) {
					Element el = itr.next();
					if (isLeaf(el)) {
						String tagName1 = el.tagName();
						if (!tagName0.equalsIgnoreCase(tagName1)) {
							el.remove();
						}
					}
				}
			}
		}
	}

	private String calculateXPath(Element root, Element node) {
		if (isEQUAL(root, node)) {
			return "";
		}
		Element child = null;
		Iterator<Element> itr = root.children().iterator();
		while (itr.hasNext()) {
			Element el = itr.next();
			if (node.equals(el) || node.parents().contains(el)) {
				child = el;
				break;
			}
		}
		if (child == null)
			return "";
		StringBuilder xPath = new StringBuilder();
		xPath.append(child.tagName());
		String subXPath = calculateXPath(child, node);
		if (!subXPath.trim().isEmpty()) {
			xPath.append(" > ");
			xPath.append(subXPath);
		}
		return xPath.toString();
	}

	private boolean isEQUAL(Element tree1, Element tree2) {
		if (tree1 == null || tree2 == null) {
			return true;
		}
		if (tree1.equals(tree2)) {
			return true;
		}
		return false;
	}

	private boolean compareCssStyles(Set<String> cssClassNamesSet1,
			Set<String> cssClassNamesSet2) {
		if (cssClassNamesSet1 == null && cssClassNamesSet2 == null)
			return true;
		if (cssClassNamesSet1 == null && cssClassNamesSet2 != null)
			return false;
		if (cssClassNamesSet1 != null && cssClassNamesSet2 == null)
			return false;
		if (cssClassNamesSet1.isEmpty() && cssClassNamesSet2.isEmpty())
			return true;
		if (cssClassNamesSet1.isEmpty() && !cssClassNamesSet2.isEmpty())
			return false;
		if (!cssClassNamesSet1.isEmpty() && cssClassNamesSet2.isEmpty())
			return false;
		List<String> cssClassNamesList1 = new ArrayList<String>(
				cssClassNamesSet1);
		Collections.sort(cssClassNamesList1);
		List<String> cssClassNamesList2 = new ArrayList<String>(
				cssClassNamesSet2);
		Collections.sort(cssClassNamesList2);
		Iterator<String> itr = cssClassNamesList2.iterator();
		while (itr.hasNext()) {
			String cssClassName = itr.next();
			cssClassNamesList1.remove(cssClassName);
		}
		return cssClassNamesList1.isEmpty();
	}

	private void cleanUpUnrelatedSubTrees(Element root, Element subTree) {
		if (root == null || subTree == null)
			return;
		Elements parents = subTree.parents();
		if (parents == null || parents.isEmpty())
			return;
		List<Element> parentsList = new ArrayList<Element>();
		Iterator<Element> itr = parents.iterator();
		while (itr.hasNext()) {
			Element parent = itr.next();
			if (root.equals(parent))
				continue;
			parentsList.add(parent);
		}
		Elements childElements = root.children();
		itr = childElements.iterator();
		while (itr.hasNext()) {
			Element tree = itr.next();
			if (subTree.equals(tree))
				continue;
			boolean remove = !(parentsList.contains(tree)) || isLeaf(tree);
			if (remove) {
				tree.remove();
			}
		}

		List<Node> childNodes = root.childNodes();
		int size = childNodes.size();
		for (int i = 0; i < size; i++) {
			Node tree = childNodes.get(i);
			if (subTree.equals(tree))
				continue;
			boolean remove = !(parentsList.contains(tree)) || isLeaf(tree);
			if (remove) {
				tree.remove();
				size = size - 1;
				i--;
			}
		}
	}

	private class WebElement {

		private String tagName;

		private Element primaryElement = null;

		private String primaryElementClassName = null;

		private int primaryElementDepth = -1;

		public String getTagName() {
			return tagName;
		}

		public WebElement setTagName(String tagName) {
			this.tagName = tagName;
			return this;
		}

		public Element getPrimaryElement() {
			return primaryElement;
		}

		public WebElement setPrimaryElement(Element primaryElement) {
			this.primaryElement = primaryElement;
			return this;
		}

		public String getPrimaryElementClassName() {
			return primaryElementClassName;
		}

		public WebElement setPrimaryElementClassName(
				String primaryElementClassName) {
			this.primaryElementClassName = primaryElementClassName;
			return this;
		}

		public int getPrimaryElementDepth() {
			return primaryElementDepth;
		}

		public WebElement setPrimaryElementDepth(int primaryElementDepth) {
			this.primaryElementDepth = primaryElementDepth;
			return this;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null && (obj instanceof WebElement)) {
				WebElement w1 = (WebElement) obj;
				return this.primaryElement.equals(w1.primaryElement)
						&& this.tagName.equals(w1.tagName);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.primaryElement.hashCode() + this.tagName.hashCode();
		}
	}
}