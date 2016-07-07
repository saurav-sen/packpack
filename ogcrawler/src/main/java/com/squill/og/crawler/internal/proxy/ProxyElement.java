package com.squill.og.crawler.internal.proxy;

import org.jsoup.nodes.Element;

public class ProxyElement {
		private Element element;
		
		public Object innerHtml;
		
		public ProxyElement(Element element) {
			this.element = element;
			innerHtml = element.html();
		}
	}