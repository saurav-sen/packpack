package com.pack.pack.rest.web.util.eciresult;

import java.io.File;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.rest.api.ElectionResult;
import com.pack.pack.services.redis.RedisCacheService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.SystemPropertyUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;

/**
 * 
 * @author Saurav
 *
 */
public final class ElectionResultUtil {

	private static Logger $LOG = LoggerFactory
			.getLogger(ElectionResultUtil.class);

	private ElectionResultUtil() {
	}

	public static void storeElectionResultFeed(List<ElectionResult> result, String htmlSnippet) {
		JRssFeed feed = new JRssFeed();
		try {
			String title = "Latest Update from Vote Counting for LS 2019 (INDIA)";
			feed.setOgTitle(title);
			final String leading = "Leading";
			final String won = "Won";
			final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			StringBuilder description = new StringBuilder();
			for (ElectionResult r : result) {
				String alliance = r.getName().toUpperCase();
				description = description.append(alliance).append(" ")
						.append("seats and Leading/Won by ").append(r.getLeading())
						.append("seats. ");

				dataset.addValue(r.getLeading(), alliance, leading);
			}

			feed.setOgDescription(description.toString());
			feed.setArticleSummaryText(description.toString());
			feed.setFullArticleText(description.toString());
			feed.setHtmlSnippet(htmlSnippet);

			JFreeChart barChart = ChartFactory.createBarChart(
					"LS_ELECTION_RESULTS_2019", "Alliance", "Score", dataset,
					PlotOrientation.VERTICAL, true, true, false);

			String htmlFolder = SystemPropertyUtil
					.getDefaultArchiveHtmlFolder();
			if (!htmlFolder.endsWith(File.separator)
					&& !htmlFolder.endsWith("/")) {
				htmlFolder = htmlFolder + File.separator;
			}

			int width = 640; /* Width of the image */
			int height = 480; /* Height of the image */
			String id = "eciresult" + System.currentTimeMillis() + ".jpeg";
			File BarChart = new File(htmlFolder + id);
			ChartUtilities.saveChartAsJPEG(BarChart, barChart, width, height);
			String baseUrl = SystemPropertyUtil.getExternalSharedLinkBaseUrl();
			if (!baseUrl.endsWith(SystemPropertyUtil.URL_SEPARATOR)) {
				baseUrl = baseUrl + SystemPropertyUtil.URL_SEPARATOR;
			}
			feed.setOgImage(baseUrl + id);
			feed.setOgUrl("http://squill.in/api/electionResult?code=IN");
			feed.setShareableUrl("http://squill.in/api/electionResult?code=IN");
			feed.setOgType(JRssFeedType.NEWS.name());

			String json = JSONUtil.serialize(feed, false);
			RedisCacheService service = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			service.addToCache("NEWS_FEED_ECIRESULT_INDIA_LS_2019", json,
					10 * 60 * 60);
		} catch (Exception e) {
			$LOG.error(e.getMessage(), e);
		}
	}

	public static JRssFeed getElectionResultNewsFeed() {
		try {
			RedisCacheService service = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			String json = service.getFromCache(
					"NEWS_FEED_ECIRESULT_INDIA_LS_2019", String.class);
			if(json == null) {
				return null;
			}
			return JSONUtil.deserialize(json, JRssFeed.class, false);
		} catch (Exception e) {
			$LOG.error(e.getMessage(), e);
			return null;
		}
	}
}
