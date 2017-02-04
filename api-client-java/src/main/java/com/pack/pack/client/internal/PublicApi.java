package com.pack.pack.client.internal;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JCategories;

/**
 * 
 * @author Saurav
 *
 */
public class PublicApi extends BaseAPI {

	PublicApi(String baseUrl) {
		super(baseUrl);
	}

	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	private JCategories getAllSystemSupportedCategories() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "sys.info/categories";
		HttpGet GET = new HttpGet(url);
		HttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JCategories.class);
	}

	private class Invoker implements ApiInvoker {

		private COMMAND action;

		@Override
		public void setConfiguration(Configuration configuration) {
			action = configuration.getAction();
		}

		@Override
		public Object invoke() throws Exception {
			if (COMMAND.GET_ALL_SYSTEM_SUPPORTED_CATEGORIES.equals(action)) {
				return getAllSystemSupportedCategories();
			}
			return null;
		}

		@Override
		public Object invoke(MultipartRequestProgressListener listener)
				throws Exception {
			throw new UnsupportedOperationException(
					"Progress track is not supported by Public API");
		}

	}
}
