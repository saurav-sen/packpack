package com.pack.pack.rest.api;

import java.util.ArrayList;
import java.util.List;

public class ElectionResults {

	private List<ElectionResult> results;

	public List<ElectionResult> getResults() {
		if(results == null) {
			results = new ArrayList<ElectionResult>();
		}
		return results;
	}

	public void setResults(List<ElectionResult> results) {
		this.results = results;
	}
}
