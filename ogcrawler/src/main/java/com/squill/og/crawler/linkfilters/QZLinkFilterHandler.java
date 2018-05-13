package com.squill.og.crawler.linkfilters;

import java.net.URL;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.squill.og.crawler.hooks.ILinkFilter;

@Component("qzLinkFilterHandler")
@Scope("prototype")
public class QZLinkFilterHandler implements ILinkFilter {
	
	private static final Logger LOG = LoggerFactory.getLogger(QZLinkFilterHandler.class);

	@Override
	public boolean isScoped(String linkUrl) {
		return true;
	}
	
	@Override
	public boolean isScrapHtmlPageLinks() {
		return false;
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

	private String today() {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		String dd = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
		String mm = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
		String yyyy = String.valueOf(year);
		return dd + "/" + mm + "/" + yyyy;
	}
	
	private String yesterday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		String dd = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
		String mm = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
		String yyyy = String.valueOf(year);
		return dd + "/" + mm + "/" + yyyy;
	}
	
	private String toDateString(int dd, int mm, int yyyy) {
		StringBuilder date = new StringBuilder();
		if(dd < 10) {
			date.append("0");
		}
		date.append(String.valueOf(dd));
		date.append("/");
		if(mm < 10) {
			date.append("0");
		}
		date.append(String.valueOf(mm));
		date.append("/");
		date.append(String.valueOf(yyyy));
		return date.toString();
	}
}
