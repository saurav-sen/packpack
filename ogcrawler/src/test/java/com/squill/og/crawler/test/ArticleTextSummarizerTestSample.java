package com.squill.og.crawler.test;

import com.squill.og.crawler.text.summarizer.AylienArticleTextSummarizer;
import com.squill.og.crawler.text.summarizer.TextSummarization;

public class ArticleTextSummarizerTestSample {

	public static void main(String[] args) throws Exception {
		String url = "https://timesofindia.indiatimes.com/city/mumbai/us-based-spouses-queue-up-for-h1-b-as-work-permits-get-scarce/articleshow/63539010.cms";
		TextSummarization response = new AylienArticleTextSummarizer().summarize(url);
		System.out.println();
		System.out
				.println("=================================================Summary=================================================");
		System.out.println(response.extractedAllSummary(true));
		System.out
				.println("=========================================================================================================");
	}
}
