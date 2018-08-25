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
import org.apache.http.ssl.SSLContextBuilder;

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

		HttpClient httpClient = httpClientBuilder.build();
		return httpClient;
	}

	public HttpResponse GET(HttpGet GET) throws ClientProtocolException,
			IOException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		HttpClient client = newHttpClient();
		return client.execute(GET);
	}
}
