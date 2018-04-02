package com.squill.og.crawler.hooks;

import com.squill.og.crawler.IWebSite;

public interface GenSession {

	public void addAttr(String key, Object value);

	public Object getAttr(String key);

	public IWebSite getCurrentWebSite();
}
