package com.pack.pack.client.internal;

/**
 * 
 * @author Saurav
 *
 */
class APIWrapper extends AbstractAPI {

	private BaseAPI api;

	APIWrapper(BaseAPI api) {
		this.api = api;
	}

	@Override
	public ApiInvoker getInvoker() {
		return api.getInvoker();
	}
}