package com.squill.og.crawler.rss;

public interface LogTags {

	public static final String TEXT_SUMMARIZATION_ERROR = "TEXT_SUMMARIZATION_ERROR :: ";
	public static final String TEXT_SUMMARIZATION_SUCCESS = "TEXT_SUMMARIZATION_SUCCESS :: ";
	
	public static final String GEO_LOC_RESOLUTION_ERROR = "GEO_LOC_RESOLUTION_ERROR :: ";
	public static final String GEO_LOC_RESOLUTION_SUCCESS = "GEO_LOC_RESOLUTION_SUCCESS :: ";
	
	public static final String CONCEPT_EXTRACTION_ERROR = "CONCEPT_EXTRACTION_ERROR :: ";
	public static final String CONCEPT_EXTRACTION_SUCCESS = "CONCEPT_EXTRACTION_SUCCESS :: ";
	
	public static final String TAXONOMY_RESOLUTION_ERROR = "TAXONOMY_RESOLUTION_ERROR :: ";
	public static final String TAXONOMY_RESOLUTION_SUCCESS = "TAXONOMY_RESOLUTION_SUCCESS :: ";
	
	public static final String ARTICLE_EXTRACTION_ERROR = "ARTICLE_EXTRACTION_ERROR :: ";
	public static final String ARTICLE_EXTRACTION_SUCCESS = "ARTICLE_EXTRACTION_SUCCESS :: ";
}
