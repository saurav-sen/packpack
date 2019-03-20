package com.squill.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class NotifyMsg {

	private String msg;
	
	private boolean expired;
	
	private long timestamp;
	
	private List<String> extraInfoList;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof NotifyMsg) {
			return msg.equals(((NotifyMsg)obj).msg);
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return (this.getClass().getName() + msg).hashCode();
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public List<String> getExtraInfoList() {
		if(extraInfoList == null) {
			extraInfoList = new LinkedList<String>();
		}
		return extraInfoList;
	}

	public void setExtraInfoList(List<String> extraInfoList) {
		this.extraInfoList = extraInfoList;
	}
}
