package com.squill.og.crawler.internal.utils;

import com.pack.pack.services.ext.CoreConstants;
import com.squill.crawlercommons.fetcher.http.UserAgent;

/**
 *
 * @author Saurav
 * @since 13-Mar-2015
 *
 */
public interface CoreConstants2 extends CoreConstants {

	public static final UserAgent SQUILL_ROBOT = new UserAgent("SquillBot", //$NON-NLS-1$
			"squill@gmail.com", "http://www.squill.co.in"); //$NON-NLS-1$ //$NON-NLS-2$
}