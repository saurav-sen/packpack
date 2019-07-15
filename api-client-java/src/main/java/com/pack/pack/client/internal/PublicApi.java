package com.pack.pack.client.internal;

import java.util.Map;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.Timestamp;

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

	private Long getServerTimeInMilliseconds() throws Exception {
		String url = getBaseUrl() + "sys.info/ntp";
		Timestamp timestamp = JSONUtil.deserialize(
				new HttpRequestExecutor().GET(url, null, false),
				Timestamp.class);
		return timestamp.getValue();
	}

	private JStatus validateUserName(String userName) throws Exception {
		String url = getBaseUrl() + "sys.info/user/" + userName;
		JStatus status = JSONUtil.deserialize(
				new HttpRequestExecutor().GET(url, null, false), JStatus.class);
		return status;
	}
	
	private String getAndroidApkUrl() throws Exception {
		String url = getBaseUrl() + "sys.info/android/apk.url";
		return new HttpRequestExecutor().GET(url, null, false);
	}

	private class Invoker implements ApiInvoker {

		private COMMAND action;

		private Map<String, Object> params;

		@Override
		public void setConfiguration(Configuration configuration) {
			action = configuration.getAction();
			params = configuration.getApiParams();
		}

		@Override
		public Object invoke() throws Exception {
			if (COMMAND.SYNC_TIME.equals(action)) {
				return getServerTimeInMilliseconds();
			} else if (COMMAND.VALIDATE_USER_NAME.equals(action)) {
				String userName = (String) params
						.get(APIConstants.User.USERNAME);
				return validateUserName(userName);
			} else if(COMMAND.ANDROID_APK_URL.equals(action)) {
				return getAndroidApkUrl();
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
