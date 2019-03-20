package com.squill.og.crawler.linkfilters;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.utils.DateTimeUtil;

/**
 * 
 * @author Saurav
 *
 */
@Component("qzLinkFilter")
@Scope("prototype")
public class QZLinkFilter extends DailyFixedSizeLinkFilter {
	
	private static final Logger LOG = LoggerFactory.getLogger(QZLinkFilter.class);
	
	public QZLinkFilter() {
		super(2);
	}
	
	@Override
	protected String todayKeySuffix() {
		return "qz_dot_com";
	}
	
	@Override
	public boolean isScopedSitemapUrl(String sitemapUrl) {
		try {
			URL url = new URL(sitemapUrl);
			if(url.getPath().equals("/news-sitemap.xml"))
				return false;
			if(url.getPath().equals("/sitemap.xml")) {
				String query = url.getQuery();
				if(query == null || query.trim().isEmpty())
					return true;
				String[] splits = query.split("&");
				int yyyy = -1;
				int mm = -1;
				int dd = -1;
				for(String split : splits) {
					String[] arr = split.split("=");
					if(arr.length > 1) {
						if("yyyy".equals(arr[0])) {
							yyyy = Integer.parseInt(arr[1]);
						} else if("mm".equals(arr[0])) {
							mm = Integer.parseInt(arr[1]);
						} else if("dd".equals(arr[0])) {
							dd = Integer.parseInt(arr[1]);
						}
					}
				}
				
				if(yyyy == -1 || mm == -1 || dd == -1) {
					LOG.error("Can't decode necessary decission parameters from sitemap URL @ " + sitemapUrl);
					return false;
				}
				
				String date = toDateString(dd, mm, yyyy);
				if(today().equals(date) || yesterday().equals(date)) {
					return true;
				}
				return false;
			}
			return false;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
	}
	
	protected String yesterday() {
		return DateTimeUtil.yesterday();
	}
}
