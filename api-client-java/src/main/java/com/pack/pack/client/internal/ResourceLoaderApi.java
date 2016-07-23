package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
			String oAuthToken) throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		if (url.contains("?")) {
			url = url + "&" + APIConstants.Image.WIDTH + "=" + width + "&"
					+ APIConstants.Image.HEIGHT + "=" + height;
		} else {
			url = url + "?" + APIConstants.Image.WIDTH + "=" + width + "&"
					+ APIConstants.Image.HEIGHT + "=" + height;
		}
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return GZipUtil.decompress(response.getEntity()).getContent();
	}

	private InputStream loadExternalResource(String url)
			throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet GET = new HttpGet(url);
		HttpResponse response = client.execute(GET);
		return GZipUtil.decompress(response.getEntity()).getContent();
	}

	private class Invoker implements ApiInvoker {

		private COMMAND action;

		private Map<String, Object> params;

		private String oAuthToken;

		@Override
		public void setConfiguration(Configuration configuration) {
			action = configuration.getAction();
			params = configuration.getApiParams();
			oAuthToken = configuration.getOAuthToken();
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
				return loadResource(url, width, height, oAuthToken);
			} else if (COMMAND.LOAD_EXTERNAL_RESOURCE.equals(action)) {
				String url = (String) params
						.get(APIConstants.ExternalResource.RESOURCE_URL);
				return loadExternalResource(url);
			}
			return null;
		}

	}
}
