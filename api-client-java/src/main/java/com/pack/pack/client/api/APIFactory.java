package com.pack.pack.client.api;

import com.pack.pack.client.internal.LoginApi;

public class APIFactory {
	
	public static final APIFactory INSTANCE = new APIFactory();

	private APIFactory() {
	}
	
	public API initAPI(APIType type) {
		API api = null;
		switch(type) {
		case LOGIN_API:
			api = new LoginApi();
			break;
		default:
			break;
		}
		return api;
	}
}