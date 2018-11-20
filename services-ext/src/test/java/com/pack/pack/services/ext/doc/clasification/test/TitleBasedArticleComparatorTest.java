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

	/*public static void main(String[] args) throws Exception {
		new TitleBasedArticleComparatorTest().doExecute();
	}*/
	
	public static void main(String[] args) {
		ArticleInfo src = new ArticleInfo("Dead in cars and homes: Northern California fire toll at 42", null);
		ArticleInfo tgt = new ArticleInfo("Death toll in California wildfire jumps to 42", null);
		System.out.println(new TitleBasedArticleComparator().checkIsProbableDuplicate(src, tgt));
		
		src = new ArticleInfo("Rajnikanth hints Modi is more powerful than Opposition", null);
		tgt = new ArticleInfo("Rajnikanth clarifies his remark on BJP, says party is dangerous for opposition - Times of India", null);
		System.out.println(new TitleBasedArticleComparator().checkIsProbableDuplicate(src, tgt));
		
		src = new ArticleInfo("Isro begins countdown for GSLV MkIII - GSAT-29 mission - Times of India", null);
		tgt = new ArticleInfo("Red letter day: Paan stain eraser wins students a US award - Times of India", null);
		System.out.println(new TitleBasedArticleComparator().checkIsProbableDuplicate(src, tgt));
		
		src = new ArticleInfo("ISRO successfully launches communication satellite GSAT-29", null);
		tgt = new ArticleInfo("Isros GSLV-MkIII-D2 rocket places GSAT-29 in orbit; mission success gives Isro boast before Chandrayaan-2 and manned mission - Times of India", null);
		System.out.println(new TitleBasedArticleComparator().checkIsProbableDuplicate(src, tgt));
		
		src = new ArticleInfo("Justice Dept. Defends Legality of Trumps Appointment of Acting Attorney General", null);
		tgt = new ArticleInfo("I dont lie, says Dassault CEO", null);
		System.out.println(new TitleBasedArticleComparator().checkIsProbableDuplicate(src, tgt));
		
		src = new ArticleInfo("U.K. Cabinet Backs Theresa May’s Brexit Plan", null);
		tgt = new ArticleInfo("SC to review Sabarimala verdict in Jan, no bar on women till then", null);
		System.out.println(new TitleBasedArticleComparator().checkIsProbableDuplicate(src, tgt));
		
		src = new ArticleInfo("BJP blocking name change of West Bengal: Mamata Banerjee", null);
		tgt = new ArticleInfo("Sajjad Lone could be BJPs choice for next Jammu & Kashmir CM", null);
		System.out.println(new TitleBasedArticleComparator().checkIsProbableDuplicate(src, tgt));
		
		src = new ArticleInfo("Delhi: Chilli powder thrown at CM Arvind Kejriwal inside secretariat - Times of India", null);
		tgt = new ArticleInfo("Man throws red chilli powder at Arvind Kejriwal in Delhi Secretariat", null);
		System.out.println(new TitleBasedArticleComparator().checkIsProbableDuplicate(src, tgt));
		
		src = new ArticleInfo("Stock Markets Slide Is Flashing a Warning About the Economy", null);
		tgt = new ArticleInfo("These 5 Tech Stocks Combined Have Lost More Than $800 Billion in Market Value", null);
		System.out.println(new TitleBasedArticleComparator().checkIsProbableDuplicate(src, tgt));
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
