package com.squill.og.crawler;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squill.og.crawler.internal.utils.HttpRequestExecutor;

public class Sample {
	
	private static final Logger $LOG = LoggerFactory.getLogger(Sample.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			HttpClient c = new DefaultHttpClient();
			HttpGet GET = new HttpGet("https://newsapi.org/v1/articles?source=talksport&apiKey=f651b01535824fdc8a7f9fb231bdae38");
			HttpResponse response = c.execute(GET);
			if(response.getStatusLine().getStatusCode() == 200) {
				$LOG.info("Succeeded #1");
			}
		} catch (ClientProtocolException e) {
			$LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			$LOG.error(e.getMessage(), e);
		}
		
		
		try {
			HttpGet GET = new HttpGet("https://newsapi.org/v1/articles?source=talksport&apiKey=f651b01535824fdc8a7f9fb231bdae38");
			HttpResponse response = new HttpRequestExecutor().GET(GET);
			if(response.getStatusLine().getStatusCode() == 200) {
				$LOG.info("Succeeded #2");
			}
		} catch (ClientProtocolException e) {
			$LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			$LOG.error(e.getMessage(), e);
		}
	}

}
