package com.pack.pack.services.ext.article.comparator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Lazy
@Scope("singleton")
public class TitleBasedArticleComparator {
	
	public boolean checkIsProbableDuplicate(ArticleInfo src, ArticleInfo tgt) {
		return new ArticleInfoMatcher().isEQUAL(src, tgt);
	}
	
	public List<ArticleInfo> checkProbableDuplicates(ArticleInfo src,
			List<ArticleInfo> tgtList) throws Exception {
		List<ArticleInfo> result = new LinkedList<ArticleInfo>();
		int len = tgtList.size();
		for (int i = 0; i < len; i++) {
			ArticleInfo tgt = tgtList.get(i);
			if (tgt.isMatchFound())
				continue;
			if(src.equals(tgt))
				continue;
			if (new ArticleInfoMatcher().isEQUAL(src, tgt)) {
				//result.add(src);
				result.add(tgt);
				return result;
			}
		}

		return result;
	}

	public Map<Integer, List<Integer>> findProbableDuplicates(
			List<ArticleInfo> articles, boolean verbose) throws Exception {
		Map<Integer, List<Integer>> groups = findProbableDuplicates(articles);
		if (verbose) {
			printMatches(groups, articles);
		}
		return groups;
	}

	public Map<Integer, List<Integer>> findProbableDuplicates(
			List<ArticleInfo> articles) throws Exception {
		Map<Integer, List<Integer>> groups = new HashMap<Integer, List<Integer>>();
		int len = articles.size();
		for (int i = 0; i < len; i++) {
			ArticleInfo src = articles.get(i);
			for (int j = i + 1; j < len; j++) {
				ArticleInfo tgt = articles.get(j);
				if (tgt.isMatchFound())
					continue;
				if (new ArticleInfoMatcher().isEQUAL(src, tgt)) {
					List<Integer> list = groups.get(i);
					if (list == null) {
						list = new LinkedList<Integer>();
						groups.put(i, list);
					}
					list.add(j);
					tgt.setMatchFound(true);
				}
			}
		}

		return groups;
	}

	private void printMatches(Map<Integer, List<Integer>> groups,
			List<ArticleInfo> articles) {
		StringBuilder sb = new StringBuilder();
		Iterator<Integer> itr = groups.keySet().iterator();
		while (itr.hasNext()) {
			int srcIndex = itr.next();
			String title = articles.get(srcIndex).getOriginalText();
			sb.append(title).append("\n");
			List<Integer> indices = groups.get(srcIndex);
			if (indices != null && !indices.isEmpty()) {
				for (int index : indices) {
					sb.append(articles.get(index).getOriginalText()).append(
							"\n");
				}
			}
			sb.append(
					"***************************************************************************************************************")
					.append("\n");
		}

		System.out.println(sb.toString());
	}
}
