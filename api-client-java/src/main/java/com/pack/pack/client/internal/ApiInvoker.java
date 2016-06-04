package com.pack.pack.client.internal;

import com.pack.pack.client.api.MultipartRequestProgressListener;

/**
 * 
 * @author Saurav
 *
 */
public interface ApiInvoker {

	public void setConfiguration(Configuration configuration);
	
	public Object invoke() throws Exception;
	
	public Object invoke(MultipartRequestProgressListener listener) throws Exception;
}
