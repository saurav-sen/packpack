package com.squill.og.crawler.model;

import java.util.ArrayList;
import java.util.List;

public class DocumentHeadersMemento {

	private List<DocumentHeader> headers;

	public List<DocumentHeader> getHeaders() {
		if(headers == null) {
			return new ArrayList<DocumentHeader>(1);
		}
		return headers;
	}
	
	public void setHeaders(List<DocumentHeader> headers) {
		this.headers = headers;
	}
}
