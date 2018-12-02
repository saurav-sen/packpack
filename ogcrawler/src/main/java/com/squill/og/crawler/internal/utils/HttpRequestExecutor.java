package com.squill.og.crawler.internal.utils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import com.squill.og.crawler.model.WebSpiderTracker;

/**
 * 
 * @author Saurav
 *
 */
public class HttpRequestExecutor {

	private HttpClient newClient() {
		try {
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
			httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy());

			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
					new TrustStrategy() {
						public boolean isTrusted(X509Certificate[] arg0, String arg1)
								throws CertificateException {
							return true;
						}
					}).build();
			httpClientBuilder.setSSLContext(sslContext);

			// HostnameVerifier hostnameVerifier =
			// SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

			SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
					sslContext, NoopHostnameVerifier.INSTANCE);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http",
							PlainConnectionSocketFactory.getSocketFactory())
					.register("https", sslSocketFactory).build();

			PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			httpClientBuilder.setConnectionManager(connMgr);

			HttpClient httpClient = httpClientBuilder.build();
			return httpClient;
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*@SuppressWarnings("deprecation")
	private DecompressingHttpClient newDecompressingHttpClient() {
		DecompressingHttpClient client = new DecompressingHttpClient(new DefaultHttpClient());
		SSLSocketFactory socketFactory = (SSLSocketFactory) client
				.getConnectionManager().getSchemeRegistry().get("https")
				.getSchemeSocketFactory();
		socketFactory
				.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		return client;
	}*/
	
	public HttpResponse GET(HttpGet GET, boolean needDecompression)
			throws ClientProtocolException, IOException {
		HttpClient client = newClient();
		/*if(needDecompression) {
			client = newDecompressingHttpClient();
		}*/
		return client.execute(GET);
	}

	public HttpResponse GET(HttpGet GET)
			throws ClientProtocolException, IOException {
		return GET(GET, false);
	}
	
	public HttpResponse GET(HttpGet GET, HttpContext HTTP_CONTEXT)
			throws ClientProtocolException, IOException {
		HttpClient client = newClient();
		return client.execute(GET, HTTP_CONTEXT);
	}

	public String GET0(String link, String domainName) throws ParseException,
			IOException {
		HttpClient client = newClient();
		HttpGet get = new HttpGet(link);
		HttpResponse response = client.execute(get);
		int responseCode = response.getStatusLine().getStatusCode();
		while (responseCode == 302 || responseCode == 303) {
			CookieStore cookieStore = new BasicCookieStore();
			String suffix = response.getFirstHeader(
					CoreConstants2.LOCATION_HTTP_HEADER).getValue();
			/*
			 * get = new HttpGet(CoreConstants.HTTP + domainName + "/" + part +
			 * "/" + suffix);
			 */
			get = new HttpGet(CoreConstants2.HTTP + domainName + "/" + suffix);
			String cookies = response.getFirstHeader(CoreConstants2.SET_COOKIE)
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
	
	public String GET(String link) throws Exception {
		return GET(link, null);
	}

	public String GET(String link, WebSpiderTracker info) throws Exception {
		HttpClient client = newClient();
		Builder configBuilder = RequestConfig.custom();
		configBuilder.setConnectionRequestTimeout(200000);
		configBuilder.setConnectTimeout(200000);
		configBuilder.setSocketTimeout(200000);
		/*HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 200000);
		HttpConnectionParams.setSoTimeout(params, 200000);*/
		HttpGet GET = new HttpGet(link);
		if (info != null) {
			String lastModifiedSince = info.getLastModifiedSince();
			if (lastModifiedSince != null
					&& !lastModifiedSince.trim().isEmpty()) {
				GET.addHeader(
						CoreConstants2.IF_MODIFIED_SINCE_CACHE_CONTROL_HEADER,
						lastModifiedSince);
			}
		}
		HttpContext HTTP_CONTEXT = new BasicHttpContext();
		HTTP_CONTEXT.setAttribute(CoreProtocolPNames.USER_AGENT,
				CoreConstants2.SQUILL_ROBOT_USER_AGENT_STRING);
		HttpResponse response = client.execute(GET, HTTP_CONTEXT);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 304) {
			// Not Modified based upon "If-Modified-Since" header flag set in
			// the request.
			return CoreConstants2.SKIP;
		} else if (statusCode == 200) {
			if (info != null) {
				Header lastModified = response
						.getLastHeader(CoreConstants2.LAST_MODIFIED_CACHE_CONTROL_HEADER);
				if (lastModified != null) {
					info.setLastModifiedSince(lastModified.getValue());
				}
			}
			return EntityUtils.toString(response.getEntity());
		}
		return null;
	}
}