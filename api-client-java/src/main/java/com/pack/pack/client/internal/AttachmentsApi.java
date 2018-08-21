package com.pack.pack.client.internal;

import static com.pack.pack.client.api.APIConstants.AUTHORIZATION_HEADER;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;

/**
 * 
 * @author Saurav
 *
 */
class AttachmentsApi extends BaseAPI {

	AttachmentsApi(String baseUrl) {
		super(baseUrl);
	}

	private static final String ATTACHMENT = "attachment/";

	private Invoker invoker = new Invoker();

	@Override
	protected ApiInvoker getInvoker() {
		return invoker;
	}

	private HttpResponse getProfilePicture(String userId, String fileName,
			String userName) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + ATTACHMENT + "profile/image/" + userId + "/"
				+ fileName;
		HttpGet GET = new HttpGet(url);
		GET.addHeader(AUTHORIZATION_HEADER, userName);
		HttpResponse response = client.execute(GET);
		return response;
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
			Object result = null;
			if (COMMAND.GET_PROFILE_PICTURE.equals(action)) {
				String userId = (String) params.get(APIConstants.User.ID);
				String fileName = (String) params
						.get(APIConstants.Attachment.FILE_NAME);
				result = getProfilePicture(userId, fileName, userName);
			}
			return result;
		}
	}
}