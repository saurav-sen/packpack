package com.squill.og.crawler.internal.proxy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ProxyDocument {
		private Document document;
		
		public Map<String, Element> forms = new HashMap<String, Element>();
		
		public ProxyDocument(Document document) {
			this.document = document;
			Elements formsArr = document.getElementsByTag("form");
			if(formsArr != null) {
				Iterator<Element> itr = formsArr.iterator();
				while(itr.hasNext()) {
					Element el = itr.next();
					String id = el.attr("id");
					forms.put(id, el);
				}
			}
		}
		
		public ProxyElement getElementById(String id) {
			Element element = document.select("#" + id).get(0);
			return new ProxyElement(element);
		}
	}