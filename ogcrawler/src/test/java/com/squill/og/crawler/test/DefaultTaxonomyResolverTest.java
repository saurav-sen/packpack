package com.squill.og.crawler.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.pack.pack.common.util.JSONUtil;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JTaxonomy;
import com.squill.og.crawler.content.handlers.DefaultTaxonomyResolver;
import com.squill.og.crawler.iptc.subjectcodes.SubjectCodeRegistry;

public class DefaultTaxonomyResolverTest {

	public static void main(String[] args) throws Exception {
		JTaxonomy[] resolveTaxonomies = new DefaultTaxonomyResolver()
				.resolveTaxonomies(
						null,
						"https://www.newscientist.com/article/2169382-an-ai-can-now-tell-how-malnourished-a-child-is-just-from-a-photo/");
		if (resolveTaxonomies == null || resolveTaxonomies.length == 0) {
			System.err.println("Failed");
		} else {
			JTaxonomies c = new JTaxonomies();
			List<JTaxonomy> taxonomies = Arrays.asList(resolveTaxonomies);
			c.getTaxonomies().addAll(taxonomies);
			for (JTaxonomy taxonomy : taxonomies) {
				JRssFeedType feedType = SubjectCodeRegistry.INSTANCE
						.resolveSquillFeedType(taxonomy);
				if (feedType == null) {
					System.err.println("Failed for " + taxonomy.getId() + " Name: " + taxonomy.getName());
					continue;
				}
				System.out.println(feedType.name());
			}
			String json = JSONUtil.serialize(c);
			Files.write(Paths.get("D:/Saurav/VM/taxonomy_test.json"),
					json.getBytes());
		}
	}

}
