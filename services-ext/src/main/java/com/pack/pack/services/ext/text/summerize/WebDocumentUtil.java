package com.pack.pack.services.ext.text.summerize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.pack.pack.util.LanguageUtil;

/**
 * 
 * @author Saurav
 *
 */
public class WebDocumentUtil {
	
	public static final String IMG_TAG_NAME = "img";
	
	public static final String AMP_IMG_TAG_NAME = "amp-img";
	
	private static final String FIGURE_TAG_NAME = "figure";
	
	private static final String SRC_ATTR_NAME = "src";
	
	private WebDocumentUtil() {
	}
	
	static void cleanUpHtmlTree(Element subTree) {
		// subTree.remove();
		if(subTree == null)
			return;

		if(isLeaf(subTree)) {
			String tagName = subTree.tagName();
			if(IMG_TAG_NAME.equalsIgnoreCase(tagName)/* || IFRAME_TAG_NAME.equalsIgnoreCase(tagName)*/) {
				if (subTree.hasAttr(SRC_ATTR_NAME)) {
					String src = subTree.attr(SRC_ATTR_NAME);
					if (src == null || src.trim().isEmpty()
							|| !src.trim().startsWith("http")) {
						subTree.remove();
					} else if (AdBlocker.INSTANCE.isAdvertisement(src.trim())) {
						subTree.remove();
					}
				} else {
					subTree.remove();
				}
			} else if(!"h1".equalsIgnoreCase(tagName) && !"h2".equalsIgnoreCase(tagName)) {
				subTree.remove();
			}
		} else {
			Elements elements = subTree.getElementsByTag(IMG_TAG_NAME);
			Iterator<Element> itr = elements.iterator();
			Set<Node> parentsList = new HashSet<Node>();
			while(itr.hasNext()) {
				Element img = itr.next();
				if(img.hasAttr(SRC_ATTR_NAME)) {
					String src = img.attr(SRC_ATTR_NAME);
					if(src == null || src.trim().isEmpty() || !src.trim().startsWith("http")) {
						img.remove();
					}
					else if(AdBlocker.INSTANCE.isAdvertisement(src.trim())) {
						img.remove();
					} else {
						Iterator<Element> itr1 = img.parents().iterator();
						while(itr1.hasNext()) {
							parentsList.add(itr1.next());
						}
					}
				} else {
					img.remove();
				}
			}
			
			elements = subTree.getElementsByTag(AMP_IMG_TAG_NAME);
			itr = elements.iterator();
			while(itr.hasNext()) {
				Element img = itr.next();
				if(img.hasAttr(SRC_ATTR_NAME)) {
					String src = img.attr(SRC_ATTR_NAME);
					if(src == null || src.trim().isEmpty() || !src.trim().startsWith("http")) {
						img.remove();
					}
					else if(AdBlocker.INSTANCE.isAdvertisement(src.trim())) {
						img.remove();
					} else {
						Iterator<Element> itr1 = img.parents().iterator();
						while(itr1.hasNext()) {
							parentsList.add(itr1.next());
						}
					}
				} else {
					img.remove();
				}
			}
			
			elements = subTree.getElementsByTag(FIGURE_TAG_NAME);
			itr = elements.iterator();
			while(itr.hasNext()) {
				Element figure = itr.next();
				if(figure.hasAttr(SRC_ATTR_NAME)) {
					String src = figure.attr(SRC_ATTR_NAME);
					if(src == null || src.trim().isEmpty() || !src.trim().startsWith("http")) {
						figure.remove();
					} else if(AdBlocker.INSTANCE.isAdvertisement(src.trim())) {
						figure.remove();
					}
				} else {
					Elements images = figure.getElementsByTag(IMG_TAG_NAME);
					Iterator<Element> itr1 = images.iterator();
					while(itr1.hasNext()) {
						Element img = itr1.next();
						String src = img.attr(SRC_ATTR_NAME);
						if(src == null || src.trim().isEmpty() || !src.trim().startsWith("http")) {
							img.remove();
						}
						else if(AdBlocker.INSTANCE.isAdvertisement(src.trim())) {
							img.remove();
						} else {
							Iterator<Element> itr2 = figure.parents().iterator();
							while(itr2.hasNext()) {
								parentsList.add(itr2.next());
							}
						}
					}
				}
			}
			
			elements = subTree.children();
			itr = elements.iterator();
			while(itr.hasNext()) {
				Element element = itr.next();
				if(!parentsList.contains(element)) {
					if(element.getElementsByTag("h1").isEmpty() && element.getElementsByTag("h2").isEmpty()) {
						element.remove();
					}
				}
			}
			
			/*List<Node> nodes = subTree.childNodes();
			Iterator<Node> itrNodes = nodes.iterator();
			while(itrNodes.hasNext()) {
				Node node = itrNodes.next();
				if(!parentsList.contains(node)) {
					node.remove();
				}
			}*/
			
			if(isEmpty(subTree)) {
				subTree.remove();
			}
		}
	}
	
	static boolean isEmpty(Element subTree) {
		boolean noElements = false;
		Elements elements = subTree.children();
		if(elements == null || elements.isEmpty()) {
			noElements = true;
		}
		boolean noNodes = false;
		List<Node> childNodes = subTree.childNodes();
		if(childNodes == null || childNodes.isEmpty()) {
			noNodes = true;
		}
		if(noElements && noNodes) {
			return true;
		}
		return false;
	}

	static int depth(Element element, Element commonAncestor) {
		int depth = 0;
		Element el = element;
		while (el != commonAncestor && el != null) {
			depth++;
			el = el.parent();
		}
		return depth;
	}

	static boolean isLeaf(Node node) {
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
	
	static boolean isHeaderElement(Element el) {
		if(el == null)
			return false;
		String tagName = el.tagName();
		if(tagName == null)
			return false;
		return tagName.toLowerCase().startsWith("h");
	}
	
	private static boolean isHeaderElementH1H2(Element el) {
		if (el == null)
			return false;
		String tagName = el.tagName();
		if (tagName == null)
			return false;
		return tagName.equalsIgnoreCase("h1") || tagName.equalsIgnoreCase("h2");
	}
	
	static boolean isChildOfHeaderElement(Element el) {
		if(isHeaderElementH1H2(el)) {
			return true;
		}
		Iterator<Element> itr = el.parents().iterator();
		while(itr.hasNext()) {
			Element elAncestor = itr.next();
			if(isHeaderElementH1H2(elAncestor)) {
				return true;
			}
		}
		return false;
	}

	static Elements siblingElements(Element el) {
		Element parent = el.parent();
		if (parent == null)
			return new Elements();
		int size = parent.children().size();
		if (size == 0)
			return new Elements();
		return el.siblingElements();
	}

	static void removeStyleLinks(Document doc) {
		Elements elementsByTag = doc.getElementsByTag("style");
		if (elementsByTag != null && !elementsByTag.isEmpty()) {
			elementsByTag.remove();
		}
	}

	static void removeScripts(Document doc) {
		Elements elementsByTag = doc.getElementsByTag("script");
		if (elementsByTag != null && !elementsByTag.isEmpty()) {
			elementsByTag.remove();
		}
	}

	static void removeHeaders(Document doc) {
		Elements elementsByTag = doc.getElementsByTag("header");
		if (elementsByTag != null && !elementsByTag.isEmpty()) {
			elementsByTag.remove();
		}
	}

	static void removeFooters(Document doc) {
		Elements elementsByTag = doc.getElementsByTag("footer");
		if (elementsByTag != null && !elementsByTag.isEmpty()) {
			elementsByTag.remove();
		}
	}

	static void removeComments(Node node) {
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
	
	static String readOgUrl(Document doc) {
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

	static String readOgTilte(Document doc) {
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

	static String readOgDescription(Document doc) {
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
	
	static String readOgImage(Document doc) {
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

	static List<String> readKeywordsList(Document doc) {
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

	static Element findLowestCommonAncestor(Element el1, Element el2, Element treeBaseRoot) {
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
	
	static Element findLowestCommonAncestorStrict(Element el1, Element el2, Element treeBaseRoot) {
		Element p1 = el1;
		Element p2 = el2;
		if (p1 == null || p2 == null) {
			return null;
		}
		Element root = p1;
		StringBuilder str = new StringBuilder();
		while (p1 != null && p1 != treeBaseRoot) {
			root = p1;
			str.append(p1.tagName());
			p1 = p1.parent();
			if (p1 != null && p1 != treeBaseRoot) {
				str.append(",");
			}
		}
		String[] p1PathSequence = str.reverse().toString().split(",");
		str = new StringBuilder();
		while (p2 != null && p2 != treeBaseRoot) {
			str.append(p2.tagName());
			p2 = p2.parent();
			if (p2 != null && p2 != treeBaseRoot) {
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
				break;
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
		p1 = tmp;
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
	
	/*private WebElement findPrimaryElementByDescription_test(Document doc,
			String description) {
		if (description == null || description.trim().isEmpty())
			return null;

		description = description.replaceAll("\\s+", " ").replaceAll("\\xA0",
				" ");
		List<String> words = LanguageUtil.getWords(description);
		//int len = words.size();
		Iterator<String> wItr = words.iterator();
		while(wItr.hasNext()) {
			String w = wItr.next();
			if(STOP_WORDS.isStopWord(w)) {
				wItr.remove();
			}
		}
		String[] descriptionWordsArr = words.toArray(new String[words.size()]);

		String junk_detect_className = "squill_junk_detect";
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		String maxCountElTypeStyle = null;
		List<MarkedElement> markedElements = new LinkedList<MarkedElement>();
		Elements allElements = doc.body().getAllElements();
		Iterator<Element> itr = allElements.iterator();
		while (itr.hasNext()) {
			Element element = itr.next();
			if (WebDocumentUtil.isHeaderElement(element))
				continue;
			String text = element.text().replaceAll("\\s+", " ")
					.replaceAll("\\xA0", " ");
			$LOG.debug(text);
			words = LanguageUtil.getWords(text);
			wItr = words.iterator();
			while(wItr.hasNext()) {
				String w = wItr.next();
				if(STOP_WORDS.isStopWord(w)) {
					wItr.remove();
				}
			}
			String[] textWordsArr = words.toArray(new String[words.size()]);
			
			MatchRank matchRank = MatchRank.checkIntersection(descriptionWordsArr, textWordsArr);
			switch (matchRank) {
			case NO_MATCH:
				if(!markedElements.isEmpty() && isAChildNode(element, markedElements)) {
					element.addClass(junk_detect_className);
				}
				break;
			case HIGH:
			case MEDIUM:
				MarkedElement markedElement = new MarkedElement(element, WebDocumentUtil.depth(element, doc.body()));
				String elTypeStyle = markedElement.getClassName() + ":" + markedElement.getTagName();
				int c = 0;
				if(map.get(elTypeStyle) != null) {
					c = map.get(elTypeStyle);
				}
				c++;
				map.put(elTypeStyle, c);
				if(maxCountElTypeStyle == null) {
					maxCountElTypeStyle = elTypeStyle;
				} else if(map.get(maxCountElTypeStyle) != null){
					int c1 = map.get(maxCountElTypeStyle);
					if(c1 < c) {
						maxCountElTypeStyle = elTypeStyle;
					}
				}
				markedElements.add(markedElement);
				break;
			case LOW:
				break;
			}
		}

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
		
		int maxOccurenceCount = 0;
		Element maxOccurenceLCA = null;
		Map<Element, Integer> identityMap = new IdentityHashMap<Element, Integer>();
		for(int i=0; i<markedElements.size(); i++) {
			Element el1 = markedElements.get(i).getEl();
			for(int j=i+1; j<markedElements.size(); j++) {
				Element el2 = markedElements.get(j).getEl();
				Element _LCA = WebDocumentUtil.findLowestCommonAncestor(el1, el2, doc.body());
				int count = 0;
				if(identityMap.get(_LCA) != null) {
					count = identityMap.get(_LCA);
				}
				count++;
				identityMap.put(_LCA, count);
				if(count > maxOccurenceCount) {
					maxOccurenceCount = count;
				}
				maxOccurenceLCA = _LCA;
			}
		}
		
		/*MarkedElement elWithMinHtml = null;
		float ratio = -1;
		for(MarkedElement markedElement : markedElements) {
			float r1 = ((float) (markedElement.getEl().getAllElements().size()) / (float) (markedElement.getEl().text().length()));
			if(elWithMinHtml == null) {
				elWithMinHtml = markedElement;
				ratio = r1;
			} else if(r1 < ratio) {
				elWithMinHtml = markedElement;
				ratio = r1;
			}
		}
		
		elWithMinHtml.getEl().getElementsByClass(junk_detect_className).remove();*/
		/*
		maxOccurenceLCA.getElementsByClass(junk_detect_className).remove();
		MarkedElement primaryMarkedElement = new MarkedElement(maxOccurenceLCA,
				WebDocumentUtil.depth(maxOccurenceLCA, doc.body()));
		
		Iterator<Element> itrEl = identityMap.keySet().iterator();
		while(itrEl.hasNext()) {
			Element el = itrEl.next();
			if(el == maxOccurenceLCA)
				continue;
			if(el.parents().contains(maxOccurenceLCA) || maxOccurenceLCA.parents().contains(el))
				continue;
			el.remove();
		}

		return new WebElement().setPrimaryElement(primaryMarkedElement.getEl())
				.setPrimaryElementClassName(primaryMarkedElement.getClassName())
				.setPrimaryElementDepth(primaryMarkedElement.getDepth())
				.setTagName(primaryMarkedElement.getTagName());
	}*/
}
