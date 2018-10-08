package com.squill.og.crawler.test;

import java.util.regex.Pattern;

public class JsoupTest {

	/*public static void main(String[] args) throws Exception {
		String content = new String(Files.readAllBytes(Paths.get("C:\\Users\\CipherCloud\\Desktop\\news-card\\news.html")));
		Document doc = Jsoup.parse(content);
		System.out.println(doc.body().text().replaceAll("[^\\x00-\\x7F]",""));
	}*/

	public static void main(String[] args) {
		String PASSWORD_PATTERN_REGEX =
	            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
	    Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_PATTERN_REGEX);
	    String passwd = "SsEn0x5f3759df";
	    boolean bool = PASSWORD_PATTERN.matcher(passwd).matches();
	    if(bool) {
	    	System.out.println("YES");
	    } else {
	    	System.out.println("NO");
	    }
	}
}
