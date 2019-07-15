package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;

class ResourceLoaderApi extends BaseAPI {

	ResourceLoaderApi(String baseUrl) {
		super(baseUrl);
	}

	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	private InputStream loadResource(String url, int width, int height,
			String userName) throws Exception {
		if (url.contains("?")) {
			url = url + "&" + APIConstants.Image.WIDTH + "=" + width + "&"
					+ APIConstants.Image.HEIGHT + "=" + height;
		} else {
			url = url + "?" + APIConstants.Image.WIDTH + "=" + width + "&"
					+ APIConstants.Image.HEIGHT + "=" + height;
		}
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(AUTHORIZATION_HEADER, userName);
		return new HttpRequestExecutor()
				.GET_Resource(url, headers, true, 30000);
	}

	private InputStream loadExternalResource(String url, String userName,
			boolean isIncludeOauthToken) throws Exception {
		Map<String, String> headers = new HashMap<String, String>();
		if (isIncludeOauthToken) {
			headers.put(AUTHORIZATION_HEADER, userName);
		}
		return new HttpRequestExecutor()
				.GET_Resource(url, headers, true, 30000);
	}

	private class Invoker implements ApiInvoker {

		private COMMAND action;

		private Map<String, Object> params;

		private String userName;

		@Override
		public void setConfiguration(Configuration configuration) {
			action = configuration.getAction();
			params = configuration.getApiParams();
			userName = configuration.getUserName();
		}

		@Override
		public Object invoke() throws Exception {
			return invoke(null);
		}

		@Override
		public Object invoke(MultipartRequestProgressListener listener)
				throws Exception {
			if (COMMAND.LOAD_RESOURCE.equals(action)) {
				String url = (String) params
						.get(APIConstants.ProtectedResource.RESOURCE_URL);
				Integer width = (Integer) params.get(APIConstants.Image.WIDTH);
				if (width == null) {
					width = -1;
				}
				Integer height = (Integer) params
						.get(APIConstants.Image.HEIGHT);
				if (height == null) {
					height = -1;
				}
				return loadResource(url, width, height, userName);
			} else if (COMMAND.LOAD_EXTERNAL_RESOURCE.equals(action)) {
				String url = (String) params
						.get(APIConstants.ExternalResource.RESOURCE_URL);
				String includeOauthToken = (String) params
						.get(APIConstants.ExternalResource.INCLUDE_OAUTH_TOKEN);
				boolean isIncludeOauthToken = false;
				if (includeOauthToken != null
						&& !includeOauthToken.trim().isEmpty()) {
					try {
						isIncludeOauthToken = Boolean
								.parseBoolean(includeOauthToken.trim());
					} catch (Exception e) {
					}
				}
				return loadExternalResource(url, userName, isIncludeOauthToken);
			}
			return null;
		}

	}
}
