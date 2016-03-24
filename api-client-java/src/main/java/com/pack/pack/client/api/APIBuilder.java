package com.pack.pack.client.api;

import java.util.HashMap;
import java.util.Map;

import com.pack.pack.client.internal.APIWrapper;
import com.pack.pack.client.internal.AttachmentsApi;
import com.pack.pack.client.internal.BrandsApi;
import com.pack.pack.client.internal.Configuration;
import com.pack.pack.client.internal.EGiftApi;
import com.pack.pack.client.internal.PackApi;
import com.pack.pack.client.internal.TopicApi;
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
		if (action == COMMAND.GET_USER_BY_ID
				|| action == COMMAND.GET_USER_BY_USERNAME
				|| action == COMMAND.SEARCH_USER_BY_NAME
				|| action == COMMAND.SIGN_IN || action == COMMAND.SIGN_OUT
				|| action == COMMAND.SIGN_UP) {
			api = new APIWrapper(new UserManagementApi());
			api.getInvoker().setConfiguration(config);
		} else if (action == COMMAND.GET_USER_FOLLOWED_TOPIC_LIST
				|| action == COMMAND.FOLLOW_TOPIC
				|| action == COMMAND.NEGLECT_TOPIC
				|| action == COMMAND.GET_TOPIC_BY_ID
				|| action == COMMAND.CREATE_NEW_TOPIC) {
			api = new APIWrapper(new TopicApi());
			api.getInvoker().setConfiguration(config);
		} else if (action == COMMAND.GET_PACK_BY_ID
				|| action == COMMAND.GET_ALL_PACKS_IN_DEFAULT_TOPICS
				|| action == COMMAND.GET_ALL_PACKS_IN_TOPIC
				|| action == COMMAND.FORWARD_PACK
				|| action == COMMAND.FORWARD_PACK_OVER_EMAIL
				|| action == COMMAND.ADD_COMMENT
				|| action == COMMAND.ADD_LIKE_TO_PACK) {
			api = new APIWrapper(new PackApi());
			api.getInvoker().setConfiguration(config);
		} else if (action == COMMAND.GET_EGIFT_BY_ID
				|| action == COMMAND.GET_EGIFTS_BY_BRAND_ID
				|| action == COMMAND.GET_EGIFTS_BY_CATEGORY
				|| action == COMMAND.FORWARD_EGIFT
				|| action == COMMAND.FORWARD_PACK_OVER_EMAIL
				|| action == COMMAND.ADD_COMMENT
				|| action == COMMAND.ADD_LIKE_TO_PACK) {
			api = new APIWrapper(new EGiftApi());
			api.getInvoker().setConfiguration(config);
		} else if (action == COMMAND.SEARCH_BRANDS_INFO) {
			api = new APIWrapper(new BrandsApi());
			api.getInvoker().setConfiguration(config);
		} else if(action == COMMAND.GET_PROFILE_PICTURE
				|| action == COMMAND.GET_THUMBNAIL_IMAGE_ATTACHMENT
				|| action == COMMAND.GET_ORIGINAL_IMAGE_ATTACHMENT
				|| action == COMMAND.GET_THUMBNAIL_VIDEO_ATTACHMENT
				|| action == COMMAND.GET_ORIGINAL_VIDEO_ATTACHMENT
				|| action == COMMAND.UPLOAD_IMAGE_PACK
				|| action == COMMAND.ADD_IMAGE_TO_PACK
				|| action == COMMAND.UPLOAD_VIDEO_PACK
				|| action == COMMAND.ADD_VIDEO_TO_PACK) {
			api = new APIWrapper(new AttachmentsApi());
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