package com.squill.og.crawler.article.taxonomy;

public class AylienTaxonomyType {
	
	private String label;
	
	private String code;
	
	private double confidence;
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	public boolean isConfident() {
		//return confidence > 0.6f;
		return true;
	}
}
