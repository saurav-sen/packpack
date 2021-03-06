package com.squill.og.crawler.internal.utils;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.squill.og.crawler.model.WebSpiderTracker;

/**
 * 
 * @author Saurav
 *
 */
public class HttpRequestExecutor {

	public String GET(String link, String domainName) throws ParseException,
			IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(link);
		HttpResponse response = client.execute(get);
		int responseCode = response.getStatusLine().getStatusCode();
		while (responseCode == 302 || responseCode == 303) {
			CookieStore cookieStore = new BasicCookieStore();
			String suffix = response.getFirstHeader(
					CoreConstants.LOCATION_HTTP_HEADER).getValue();
			/*
			 * get = new HttpGet(CoreConstants.HTTP + domainName + "/" + part +
			 * "/" + suffix);
			 */
			get = new HttpGet(CoreConstants.HTTP + domainName + "/" + suffix);
			String cookies = response.getFirstHeader(CoreConstants.SET_COOKIE)
					.getValue();
			String[] split = cookies.split(";");

			String path = "/";
			for (String s : split) {
				String[] split2 = s.split("=");
				String key = split2[0].trim();
				String value = "";
				if (split2.length > 1) {
					value = split2[1].trim();
				}

				if (key.equalsIgnoreCase("path"))
					path = value;
				else {
					BasicClientCookie cookie = new BasicClientCookie(key, value);
					cookieStore.addCookie(cookie);
				}
			}
			List<Cookie> cookies2 = cookieStore.getCookies();
			for (Cookie c : cookies2) {
				if (c instanceof BasicClientCookie) {
					((BasicClientCookie) c).setPath(path);
					((BasicClientCookie) c).setDomain(domainName);
				}
			}

			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			response = new DefaultHttpClient().execute(get, localContext);
			responseCode = response.getStatusLine().getStatusCode();
		}
		return ResponseUtil.getResponseBodyContent(response);
	}

	public String GET(String link, WebSpiderTracker info) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 200000);
		HttpConnectionParams.setSoTimeout(params, 200000);
		HttpGet GET = new HttpGet(link);
		if (info != null) {
			String lastModifiedSince = info.getLastModifiedSince();
			if (lastModifiedSince != null
					&& !lastModifiedSince.trim().isEmpty()) {
				GET.addHeader(
						CoreConstants.IF_MODIFIED_SINCE_CACHE_CONTROL_HEADER,
						lastModifiedSince);
			}
		}
		HttpContext HTTP_CONTEXT = new BasicHttpContext();
		HTTP_CONTEXT.setAttribute(CoreProtocolPNames.USER_AGENT,
				CoreConstants.SQUILL_ROBOT_USER_AGENT_STRING);
		HttpResponse response = client.execute(GET, HTTP_CONTEXT);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 304) {
			// Not Modified based upon "If-Modified-Since" header flag set in
			// the request.
			return CoreConstants.SKIP;
		} else if (statusCode == 200) {
			if (info != null) {
				Header lastModified = response
						.getLastHeader(CoreConstants.LAST_MODIFIED_CACHE_CONTROL_HEADER);
				if (lastModified != null) {
					info.setLastModifiedSince(lastModified.getValue());
				}
			}
			return EntityUtils.toString(response.getEntity());
		}
		return null;
	}
}