package com.pack.pack.model.web.notification;

/**
 * 
 * @author Saurav
 *
 */
public class FeedMsg {
	
	private String title;
	
	private String timestamp;
	
	private String key;
	
	private FeedMsgType msgType;
	
	private String dataObj;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public FeedMsgType getMsgType() {
		return msgType;
	}

	public void setMsgType(FeedMsgType msgType) {
		this.msgType = msgType;
	}

	public String getDataObj() {
		return dataObj;
	}

	public void setDataObj(String dataObj) {
		this.dataObj = dataObj;
	}

}
