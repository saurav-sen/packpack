package com.pack.pack.services.ext.text.summerize.test;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONObject;

import com.pack.pack.services.ext.text.summerize.WebDocumentParser;

public class WebDocumentParserTest {

	public static void main(String[] args) {
		JSONObject json = new WebDocumentParser().parse(
				"https://timesofindia.indiatimes.com/city/hyderabad/fir-against-rainbow-hospital-for-taking-parking-fee-from-patients/articleshow/65018230.cms");
		System.out.println(StringEscapeUtils.unescapeJava(json.toJSONString()));
	}
}