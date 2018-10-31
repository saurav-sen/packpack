package com.pack.pack.services.ext.doc.clasification.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.services.ext.article.comparator.ArticleInfo;
import com.pack.pack.services.ext.article.comparator.TitleBasedArticleComparator;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;

public class TitleBasedArticleComparatorTest {

	public static void main(String[] args) throws Exception {
		new TitleBasedArticleComparatorTest().doExecute();
	}

	private void doExecute() throws Exception {
		List<ArticleInfo> articles = new NewsLoader().loadAllTitles();
		new TitleBasedArticleComparator()
				.findProbableDuplicates(articles, true);
	}

	private class NewsLoader {

		private List<JRssFeed> feeds;

		private List<ArticleInfo> loadAllTitles() throws IOException {
			List<JRssFeed> feeds = loadAllFeeds();
			List<ArticleInfo> titles = new ArrayList<ArticleInfo>();
			for (JRssFeed feed : feeds) {
				titles.add(new ArticleInfo(feed.getOgTitle(), null));
			}
			return titles;
		}

		private List<JRssFeed> loadAllFeeds() throws IOException {
			Files.list(Paths.get("D:\\Saurav\\archieve\\test")).forEach(
					f -> loadFile(f));
			return feeds;
		}

		private void loadFile(Path path) {
			if (!path.toFile().getName().endsWith(".json"))
				return;
			if (feeds == null) {
				feeds = new ArrayList<JRssFeed>();
			}
			try {
				String json = new String(Files.readAllBytes(path));
				JRssFeeds jRssFeeds = JSONUtil.deserialize(json,
						JRssFeeds.class, true);
				feeds.addAll(jRssFeeds.getFeeds());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
