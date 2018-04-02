package com.squill.og.crawler.test;

import com.squill.og.crawler.text.summarizer.ArticleTextSummarizer;
import com.squill.og.crawler.text.summarizer.AylienSummarization;

public class ArticleTextSummarizerTestSample {

	public static void main(String[] args) throws Exception {
		String url = "https://timesofindia.indiatimes.com/city/mumbai/us-based-spouses-queue-up-for-h1-b-as-work-permits-get-scarce/articleshow/63539010.cms";
		AylienSummarization response = new ArticleTextSummarizer().summarize(url);
		System.out.println();
		System.out
				.println("=================================================Summary=================================================");
		System.out.println(response.extractedAllSummary(true));
		System.out
				.println("=========================================================================================================");
	}
}
