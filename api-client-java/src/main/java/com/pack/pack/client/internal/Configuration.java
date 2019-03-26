package com.pack.pack.client.internal;

import java.util.Map;

import com.pack.pack.client.api.COMMAND;

/**
 * 
 * @author Saurav
 *
 */
public interface Configuration {

	public COMMAND getAction();
	
	public String getUserName();
	
	public String getDeviceId();
	
	public Map<String, Object> getApiParams();
}
