package com.squill.feed.web.model;

/**
 * 
 * @author Saurav
 *
 */
public class JRssFeedUploadRequest {

	private JRssFeed content;
	
	private boolean isNotify = false;
	
	private boolean openDirectLink = false;
	
	private boolean checkDuplicate = true;
	
	private String feedType = JRssFeedType.NEWS.name();

	public JRssFeed getContent() {
		return content;
	}

	public void setContent(JRssFeed content) {
		this.content = content;
	}

	public boolean isNotify() {
		return isNotify;
	}

	public void setNotify(boolean isNotify) {
		this.isNotify = isNotify;
	}

	public boolean isOpenDirectLink() {
		return openDirectLink;
	}

	public void setOpenDirectLink(boolean openDirectLink) {
		this.openDirectLink = openDirectLink;
	}

	public boolean isCheckDuplicate() {
		return checkDuplicate;
	}

	public void setCheckDuplicate(boolean checkDuplicate) {
		this.checkDuplicate = checkDuplicate;
	}

	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}
}
