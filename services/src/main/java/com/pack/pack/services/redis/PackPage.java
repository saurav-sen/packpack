package com.pack.pack.services.redis;

import java.util.LinkedList;
import java.util.List;

import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.Pagination;

public class PackPage {

	private String previousLink;
	private String nextLink;
	
	private List<JPack> packs;

	public String getPreviousLink() {
		return previousLink;
	}

	public void setPreviousLink(String previousLink) {
		this.previousLink = previousLink;
	}

	public String getNextLink() {
		return nextLink;
	}

	public void setNextLink(String nextLink) {
		this.nextLink = nextLink;
	}

	public List<JPack> getPacks() {
		if(packs == null) {
			packs = new LinkedList<JPack>();
		}
		return packs;
	}

	public void setPacks(List<JPack> packs) {
		this.packs = packs;
	}
	
	public Pagination<JPack> convert() {
		Pagination<JPack> page = new Pagination<JPack>();
		page.setNextLink(nextLink);
		page.setPreviousLink(previousLink);
		page.setResult(getPacks());
		return page;
	}
	
	public static PackPage build(Pagination<JPack> page) {
		PackPage r = new PackPage();
		r.setNextLink(page.getNextLink());
		r.setPreviousLink(page.getPreviousLink());
		r.setPacks(page.getResult());
		return r;
	}
}
