package com.pack.pack.model.web.dto;

/**
 * 
 * @author Saurav
 *
 */
public class FeedPublish {

	private String id;
	
	private boolean openDirectLink;
	
	private String titleText;
	
	private String summaryText;
	
	private boolean isNotify;
	
	private boolean useExternalSummaryAlgo;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isOpenDirectLink() {
		return openDirectLink;
	}

	public void setOpenDirectLink(boolean openDirectLink) {
		this.openDirectLink = openDirectLink;
	}

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public String getSummaryText() {
		return summaryText;
	}

	public void setSummaryText(String summaryText) {
		this.summaryText = summaryText;
	}

	public boolean isNotify() {
		return isNotify;
	}

	public void setNotify(boolean isNotify) {
		this.isNotify = isNotify;
	}

	public boolean isUseExternalSummaryAlgo() {
		return useExternalSummaryAlgo;
	}

	public void setUseExternalSummaryAlgo(boolean useExternalSummaryAlgo) {
		this.useExternalSummaryAlgo = useExternalSummaryAlgo;
	}
}
