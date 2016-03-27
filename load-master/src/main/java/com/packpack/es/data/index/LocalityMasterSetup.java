package com.packpack.es.data.index;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.es.CityLocation;

/**
 * 
 * @author Saurav
 *
 */
public class LocalityMasterSetup {

	private String ES_CITY_LICATION_INDEX_URL;
	
	private CloseableHttpClient client;

	public LocalityMasterSetup() {
		String esIp = System.getProperty("esIp");
		String esPort = System.getProperty("esPort");
		if (esPort == null || esPort.trim().equals("")) {
			esPort = "9200";
		}
		esPort = esPort.trim();
		ES_CITY_LICATION_INDEX_URL = "http://" + esIp + ":" + esPort
				+ "/city/locality/";
		client = HttpClientBuilder.create().build();
	}

	public void setup() throws Exception {
		BufferedReader reader = null;
		try {
			String line = null;
			reader = new BufferedReader(new FileReader(
					/*"D:/Saurav/packpack/load-master/src/bin/IN.txt"));*/"./IN.txt"));
			int count = 1;
			while ((line = reader.readLine()) != null) {
				String[] split = line.split("\t");
				String pincode = split[1].trim();
				String locality = split[2].trim();
				String state = split[3].trim();
				String city = split[5].trim();
				String country = "India";
				CityLocation c = new CityLocation();
				c.setCountry(country);
				c.setCity(city);
				c.setName(locality);
				c.setState(state);
				c.setPincode(pincode);

				indexDataElasticsearch(c, count);
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			/*
			 * if(writer != null) { writer.close(); }
			 */
		}
	}

	private String indexDataElasticsearch(CityLocation cityLocation, int id) throws Exception {
		String json = JSONUtil.serialize(cityLocation, false);
		HttpPut PUT = new HttpPut(ES_CITY_LICATION_INDEX_URL + id);
		HttpEntity jsonBody = new StringEntity(json, ContentType.APPLICATION_JSON);
		PUT.setEntity(jsonBody);
		CloseableHttpResponse response = client.execute(PUT);
		String str = EntityUtils.toString(response.getEntity());
		System.out.println(ES_CITY_LICATION_INDEX_URL + id);
		System.out.println(str);
		return str;
	}
}