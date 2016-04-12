package com.pack.pack.client.internal;

import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JBrands;

import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.BASE_URL;

/**
 * 
 * @author Saurav
 *
 */
public class BrandsApi extends AbstractAPI {

	private static final String BRANDS = "/brands/";
	
	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	public JBrands getBrandsInfo(String companyName, String oAuthToken)
			throws Exception {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = BASE_URL + BRANDS + "companyName/" + companyName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		CloseableHttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JBrands.class);
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