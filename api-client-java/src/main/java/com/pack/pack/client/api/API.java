package com.pack.pack.client.api;

import java.util.Map;

public interface API {
	
	public static interface Login {
		public static final String CLIENT_KEY = "clientKey";
		public static final String CLIENT_SECRET = "clientSecret";
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
	}

	public <T> T execute(Class<T> clazz, COMMAND action, Map<String, Object> params) throws Exception;
}
