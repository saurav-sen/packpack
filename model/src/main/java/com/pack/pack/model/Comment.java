package com.pack.pack.model;

import org.ektorp.support.CouchDbDocument;
import org.joda.time.DateTime;

/**
 * 
 * @author Saurav
 *
 */
public class Comment extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1839844005905686366L;

	private String fromUser;
	
	private String comment;
	
	private DateTime dateTime;
	
	private String packId;

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPackId() {
		return packId;
	}

	public void setPackId(String packId) {
		this.packId = packId;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}
}