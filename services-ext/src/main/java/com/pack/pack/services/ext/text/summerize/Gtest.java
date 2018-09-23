package com.pack.pack.services.ext.text.summerize;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.pack.pack.services.ext.HttpRequestExecutor;

public class Gtest {

	public static void main(String[] args) {
		try {
			HttpGet GET = new HttpGet("https://newsapi.org/v1/articles?source=talksport&apiKey=f651b01535824fdc8a7f9fb231bdae38");
			HttpResponse response = new HttpRequestExecutor().GET(GET);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				System.out.println("YES");
			} else {
				System.out.println("NO");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
