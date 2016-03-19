package com.pack.pack.client.api;

import java.util.HashMap;
import java.util.Map;

import com.pack.pack.client.internal.APIWrapper;
import com.pack.pack.client.internal.Configuration;
import com.pack.pack.client.internal.UserManagementApi;

/**
 * 
 * @author Saurav
 *
 */
public class APIBuilder {
	
	private ConfigurationImpl config;
	
	private APIBuilder() {
		config = new ConfigurationImpl();
	}
	
	public static APIBuilder create() {
		return new APIBuilder();
	}
	
	public APIBuilder setAction(COMMAND action) {
		config.action = action;
		return this;
	}
	
	public APIBuilder addApiParam(String key, Object value) {
		config.params.put(key, value);
		return this;
	}
	
	public APIBuilder setOauthToken(String oAuthToken) {
		config.oAuthToken = oAuthToken;
		return this;
	}
	
	public API build() {
		APIWrapper api = null;
		COMMAND action = config.action;
		if(action == COMMAND.GET_USER_BY_ID || action == COMMAND.GET_USER_BY_USERNAME 
			|| action == COMMAND.SEARCH_USER_BY_NAME || action == COMMAND.SIGN_IN 
			|| action == COMMAND.SIGN_OUT || action == COMMAND.SIGN_UP) {
			api = new APIWrapper(new UserManagementApi());
			api.getInvoker().setConfiguration(config);
		}
		return api;
	}
	
	private class ConfigurationImpl implements Configuration {
		
		private COMMAND action;
		
		private Map<String, Object> params = new HashMap<String, Object>();
		
		private String oAuthToken;

		@Override
		public COMMAND getAction() {
			return action;
		}

		@Override
		public String getOAuthToken() {
			return oAuthToken;
		}

		@Override
		public Map<String, Object> getApiParams() {
			return params;
		}
		
	}
}