package com.pack.pack.client.api.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class Main {
	
	//private static final String ANDROID_APP_CLIENT_KEY = "53e8a1f2-7568-4ac8-ab26-45738ca02599t";
	//private static final String ANDROID_APP_CLIENT_SECRET = "b1f6d761-dcb7-482b-a695-ab17e4a29b25";
	
	/*public static final String USERNAME = "sourabhnits@gmail.com";
	public static final String PASSWORD = "password";*/
	
	
	public static void main(String[] args) throws Exception {
		String str = "\"opbu\": \"https://docs.google.com/a/ciphercloud.co/picker\",\n" +
				"                \"opru\": \"https://docs.google.com/relay.html\",\n" +
				"                \"opdu\": true,\n" +
				"                \"opccp\": false,\n" +
				"                \"ophi\": \"kix\",\n" +
				"                \"opst\": \"000770F203FFD4D8E9460322CF254E99241EF3B52A7FBBFEFB::1552644753362\",\n" +
				"                \"opuci\": \"document\",\n" +
				"                \"docs-to\": \"https://docs.google.com\"";
		str = str.replaceAll("https://docs.google.com/",
				"https://docs-google-com.qa.ciphercloud.in/");
		System.out.println(str);
	}
}
