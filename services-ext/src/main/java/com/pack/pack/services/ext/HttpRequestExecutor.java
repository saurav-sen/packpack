package com.pack.pack.services.ext;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author Saurav
 *
 */
public class HttpRequestExecutor {

	private HttpClient newHttpClient() throws KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException {
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
		
		/*BasicCookieStore cookieStore = new BasicCookieStore();
	    BasicClientCookie cookie = new BasicClientCookie("", "");
	    cookie.setDomain("timesofindia.indiatimes.com");
	    cookie.setPath("/");
	    cookieStore.addCookie(cookie);
	    
		httpClientBuilder.setDefaultCookieStore(cookieStore);*/

		HttpClient httpClient = httpClientBuilder.build();
		return httpClient;
	}

	public HttpResponse GET(HttpGet GET) throws ClientProtocolException,
			IOException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		HttpClient client = newHttpClient();
		
        //GET.setHeader("Referer", "");
		String USER_AGENT = CoreConstants.SQUILL_ROBOT_USER_AGENT_STRING;
		if(new java.net.URL(GET.getRequestLine().getUri()).getHost().endsWith("theprint.in")) {
			USER_AGENT = "mozilla/5.0";
		}
        GET.setHeader("User-Agent", USER_AGENT);
        
		return client.execute(GET);
	}
	
	public String GET(String link) throws Exception {
		HttpClient client = newHttpClient();
		Builder configBuilder = RequestConfig.custom();
		configBuilder.setConnectionRequestTimeout(200000);
		configBuilder.setConnectTimeout(200000);
		configBuilder.setSocketTimeout(200000);
		/*HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 200000);
		HttpConnectionParams.setSoTimeout(params, 200000);*/
		HttpGet GET = new HttpGet(link);
		/*if (info != null) {
			String lastModifiedSince = info.getLastModifiedSince();
			if (lastModifiedSince != null
					&& !lastModifiedSince.trim().isEmpty()) {
				GET.addHeader(
						CoreConstants.IF_MODIFIED_SINCE_CACHE_CONTROL_HEADER,
						lastModifiedSince);
			}
		}*/
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
			/*if (info != null) {
				Header lastModified = response
						.getLastHeader(CoreConstants.LAST_MODIFIED_CACHE_CONTROL_HEADER);
				if (lastModified != null) {
					info.setLastModifiedSince(lastModified.getValue());
				}
			}*/
			return EntityUtils.toString(response.getEntity());
		}
		return null;
	}
}
