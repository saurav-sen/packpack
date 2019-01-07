package com.pack.pack.services.ext.text.summerize;

import org.jsoup.nodes.Element;

/**
 * 
 * @author Saurav
 *
 */
class MarkedElement {

	private Element el;

	private int depth;

	MarkedElement(Element el, int depth) {
		this.el = el;
		this.depth = depth;
	}

	Element getEl() {
		return el;
	}

	int getDepth() {
		return depth;
	}
}