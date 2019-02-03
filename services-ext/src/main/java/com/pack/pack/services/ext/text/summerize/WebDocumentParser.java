package com.pack.pack.services.ext.text.summerize;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
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
import org.xml.sax.SAXException;

import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
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
	
	private static final String H2_TAG_NAME = "h2";
	
	private static final Logger $LOG = LoggerFactory
			.getLogger(WebDocumentParser.class);
	
	private boolean strictSuccessMode = false;
	
	private String url;
	
	private String payload;
	
	private boolean isUrlBasedProcessing;
	
	private String placeHolderText;
	
	private boolean summerize;
	
	public WebDocumentParser() {
		this(null, null);
	}
	
	public WebDocumentParser(String url) {
		this(url, null);
	}
	
	public WebDocumentParser(String url, String payload) {
		this(url, null, null, true, false);
	}
	
	public WebDocumentParser(String url, String payload, String placeHolderText, boolean summerize, boolean strictSuccessMode) {
		this.url = url;
		this.payload = payload;
		this.placeHolderText = placeHolderText;
		this.summerize = summerize;
		this.strictSuccessMode = strictSuccessMode;
		this.isUrlBasedProcessing = (url != null && !url.trim().isEmpty());
	}
	
	public WebDocumentParser setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public JRssFeed parse() {
		JRssFeed json = new JRssFeed();
		try {
			String content = this.payload;
			if (this.isUrlBasedProcessing) {
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
				if (entity.getContentType().getValue().toLowerCase()
						.contains("image")) {
					json.setOgImage(url);
					json.setOgUrl(url);
					json.setHrefSource(url);
					return json;
				}
				content = EntityUtils.toString(entity);
			}
			json = parseHtmlPayload(content, placeHolderText, summerize);
			if (json != null
					&& (json.getOgUrl() == null || json.getOgUrl().trim()
							.isEmpty())) {
				json.setOgUrl(url);
			}
			
			if (json != null
					&& (json.getHrefSource() == null || json.getHrefSource().trim()
							.isEmpty())) {
				json.setHrefSource(url);
			}
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
			$LOG.error(e.getMessage(), e);
		}
		return json;
	}
	
	public JRssFeed parseHtmlPayload(String content) {
		return parseHtmlPayload(content, null, true);
	}
	
	private JRssFeed parseHtmlPayload(String content, String placeHolder, boolean summerize) {
		JRssFeed json = new JRssFeed();
		String article = "";
		String ogDescription = "";
		try {
			$LOG.debug("Parsing HTML");
			WebDocument webDocument = parseHtml(content, placeHolder);
			if(!webDocument.isSuccess() && !webDocument.isCompatible()) {
				/*InputStream input = new ByteArrayInputStream(webDocument
						.getFilteredHtml().getBytes());
				ContentHandler textHandler = new BodyContentHandler();
				Metadata metadata = new Metadata();
				AutoDetectParser parser = new AutoDetectParser();
				ParseContext context = new ParseContext();
				BoilerpipeContentHandler handler2 = new BoilerpipeContentHandler(
						textHandler, LargestContentExtractor.getInstance());
				$LOG.debug("Parsing HTML using Boilerpipe Content Handler/AutoDetectParser");
				try {
					parser.parse(input, handler2, metadata, context);
				} catch (SAXException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (TikaException e) {
					throw new RuntimeException(e.getMessage(), e);
				}

				article = textHandler.toString();*/
			} else {
				article = Jsoup.parse(new String(webDocument.getFilteredHtml()
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

				ogDescription = webDocument.getDescription();
				json.setOgDescription(ogDescription);

				json.setOgTitle(title);
				if(webDocument.isSuccess()) {
					$LOG.debug("Parsing HTML was successful");
					json.setFullArticleText(article);
					json.setHtmlSnippet(webDocument.getExtractedHtmlSnippet());

					/*Summarizer summarizer = new Summarizer();
					String summaryText = summarizer.Summarize(article, 3);

					json.setArticleSummaryText(summaryText);*/
				} else {
					$LOG.debug("Parsing HTML didn't go well");
					if(strictSuccessMode){
						return null;
					}
				}
			}
			
			if(summerize) {
				Summarizer summarizer = new Summarizer();
				String summaryText = summarizer.Summarize(article, 3);

				if(summaryText == null || summaryText.trim().isEmpty()) {
					json.setArticleSummaryText(ogDescription);
				} else {
					json.setArticleSummaryText(summaryText.replaceAll("\\s+", " ")
							.replaceAll("\\t+", " ").replaceAll("\\n+", " ")
							.replaceAll("\\r+", " "));
				}
			}
		} catch (IOException e) {
			$LOG.error(e.getMessage(), e);
		} catch (BoilerpipeProcessingException e) {
			$LOG.error(e.getMessage(), e);
		} catch (SAXException e) {
			$LOG.error(e.getMessage(), e);
		}

		return json;
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
			MatchRank matchRank = MatchRank.checkMatch(wordMatrix, text);
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
					primaryElementDepth = WebDocumentUtil.depth(primaryElement, doc);
				} else if (WebDocumentUtil.isLeaf(element)) {
					tagName = element.tagName();
					primaryElement = element;
					primaryElementClassName = primaryElement.className();
					primaryElementDepth = WebDocumentUtil.depth(primaryElement, doc);
					if (!WebDocumentUtil.isHeaderElement(element)) {
						return new WebElement()
								.setPrimaryElement(primaryElement)
								.setPrimaryElementClassName(
										primaryElementClassName)
								.setPrimaryElementDepth(primaryElementDepth)
								.setTagName(tagName);
					}
				} else {
					int newDepth = WebDocumentUtil.depth(element, doc);
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
	
	private WebDocument parseHtml(String html, String placeHolder) throws IOException, BoilerpipeProcessingException, SAXException {
		return doParseHtml(html, placeHolder, false);
	}

	private WebDocument doParseHtml(String html, String placeHolder, boolean isAmpHtmlLink) throws IOException, BoilerpipeProcessingException, SAXException {
		Document doc = Jsoup.parse(html);
		//$LOG.debug(doc.body().text());
		String title = WebDocumentUtil.readOgTilte(doc);
		String tmpDescription = WebDocumentUtil.readOgDescription(doc);
		String imageUrl = WebDocumentUtil.readOgImage(doc);
		String ogUrl = WebDocumentUtil.readOgUrl(doc);
		String inputUrl = ogUrl;
		/*if(title == null && tmpDescription == null && imageUrl == null && ogUrl == null) {
			return new WebDocument(title, tmpDescription, imageUrl, ogUrl, inputUrl, doc, null).setCompatible(false);
		}*/
		String description = placeHolder;
		if(description == null || description.trim().isEmpty()) {
			description = tmpDescription;
		}
		//String description = readOgDescription(doc);
		List<String> keywordsList = WebDocumentUtil.readKeywordsList(doc);
		
		if (!isAmpHtmlLink) {
			try {
				AmpHtml ampHtml = parseAmpHtml(doc, description);
				if (ampHtml != null) {
					String html2 = ampHtml.getHtml();
					if (html2 != null && !html2.trim().isEmpty()) {
						Document doc1 = Jsoup.parse(html2);
						String title1 = WebDocumentUtil.readOgTilte(doc1);
						String description1 = WebDocumentUtil.readOgDescription(doc1);
						String imageUrl1 = WebDocumentUtil.readOgImage(doc1);
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
		WebDocumentUtil.removeComments(doc);
		$LOG.debug("Removing JS Skripts");
		WebDocumentUtil.removeScripts(doc);
		$LOG.debug("Removing CSS Style links");
		WebDocumentUtil.removeStyleLinks(doc);
		
		if (description == null || description.trim().isEmpty()) {
			return BoilerpipeHtmlProcessor.newInstance().process(
					html, doc.outerHtml(), title, description, imageUrl, ogUrl,
					inputUrl, this.url);
		}

		WebElement primaryElement = findPrimaryElement(doc, description,
				keywordsList);
		if (primaryElement == null) {
			$LOG.debug("Failed to get primary element based upon description");
			//return new WebDocument(title, description, imageUrl, ogUrl, inputUrl, doc, null);
			return BoilerpipeHtmlProcessor.newInstance().process(
					html, doc.outerHtml(), title, description, imageUrl, ogUrl,
					inputUrl, this.url);
		}
		Element majorElement = primaryElement.getPrimaryElement();
		if (majorElement == null) {
			$LOG.debug("Failed to get primary/major element based upon description");
			//return new WebDocument(title, description, imageUrl, ogUrl, inputUrl, doc, null);
			return BoilerpipeHtmlProcessor.newInstance().process(
					html, doc.outerHtml(), title, description, imageUrl, ogUrl,
					inputUrl, this.url);
		}

		Element h1Element = null;
		Elements h1Elements = doc.getElementsByTag(H1_TAG_NAME);
		if (h1Elements != null && !h1Elements.isEmpty()) {
			h1Element = h1Elements.get(0);
		}
		if (h1Element == null) {
			$LOG.debug("No <h1> tag found");
			//return new WebDocument(title, description, imageUrl, ogUrl, inputUrl, doc, null);
			return BoilerpipeHtmlProcessor.newInstance().process(
					html, doc.outerHtml(), title, description, imageUrl, ogUrl,
					inputUrl, this.url);
		} else {
			title = h1Element.text();
		}

		$LOG.debug("Trying to find LCA");
		Element _LCA = WebDocumentUtil.findLowestCommonAncestor(majorElement, h1Element, doc.body());
		if (_LCA == null) {
			$LOG.debug("Failed to compute LCA node");
			//return new WebDocument(title, description, imageUrl, ogUrl, inputUrl, doc, null);
			return BoilerpipeHtmlProcessor.newInstance().process(
					html, doc.outerHtml(), title, description, imageUrl, ogUrl,
					inputUrl, this.url);
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

		Elements siblingElements = WebDocumentUtil.siblingElements(_LCA);
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
		WebDocumentUtil.removeHeaders(doc);
		$LOG.debug("Removing footers");
		WebDocumentUtil.removeFooters(doc);

		html = "<html><head><script async src=\"https://cdn.ampproject.org/v0.js\"></script></head><body>" + _LCA.html() + "</body></html>";
		$LOG.trace("Final HTML to parse = ");
		Document document = Jsoup.parse(html);
		$LOG.trace(document.text());
		$LOG.trace(document.outerHtml());

		return new WebDocument(title, description, imageUrl, ogUrl, inputUrl, document, _LCA.outerHtml()).setSuccess(true);
	}

	private void cleanUpLCA(Element lca, Element majorElement) {
		String tagNameToConsiderOnly = null;
		Set<String> cssClassesToConsider = null;
		
		if(lca == null || majorElement == null)
			return;
		
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
				if (H1_TAG_NAME.equals(subTree.tagName()) || H2_TAG_NAME.equals(subTree.tagName()))
					continue;
				if (tagNameToConsiderOnly.equals(subTree.tagName())
						&& compareCssStyles(cssClassesToConsider,
								subTree.classNames()))
					continue;
				//subTree.remove();
				WebDocumentUtil.cleanUpHtmlTree(subTree);
			}
			
			if (majorParent != null) {
				itr = lca.children().iterator();
				if (itr.hasNext()) {
					String xPath = calculateXPath(majorParent, majorElement);
					if (!xPath.trim().equals("")) {
						xPath = majorParent.tagName() + " > " + xPath;
						while (itr.hasNext()) {
							Element subTree = itr.next();
							if (H1_TAG_NAME.equals(subTree.tagName()) || H2_TAG_NAME.equals(subTree.tagName()))
								continue;
							if (isEQUAL(majorParent, subTree))
								continue;
							String html = "<html><body>" + subTree.html()
									+ "</body></html>";
							Document document = Jsoup.parse(html);
							Elements elements = document.select(xPath);
							if (elements == null || elements.isEmpty()) {
								//subTree.remove();
								WebDocumentUtil.cleanUpHtmlTree(subTree);
							}
						}
					}
				}
			}
		}
		
		if (WebDocumentUtil.isLeaf(majorElement)) {
			String tagName0 = majorElement.tagName();
			if(tagName0 == null)
				return;
			Elements allElements = lca.getAllElements();
			if (allElements != null && !allElements.isEmpty()) {
				Iterator<Element> itr = allElements.iterator();
				while (itr.hasNext()) {
					Element el = itr.next();
					if (WebDocumentUtil.isLeaf(el)) {
						String tagName1 = el.tagName();
						if (!tagName0.equalsIgnoreCase(tagName1)
								&& !tagName1
										.equalsIgnoreCase(WebDocumentUtil.IMG_TAG_NAME)
								&& !tagName1
										.equalsIgnoreCase(WebDocumentUtil.AMP_IMG_TAG_NAME)
								&& !WebDocumentUtil.isChildOfHeaderElement(el)) {
							// el.remove();
							WebDocumentUtil.cleanUpHtmlTree(el);
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
		if(root == subTree)
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
			boolean remove = !(parentsList.contains(tree)) || WebDocumentUtil.isLeaf(tree);
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
			boolean remove = !(parentsList.contains(tree)) || WebDocumentUtil.isLeaf(tree);
			if (remove) {
				tree.remove();
				size = size - 1;
				i--;
			}
		}
	}
}