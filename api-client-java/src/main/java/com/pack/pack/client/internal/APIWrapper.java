package com.pack.pack.client.internal;

/**
 * 
 * @author Saurav
 *
 */
public class APIWrapper extends AbstractAPI {

	private AbstractAPI api;

	public APIWrapper(AbstractAPI api) {
		this.api = api;
	}

	@Override
	public ApiInvoker getInvoker() {
		return api.getInvoker();
	}
}