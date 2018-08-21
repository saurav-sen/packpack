package com.pack.pack.services.ext.text.summerize;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.squill.feed.web.model.JRssFeed;

import de.l3s.boilerpipe.extractors.LargestContentExtractor;

@Component
@Lazy
@Scope("singleton")
public class WebDocumentParser {

	/*public static final String ARTICLE_TITLE = "title";

	public static final String ARTICLE_FULL_TEXT = "articleFullText";
	
	public static final String ARTICLE_SUMMARY_TEXT = "articleSummaryText";

	public static final String SOURCE = "source";

	public static final String STATUS = "status";

	public static final String STATUS_SUCCESS = "success";

	public static final String STATUS_ERROR = "error";*/

	public JRssFeed parse(String url) {
		JRssFeed json = new JRssFeed();
		try {
			HttpGet httpget = new HttpGet(url);
			HttpEntity entity = null;
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(httpget);
			entity = response.getEntity();
			if (entity != null) {
				InputStream input = entity.getContent();
				ContentHandler textHandler = new BodyContentHandler();
				Metadata metadata = new Metadata();
				AutoDetectParser parser = new AutoDetectParser();
				ParseContext context = new ParseContext();
				/*
				 * BoilerpipeContentHandler handler2 = new
				 * BoilerpipeContentHandler(textHandler,
				 * ArticleExtractor.getInstance());
				 */
				BoilerpipeContentHandler handler2 = new BoilerpipeContentHandler(
						textHandler, LargestContentExtractor.getInstance());
				parser.parse(input, handler2, metadata, context);
				String title = metadata.get("title");
				String ogTitle = metadata.get("og:title");
				String article = textHandler.toString();
				if (title != null) {
					article.replaceAll(title.trim(), "");
				}
				if (ogTitle != null) {
					article.replaceAll(ogTitle.trim(), "");
					title = ogTitle;
				}

				String ogImage = metadata.get("og:image");
				if(ogImage == null) {
					ogImage = metadata.get("twitter:image");
				}
				json.setOgImage(ogImage);
				//json.put("og:site_name", metadata.get("og:site_name"));
				//json.put("keywords", metadata.get("keywords"));
				String keywordsText = metadata.get("keywords");
				if(keywordsText != null) {
					String[] keywords = keywordsText.split(",");
					for(String keyword : keywords) {
						json.getKeywords().add(keyword.trim());
					}
				}
				
				String ogUrl = metadata.get("og:url");
				if(ogUrl == null) {
					ogUrl = metadata.get("twitter:url");
				}
				if(ogUrl == null) {
					ogUrl = url;
				}
				json.setOgUrl(ogUrl);
				json.setHrefSource(ogUrl);
				
				String ogDescription = metadata.get("og:description");
				if(ogDescription == null) {
					ogDescription = metadata.get("twitter:description");
				}
				if(ogDescription == null) {
					ogDescription = metadata.get("description");
				}
				json.setOgDescription(ogDescription);
				/*json.put("y_key", metadata.get("y_key"));
				json.put("fb:pages", metadata.get("fb:pages"));*/
				
				json.setOgTitle(title);
				json.setFullArticleText(article);
				
				Summarizer summarizer = new Summarizer();
				String summaryText = summarizer.Summarize(article, 3);
				
				json.setArticleSummaryText(summaryText);
				
				json.setId(String.valueOf(System.currentTimeMillis()));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}

		return json;
	}
}