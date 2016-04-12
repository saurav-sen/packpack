package com.pack.pack.oauth1.client.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.pack.pack.oauth1.client.internal.HmacSha1Method;
import com.pack.pack.oauth1.client.internal.OAuth1Secrets;
import com.pack.pack.oauth1.client.internal.UriComponent;

public class OAuth1Signature {

	public String generate(final OAuth1Request request,
			final OAuth1Parameters params, OAuth1Secrets secrets) {
		return HmacSha1Method.INSTANCE.sign(baseString(request, params),
				secrets);
	}

	private String baseString(final OAuth1Request oauth1Request,
			final OAuth1Parameters params) {
		// HTTP request method
		final StringBuilder builder = new StringBuilder(oauth1Request
				.getRequestMethod().toUpperCase());

		// request URL, see section 3.4.1.2
		// http://tools.ietf.org/html/draft-hammer-oauth-10#section-3.4.1.2
		builder.append('&').append(
				UriComponent.encode(constructRequestURL(oauth1Request.getUrl())
						.toASCIIString(), UriComponent.Type.UNRESERVED));

		// normalized request parameters, see section 3.4.1.3.2
		// http://tools.ietf.org/html/draft-hammer-oauth-10#section-3.4.1.3.2
		builder.append('&').append(
				UriComponent.encode(normalizeParameters(oauth1Request, params),
						UriComponent.Type.UNRESERVED));

		return builder.toString();
	}

	private URI constructRequestURL(URL url) {
		try {
			final StringBuilder builder = new StringBuilder(url.getProtocol())
					.append("://").append(url.getHost().toLowerCase());
			final int port = url.getPort();
			if (port > 0 && port != url.getDefaultPort()) {
				builder.append(':').append(port);
			}
			builder.append(url.getPath());
			return new URI(builder.toString());

		} catch (final URISyntaxException mue) {
			// throw new OAuth1SignatureException(mue);
		}
		return null;
	}

	static String normalizeParameters(final OAuth1Request request,
			final OAuth1Parameters params) {

		final ArrayList<String[]> list = new ArrayList<String[]>();

		// parameters in the OAuth HTTP authorization header
		for (final String key : params.keySet()) {

			/*
			 * // exclude realm and oauth_signature parameters from OAuth HTTP
			 * authorization header if (key.equals(OAuth1Parameters.REALM) ||
			 * key.equals(OAuth1Parameters.SIGNATURE)) { continue; }
			 */

			final String value = params.get(key);

			// Encode key and values as per section 3.6
			// http://tools.ietf.org/html/draft-hammer-oauth-10#section-3.6
			if (value != null) {
				addParam(key, value, list);
			}
		}

		// parameters in the HTTP POST request body and HTTP GET parameters in
		// the query part
		for (final String key : request.getParameterNames()) {

			// ignore parameter if an OAuth-specific parameter that appears in
			// the OAuth parameters
			if (key.startsWith("oauth_") && params.containsKey(key)) {
				continue;
			}

			// the same parameter name can have multiple values
			final List<String> values = request.getParameterValues(key);

			// Encode key and values as per section 3.6
			// http://tools.ietf.org/html/draft-hammer-oauth-10#section-3.6
			if (values != null) {
				for (final String value : values) {
					addParam(key, value, list);
				}
			}
		}

		// sort name-value pairs by name
		Collections.sort(list, new Comparator<String[]>() {
			@Override
			public int compare(final String[] t, final String[] t1) {
				final int c = t[0].compareTo(t1[0]);
				return c == 0 ? t[1].compareTo(t1[1]) : c;
			}
		});

		final StringBuilder buf = new StringBuilder();

		// append each name-value pair, delimited with ampersand
		for (final Iterator<String[]> i = list.iterator(); i.hasNext();) {
			final String[] param = i.next();
			buf.append(param[0]).append("=").append(param[1]);
			if (i.hasNext()) {
				buf.append('&');
			}
		}

		return buf.toString();
	}

	private static void addParam(final String key, final String value,
			final List<String[]> list) {
		list.add(new String[] {
				UriComponent.encode(key, UriComponent.Type.UNRESERVED),
				value == null ? "" : UriComponent.encode(value,
						UriComponent.Type.UNRESERVED) });
	}
}