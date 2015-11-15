package com.pack.pack.model;

import java.sql.Timestamp;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class PackComment extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1839844005905686366L;

	private String fromUser;
	
	private String comment;
	
	private Timestamp timestamp;
	
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

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getPackId() {
		return packId;
	}

	public void setPackId(String packId) {
		this.packId = packId;
	}
}