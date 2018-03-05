package com.pack.pack.services.es;

import static com.pack.pack.services.es.Constants.CONTENT_TYPE_HEADER_NAME;
import static com.pack.pack.services.es.Constants.ES_BASE_URL;
import static com.pack.pack.services.es.Constants.ES_CITY_DOC;
import static com.pack.pack.services.es.Constants.ES_LOCALITY_INDEX;
import static com.pack.pack.services.es.Constants.URL_SEPARATOR;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.es.UserDetail;

/**
 * 
 * @author Saurav
 *
 */
public class IndexUploadService {

	private static Logger logger = LoggerFactory
			.getLogger(IndexUploadService.class);
	
	private String esBaseUrl;
	
	private CloseableHttpClient client;
	
	public static final IndexUploadService INSTANCE = new IndexUploadService();

	private IndexUploadService() {
		esBaseUrl = System.getProperty(ES_BASE_URL);
		client = HttpClientBuilder.create().build();
	}

	public void uploadNewUserDetails(UserDetail userDetail)
			throws Exception {
		String url = new StringBuilder(esBaseUrl).append(ES_CITY_DOC)
				.append(URL_SEPARATOR).append(ES_LOCALITY_INDEX)
				.append(URL_SEPARATOR).append(userDetail.getUserId())
				.toString();
		HttpPut PUT = new HttpPut(url);
		PUT.addHeader(CONTENT_TYPE_HEADER_NAME,
				ContentType.APPLICATION_JSON.getMimeType());
		String json = JSONUtil.serialize(userDetail);
		HttpEntity jsonBody = new StringEntity(json,
				ContentType.APPLICATION_JSON);
		PUT.setEntity(jsonBody);
		CloseableHttpResponse response = client.execute(PUT);
		logger.info("Successfully uploaded new user details to ES @ PUT "
				+ esBaseUrl);
		logger.info(EntityUtils.toString(response.getEntity()));
	}
}