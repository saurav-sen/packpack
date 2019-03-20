package com.squill.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class NotifyMsgs {

	private List<NotifyMsg> msgs;

	public List<NotifyMsg> getMsgs() {
		if(msgs == null) {
			msgs = new LinkedList<NotifyMsg>();
		}
		return msgs;
	}

	public void setMsgs(List<NotifyMsg> msgs) {
		this.msgs = msgs;
	}
}
