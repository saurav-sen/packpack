package com.pack.pack.client.internal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.pack.pack.client.api.APIConstants;

/**
 * 
 * @author Saurav
 *
 */
public class HttpRequestExecutor {

	private HttpURLConnection prepareConnection(String url,
			String requestMethod, Map<String, String> headers, String cookies,
			boolean isResponseGZipEncoded, int readTimeout) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) new URL(url)
				.openConnection();
		connection.setRequestMethod(requestMethod);
		connection.setReadTimeout(readTimeout);

		HttpURLConnection.setFollowRedirects(true);

		if (headers != null) {
			Iterator<String> itr = headers.keySet().iterator();
			while (itr.hasNext()) {
				String headerName = itr.next();
				String headerValue = headers.get(headerName);
				if (headerValue != null) {
					connection.addRequestProperty(headerName, headerValue);
				}
			}
		}

		if (isResponseGZipEncoded) {
			connection.setRequestProperty(APIConstants.ACCEPT_ENCODING,
					APIConstants.GZIP);
		}
		connection.setRequestProperty(APIConstants.USER_AGENT,
				APIConstants.SQUILL_CLIENT_USER_AGENT);
		if (cookies != null) {
			connection.setRequestProperty(APIConstants.COOKIE, cookies);
		}
		return connection;
	}

	private String readResponsePayload(HttpURLConnection connection)
			throws Exception {
		try {
			StringBuilder payload = new StringBuilder();
			InputStreamReader inputStreamReader = null;
			if (APIConstants.GZIP.equalsIgnoreCase(connection
					.getContentEncoding())) {
				inputStreamReader = new InputStreamReader(new GZIPInputStream(
						connection.getInputStream()));
			} else {
				inputStreamReader = new InputStreamReader(
						connection.getInputStream());
			}
			BufferedReader contentReader = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = contentReader.readLine()) != null) {
				payload.append(line);
			}
			contentReader.close();
			return payload.toString();
		} catch (SocketTimeoutException e) {
			return null;
		}
	}

	public String GET(String url, Map<String, String> headers,
			boolean isResponseGZipEncoded) throws Exception {
		return GET(url, headers, isResponseGZipEncoded, 0);
	}

	public String GET(String url, Map<String, String> headers,
			boolean isResponseGZipEncoded, int readTimeout) throws Exception {
		HttpURLConnection GET = prepareConnection(url, "GET", headers, null,
				isResponseGZipEncoded, readTimeout);

		String cookies = null;
		String responsePayload = null;
		int status = GET.getResponseCode();
		while (status == HttpURLConnection.HTTP_MOVED_TEMP
				|| status == HttpURLConnection.HTTP_MOVED_PERM
				|| status == HttpURLConnection.HTTP_SEE_OTHER) {
			String locationUrl = GET.getHeaderField(APIConstants.LOCATION);
			String newCookies = GET.getHeaderField(APIConstants.SET_COOKIE);
			if (newCookies != null && !newCookies.trim().isEmpty()) {
				cookies = newCookies;
			}
			GET.disconnect();
			GET = prepareConnection(locationUrl, "GET", headers, cookies,
					isResponseGZipEncoded, readTimeout);
			status = GET.getResponseCode();
		}
		if (status == HttpURLConnection.HTTP_OK) {
			responsePayload = readResponsePayload(GET);
		}
		GET.disconnect();
		return responsePayload;
	}
	
	public InputStream GET_Resource(String url, Map<String, String> headers,
			boolean isResponseGZipEncoded) throws Exception {
		return GET_Resource(url, headers, isResponseGZipEncoded, 0);
	}

	public InputStream GET_Resource(String url, Map<String, String> headers,
			boolean isResponseGZipEncoded, int readTimeout) throws Exception {
		HttpURLConnection GET = prepareConnection(url, "GET", headers, null,
				isResponseGZipEncoded, readTimeout);

		String cookies = null;
		int status = GET.getResponseCode();
		while (status == HttpURLConnection.HTTP_MOVED_TEMP
				|| status == HttpURLConnection.HTTP_MOVED_PERM
				|| status == HttpURLConnection.HTTP_SEE_OTHER) {
			String locationUrl = GET.getHeaderField(APIConstants.LOCATION);
			String newCookies = GET.getHeaderField(APIConstants.SET_COOKIE);
			if (newCookies != null && !newCookies.trim().isEmpty()) {
				cookies = newCookies;
			}
			GET.disconnect();
			GET = prepareConnection(locationUrl, "GET", headers, cookies,
					isResponseGZipEncoded, readTimeout);
			status = GET.getResponseCode();
		}
		InputStream inputStream = null;
		if (status == HttpURLConnection.HTTP_OK) {
			if (APIConstants.GZIP.equalsIgnoreCase(GET
					.getContentEncoding())) {
				inputStream = new GZIPInputStream(GET.getInputStream());
			} else {
				inputStream = GET.getInputStream();
			}
		}
		return inputStream;
	}

	private void sendPOST(HttpURLConnection POST, String payload)
			throws Exception {
		POST.setUseCaches(false);
		POST.setDoInput(true);
		POST.setDoOutput(true);

		byte[] payloadInBytes = payload.getBytes("UTF-8");
		OutputStream os = POST.getOutputStream();
		os.write(payloadInBytes);
		os.close();
	}

	public String POST(String url, Map<String, String> headers, String payload,
			boolean isResponseGZipEncoded) throws Exception {
		return POST(url, headers, payload, isResponseGZipEncoded, 0);
	}

	public String POST(String url, Map<String, String> headers, String payload,
			boolean isResponseGZipEncoded, int readTimeout) throws Exception {
		HttpURLConnection POST = prepareConnection(url, "POST", headers, null,
				isResponseGZipEncoded, readTimeout);
		sendPOST(POST, payload);

		String cookies = null;
		String content = null;
		int status = POST.getResponseCode();
		while (status == HttpURLConnection.HTTP_MOVED_TEMP
				|| status == HttpURLConnection.HTTP_MOVED_PERM
				|| status == HttpURLConnection.HTTP_SEE_OTHER) {
			String locationUrl = POST.getHeaderField(APIConstants.LOCATION);
			String newCookies = POST.getHeaderField(APIConstants.SET_COOKIE);
			if (newCookies != null && !newCookies.trim().isEmpty()) {
				cookies = newCookies;
			}
			POST.disconnect();
			POST = prepareConnection(locationUrl, "POST", headers, cookies,
					isResponseGZipEncoded, readTimeout);
			sendPOST(POST, payload);
			status = POST.getResponseCode();
		}
		if (status == HttpURLConnection.HTTP_OK) {
			content = readResponsePayload(POST);
		}
		POST.disconnect();
		return content;
	}

}
