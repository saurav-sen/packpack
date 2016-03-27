package com.packpack.es.data.index;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
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

	public LocalityMasterSetup() {
		String esIp = System.getProperty("esIp");
		String esPort = System.getProperty("esPort");
		if (esPort == null || esPort.trim().equals("")) {
			esPort = "9200";
		}
		esPort = esPort.trim();
		ES_CITY_LICATION_INDEX_URL = "http://" + esIp + ":" + esPort
				+ "/city/locality/";
	}

	public void setup() throws Exception {
		BufferedReader reader = null;
		// BufferedWriter writer = null;
		try {
			String line = null;
			reader = new BufferedReader(new FileReader(
					"./IN.txt"));
			// writer = new BufferedWriter(new
			// FileWriter("D:/Saurav/packpack/load-master/src/main/resources/IN_out.txt"));
			while ((line = reader.readLine()) != null) {
				String[] split = line.split("\t");
				String pincode = split[1];
				String locality = split[2];
				String state = split[3];
				String city = split[5];
				String country = "India";
				CityLocation c = new CityLocation();
				c.setCountry(country);
				c.setCity(city);
				c.setName(locality);
				c.setState(state);
				c.setPincode(pincode);

				indexDataElasticsearch(c);
				// String json = JSONUtil.serialize(c, false);
				// writer.write(json);
				// writer.newLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

	private String indexDataElasticsearch(CityLocation cityLocation) throws Exception {
		String json = JSONUtil.serialize(cityLocation, false);
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPut PUT = new HttpPut(ES_CITY_LICATION_INDEX_URL);
		HttpEntity jsonBody = new StringEntity(json);
		PUT.setEntity(jsonBody);
		CloseableHttpResponse response = client.execute(PUT);
		return EntityUtils.toString(response.getEntity());
	}
}