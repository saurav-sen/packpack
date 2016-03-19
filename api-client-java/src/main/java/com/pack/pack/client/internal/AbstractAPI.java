package com.pack.pack.client.internal;

import com.pack.pack.client.api.API;

/**
 * 
 * @author Saurav
 *
 */
public abstract class AbstractAPI implements API {

	protected abstract ApiInvoker getInvoker();
	
	@Override
	public Object execute() throws Exception {
		return getInvoker().invoke();
	}
}
