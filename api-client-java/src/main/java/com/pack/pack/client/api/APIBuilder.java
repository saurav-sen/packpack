package com.pack.pack.client.api;

import com.pack.pack.client.internal.APIBuilderImpl;

/**
 * 
 * @author Saurav
 *
 */
public class APIBuilder {	

	public static APIBuilder create(String baseUrl) {
		return new APIBuilderImpl(baseUrl);
	}

	public APIBuilder setAction(COMMAND action) {
		throw new UnsupportedOperationException();
	}

	public APIBuilder addApiParam(String key, Object value) {
		throw new UnsupportedOperationException();
	}

	public APIBuilder setUserName(String userName) {
		throw new UnsupportedOperationException();
	}

	public API build() {
		throw new UnsupportedOperationException();
	}
}