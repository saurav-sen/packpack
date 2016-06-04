package com.pack.pack.client.api;

/**
 * 
 * @author Saurav
 *
 */
public interface API {
	
	public Object execute() throws Exception;
	
	public Object execute(MultipartRequestProgressListener listener) throws Exception;
}
