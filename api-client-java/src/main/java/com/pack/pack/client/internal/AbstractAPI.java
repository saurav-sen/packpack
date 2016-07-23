package com.pack.pack.client.internal;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.MultipartRequestProgressListener;

/**
 * 
 * @author Saurav
 *
 */
abstract class AbstractAPI implements API {

	protected abstract ApiInvoker getInvoker();
	
	@Override
	public Object execute() throws Exception {
		return getInvoker().invoke();
	}
	
	@Override
	public Object execute(MultipartRequestProgressListener listener)
			throws Exception {
		return getInvoker().invoke(listener);
	}
}