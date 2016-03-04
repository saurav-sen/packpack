package com.pack.pack.security.util;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 * 
 * @author Saurav
 *
 */
public class OAuth1Util {

	public static MultivaluedMap<String, String> getImmutableMap(
			final Map<String, List<String>> map) {
		final MultivaluedHashMap<String, String> newMap = new MultivaluedHashMap<String, String>();
		for (final Map.Entry<String, List<String>> entry : map.entrySet()) {
			newMap.put(entry.getKey(), entry.getValue());
		}
		return newMap;
	}
}