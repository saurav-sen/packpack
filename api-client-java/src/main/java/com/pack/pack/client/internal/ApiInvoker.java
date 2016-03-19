package com.pack.pack.client.internal;

/**
 * 
 * @author Saurav
 *
 */
public interface ApiInvoker {

	public void setConfiguration(Configuration configuration);
	
	public Object invoke() throws Exception;
}
