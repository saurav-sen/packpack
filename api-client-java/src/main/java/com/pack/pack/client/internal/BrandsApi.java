package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JBrands;

/**
 * 
 * @author Saurav
 *
 */
class BrandsApi extends BaseAPI {

	BrandsApi(String baseUrl) {
		super(baseUrl);
	}

	private static final String BRANDS = "/brands/";

	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	public JBrands getBrandsInfo(String companyName, String oAuthToken)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + BRANDS + "companyName/" + companyName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return JSONUtil
				.deserialize(EntityUtils.toString(GZipUtil.decompress(response
						.getEntity())), JBrands.class);
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
			Object result = null;
			if (COMMAND.SEARCH_BRANDS_INFO.equals(action)) {
				String companyName = (String) params
						.get(APIConstants.Brand.COMPANY_NAME);
				return getBrandsInfo(companyName, oAuthToken);
			}
			return result;
		}
	}
}