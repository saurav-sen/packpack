package com.pack.pack.client.api;

import static com.pack.pack.common.util.CommonConstants.PAGELINK_DIRECTION_POSITIVE;

/**
 * 
 * @author Saurav
 *
 */
public final class PageUtil {

	private PageUtil() {
	}

	public static String buildNextPageLink(long lastReceiveTimestamp) {
		return lastReceiveTimestamp + PAGELINK_DIRECTION_POSITIVE;
	}
}
