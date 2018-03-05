package com.pack.pack.client.internal;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JCategories;
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

	private JCategories getAllSystemSupportedCategories() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "sys.info/categories";
		HttpGet GET = new HttpGet(url);
		HttpResponse response = client.execute(GET);
		return JSONUtil.deserialize(EntityUtils.toString(response.getEntity()),
				JCategories.class);
	}

	private Long getServerTimeInMilliseconds() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "sys.info/ntp";
		HttpGet GET = new HttpGet(url);
		HttpResponse response = client.execute(GET);
		Timestamp timestamp = JSONUtil.deserialize(
				EntityUtils.toString(response.getEntity()), Timestamp.class);
		return timestamp.getValue();
	}

	private JStatus validateUserName(String userName) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "sys.info/user/" + userName;
		HttpGet GET = new HttpGet(url);
		HttpResponse response = client.execute(GET);
		JStatus status = JSONUtil.deserialize(
				EntityUtils.toString(response.getEntity()), JStatus.class);
		return status;
	}
	
	private String getAndroidApkUrl() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = getBaseUrl() + "sys.info/android/apk.url";
		HttpGet GET = new HttpGet(url);
		HttpResponse response = client.execute(GET);
		return EntityUtils.toString(response.getEntity());
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
