package com.pack.pack.services.ext.text.summerize;

import org.jsoup.nodes.Element;

/**
 * 
 * @author Saurav
 *
 */
class WebElement {

	private String tagName;

	private Element primaryElement = null;

	private String primaryElementClassName = null;

	private int primaryElementDepth = -1;
	
	private Element _LCA;

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

	public WebElement setPrimaryElementClassName(String primaryElementClassName) {
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

	public Element get_LCA() {
		return _LCA;
	}

	public WebElement set_LCA(Element _LCA) {
		this._LCA = _LCA;
		return this;
	}
}