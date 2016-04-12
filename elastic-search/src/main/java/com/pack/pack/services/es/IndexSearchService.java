package com.pack.pack.services.es;

import static com.pack.pack.services.es.Constants.CONTENT_TYPE_HEADER_NAME;
import static com.pack.pack.services.es.Constants.ES_BASE_URL;
import static com.pack.pack.services.es.Constants.ES_CITY_DOC;
import static com.pack.pack.services.es.Constants.ES_LOCALITY_INDEX;
import static com.pack.pack.services.es.Constants.ES_TOPIC_DETAIL_INDEX;
import static com.pack.pack.services.es.Constants.ES_TOPIC_DOC;
import static com.pack.pack.services.es.Constants.ES_USER_DETAIL_INDEX;
import static com.pack.pack.services.es.Constants.ES_USER_DOC;
import static com.pack.pack.services.es.Constants.URL_SEPARATOR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.es.CityLocation;
import com.pack.pack.model.es.TopicDetail;
import com.pack.pack.model.es.UserDetail;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public class IndexSearchService {

	private CloseableHttpClient client;

	private static final String QUERY = "query";
	private static final String MATCH = "match";
	private static final String FUZZINESS = "fuzziness";
	private static final String PREFIX_LENGTH = "prefix_length";

	private static final String _SEARCH = "_search";
	
	private static final String _SOURCE = "_source";

	private static final String HITS = "hits";

	private String esBaseURL = null;
	
	public static final IndexSearchService INSTANCE = new IndexSearchService();

	private IndexSearchService() {
		client = HttpClientBuilder.create().build();
		esBaseURL = System.getProperty(ES_BASE_URL);
		if (!esBaseURL.endsWith(URL_SEPARATOR)) {
			esBaseURL = esBaseURL + URL_SEPARATOR;
		}
	}

	public List<CityLocation> searchCityByName(String pattern)
			throws PackPackException {
		List<CityLocation> result = null;
		try {
			String esUrl = new StringBuilder(esBaseURL).append(ES_CITY_DOC)
					.append(URL_SEPARATOR).append(ES_LOCALITY_INDEX)
					.append(URL_SEPARATOR).append(_SEARCH).toString();
			HttpPost POST = new HttpPost(esUrl);
			POST.addHeader(CONTENT_TYPE_HEADER_NAME,
					ContentType.APPLICATION_JSON.getMimeType());
			JSONObject jsonObj = new JSONObject();
			JSONObject queryObj = new JSONObject();
			JSONObject matchObj = new JSONObject();
			JSONObject cityQueryObj = new JSONObject();
			cityQueryObj.put(QUERY, pattern);
			cityQueryObj.put(FUZZINESS, 2);
			cityQueryObj.put(PREFIX_LENGTH, 1);
			matchObj.put("city", cityQueryObj);
			queryObj.put(MATCH, matchObj);
			jsonObj.put(QUERY, queryObj);
			String json = jsonObj.toString();
			HttpEntity jsonQuery = new StringEntity(json,
					ContentType.APPLICATION_JSON);
			POST.setEntity(jsonQuery);
			CloseableHttpResponse response = client.execute(POST);
			json = EntityUtils.toString(response.getEntity());
			jsonObj = new JSONObject(json);
			jsonObj = jsonObj.getJSONObject(HITS);
			JSONArray jsonArray = jsonObj.getJSONArray(HITS);
			int len = jsonArray.length();
			result = new ArrayList<CityLocation>((int) (len * 1.5f));
			for (int i = 0; i < len; i++) {
				jsonObj = jsonArray.getJSONObject(i);
				jsonObj = jsonObj.getJSONObject(_SOURCE);
				CityLocation cityLocation = JSONUtil.deserialize(
						jsonObj.toString(), CityLocation.class);
				result.add(cityLocation);
			}
			return result;
		} catch (ClientProtocolException e) {
			throw new PackPackException("TODO", e.getMessage(), e);
		} catch (IOException e) {
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}

	public List<UserDetail> searchUserByName(String pattern)
			throws PackPackException {
		List<UserDetail> result = null;
		try {
			String esUrl = new StringBuilder(esBaseURL).append(ES_USER_DOC)
					.append(URL_SEPARATOR).append(ES_USER_DETAIL_INDEX)
					.append(URL_SEPARATOR).append(_SEARCH).toString();
			HttpPost POST = new HttpPost(esUrl);
			POST.addHeader(CONTENT_TYPE_HEADER_NAME,
					ContentType.APPLICATION_JSON.getMimeType());
			JSONObject jsonObj = new JSONObject();
			JSONObject queryObj = new JSONObject();
			JSONObject matchObj = new JSONObject();
			JSONObject nameQueryObj = new JSONObject();
			nameQueryObj.put(QUERY, pattern);
			nameQueryObj.put(FUZZINESS, 2);
			nameQueryObj.put(PREFIX_LENGTH, 1);
			matchObj.put("name", nameQueryObj);
			queryObj.put(MATCH, matchObj);
			jsonObj.put(QUERY, queryObj);
			String json = jsonObj.toString();
			HttpEntity jsonQuery = new StringEntity(json,
					ContentType.APPLICATION_JSON);
			POST.setEntity(jsonQuery);
			CloseableHttpResponse response = client.execute(POST);
			json = EntityUtils.toString(response.getEntity());
			jsonObj = new JSONObject(json);
			jsonObj = jsonObj.getJSONObject(HITS);
			JSONArray jsonArray = jsonObj.getJSONArray(HITS);
			int len = jsonArray.length();
			result = new ArrayList<UserDetail>((int) (len * 1.5f));
			for (int i = 0; i < len; i++) {
				jsonObj = jsonArray.getJSONObject(i);
				jsonObj = jsonObj.getJSONObject(_SOURCE);
				UserDetail cityLocation = JSONUtil.deserialize(
						jsonObj.toString(), UserDetail.class);
				result.add(cityLocation);
			}
			return result;
		} catch (ClientProtocolException e) {
			throw new PackPackException("TODO", e.getMessage(), e);
		} catch (IOException e) {
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}

	public List<TopicDetail> searchTopic(String pattern)
			throws PackPackException {
		List<TopicDetail> result = null;
		try {
			String esUrl = new StringBuilder(esBaseURL).append(ES_TOPIC_DOC)
					.append(URL_SEPARATOR).append(ES_TOPIC_DETAIL_INDEX)
					.append(URL_SEPARATOR).append(_SEARCH).toString();
			HttpPost POST = new HttpPost(esUrl);
			POST.addHeader(CONTENT_TYPE_HEADER_NAME,
					ContentType.APPLICATION_JSON.getMimeType());
			JSONObject jsonObj = new JSONObject();
			JSONObject queryObj = new JSONObject();
			JSONObject matchObj = new JSONObject();
			JSONObject nameQueryObj = new JSONObject();
			nameQueryObj.put(QUERY, pattern);
			nameQueryObj.put(FUZZINESS, 2);
			nameQueryObj.put(PREFIX_LENGTH, 1);
			matchObj.put("name", nameQueryObj);
			queryObj.put(MATCH, matchObj);
			jsonObj.put(QUERY, queryObj);
			String json = jsonObj.toString();
			HttpEntity jsonQuery = new StringEntity(json,
					ContentType.APPLICATION_JSON);
			POST.setEntity(jsonQuery);
			CloseableHttpResponse response = client.execute(POST);
			json = EntityUtils.toString(response.getEntity());
			jsonObj = new JSONObject(json);
			jsonObj = jsonObj.getJSONObject(HITS);
			JSONArray jsonArray = jsonObj.getJSONArray(HITS);
			int len = jsonArray.length();
			result = new ArrayList<TopicDetail>((int) (len * 1.5f));
			for (int i = 0; i < len; i++) {
				jsonObj = jsonArray.getJSONObject(i);
				jsonObj = jsonObj.getJSONObject(_SOURCE);
				TopicDetail topicDetail = JSONUtil.deserialize(
						jsonObj.toString(), TopicDetail.class);
				result.add(topicDetail);
			}
			return result;
		} catch (ClientProtocolException e) {
			throw new PackPackException("TODO", e.getMessage(), e);
		} catch (IOException e) {
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}
}