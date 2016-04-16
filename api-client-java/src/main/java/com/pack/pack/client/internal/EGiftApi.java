package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.APPLICATION_JSON;
import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;
import static com.pack.pack.client.api.APIConstants.BASE_URL;
import static com.pack.pack.client.api.APIConstants.CONTENT_TYPE_HEADER;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JeGift;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.dto.EGiftForwardDTO;
import com.pack.pack.model.web.dto.PackReceipent;
import com.pack.pack.model.web.dto.PackReceipentType;

/**
 * 
 * @author Saurav
 *
 */
public class EGiftApi extends AbstractAPI {

	private static final String EGIFTS = "egifts/";
	
	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	private JeGift getEGiftById(String egiftId, String oAuthToken)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + EGIFTS + egiftId;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JeGift.class);
	}

	@SuppressWarnings("unchecked")
	private Pagination<JeGift> getAllEGiftsByBrandId(String brandId,
			String pageLink, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + EGIFTS + "brand/" + brandId + "/page/"
				+ pageLink;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				Pagination.class);
	}

	@SuppressWarnings("unchecked")
	private Pagination<JeGift> getAllEGiftsByCategory(String category,
			String pageLink, String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + EGIFTS + "category/" + category + "/page/"
				+ pageLink;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		HttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				Pagination.class);
	}

	private JStatus forwardEGift(EGiftForwardDTO dto, String egiftId,
			String oAuthToken) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = BASE_URL + EGIFTS + egiftId;
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(AUTHORIZATION_HEADER, oAuthToken);
		PUT.addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON);
		String json = JSONUtil.serialize(dto);
		HttpEntity jsonBody = new StringEntity(json);
		PUT.setEntity(jsonBody);
		HttpResponse response = client.execute(PUT);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JStatus.class);
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
			if (COMMAND.GET_EGIFT_BY_ID.equals(action)) {
				String egiftId = (String) params.get(APIConstants.EGift.ID);
				result = getEGiftById(egiftId, oAuthToken);
			} else if (COMMAND.GET_EGIFTS_BY_BRAND_ID.equals(action)) {
				String brandId = (String) params.get(APIConstants.Brand.ID);
				String pageLink = (String) params
						.get(APIConstants.PageInfo.PAGE_LINK);
				if (pageLink == null || pageLink.trim().equals("")) {
					pageLink = "FIRST_PAGE";
				}
				result = getAllEGiftsByBrandId(brandId, pageLink, oAuthToken);
			} else if (COMMAND.GET_EGIFTS_BY_CATEGORY.equals(action)) {
				String category = (String) params
						.get(APIConstants.EGift.CATEGORY);
				String pageLink = (String) params
						.get(APIConstants.PageInfo.PAGE_LINK);
				if (pageLink == null || pageLink.trim().equals("")) {
					pageLink = "FIRST_PAGE";
				}
				result = getAllEGiftsByCategory(category, pageLink, oAuthToken);
			} else if (COMMAND.FORWARD_EGIFT.equals(action)) {
				String eGiftId = (String) params.get(APIConstants.EGift.ID);
				String fromUserId = (String) params
						.get(APIConstants.ForwardEGift.FROM_USER_ID);
				String toUserId = (String) params
						.get(APIConstants.ForwardEGift.TO_USER_ID);
				String title = (String) params
						.get(APIConstants.ForwardEGift.TITLE);
				String message = (String) params
						.get(APIConstants.ForwardEGift.MESSAGE);
				EGiftForwardDTO dto = new EGiftForwardDTO();
				dto.setFromUserId(fromUserId);
				dto.setMessage(message);
				dto.setTitle(title);
				PackReceipent receipent = new PackReceipent();
				receipent.setToUserId(toUserId);
				receipent.setType(PackReceipentType.USER);
				dto.getReceipents().add(receipent);
				result = forwardEGift(dto, eGiftId, oAuthToken);
			}
			return result;
		}
	}
}