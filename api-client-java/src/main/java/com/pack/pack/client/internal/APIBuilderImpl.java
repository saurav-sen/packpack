package com.pack.pack.client.internal;

import java.util.HashMap;
import java.util.Map;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.COMMAND;

/**
 * 
 * @author Saurav
 *
 */
public class APIBuilderImpl extends APIBuilder {

	private ConfigurationImpl config;
	
	private String baseUrl;

	public APIBuilderImpl(String baseUrl) {
		config = new ConfigurationImpl();
		this.baseUrl = baseUrl;
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
		if (action == COMMAND.GET_USER_BY_ID
				|| action == COMMAND.GET_USER_BY_USERNAME
				|| action == COMMAND.SEARCH_USER_BY_NAME
				|| action == COMMAND.EDIT_USER_CATEGORIES
				|| action == COMMAND.GET_USER_CATEGORIES
				|| action == COMMAND.SIGN_IN 
				|| action == COMMAND.SIGN_OUT
				|| action == COMMAND.SIGN_UP
				|| action == COMMAND.UPLOAD_USER_PROFILE_PICTURE
				|| action == COMMAND.UPDATE_USER_SETTINGS
				|| action == COMMAND.ISSUE_PASSWD_RESET_LINK
				|| action == COMMAND.RESET_USER_PASSWD
				|| action == COMMAND.ISSUE_SIGNUP_VERIFIER) {
			api = new APIWrapper(new UserManagementApi(baseUrl));
			api.getInvoker().setConfiguration(config);
		} else if (action == COMMAND.GET_PROFILE_PICTURE) {
			api = new APIWrapper(new AttachmentsApi(baseUrl));
			api.getInvoker().setConfiguration(config);
		} else if (action == COMMAND.LOAD_RESOURCE
				|| action == COMMAND.LOAD_EXTERNAL_RESOURCE) {
			api = new APIWrapper(new ResourceLoaderApi(baseUrl));
			api.getInvoker().setConfiguration(config);
		} else if(action == COMMAND.GET_ALL_PROMOTIONAL_FEEDS
				|| action == COMMAND.GET_ALL_NEWS_FEEDS
				|| action == COMMAND.GET_ALL_SPORTS_NEWS_FEEDS
				|| action == COMMAND.GET_ALL_SCIENCE_AND_TECHNOLOGY_NEWS_FEEDS) {
			api = new APIWrapper(new BroadcastApi(baseUrl));
			api.getInvoker().setConfiguration(config);
		} else if(action == COMMAND.SYNC_TIME
				|| action == COMMAND.VALIDATE_USER_NAME
				|| action == COMMAND.ANDROID_APK_URL) {
			api = new APIWrapper(new PublicApi(baseUrl));
			api.getInvoker().setConfiguration(config);
		} else if(action == COMMAND.CRAWL_FEED) {
			api = new APIWrapper(new FeedReaderApi(baseUrl));
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