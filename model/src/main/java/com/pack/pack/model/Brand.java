package com.pack.pack.model;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class Brand extends CouchDbDocument{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2619689006084230815L;

	private String compnayName;
	
	private String logoImageId;
	
	private String contact;

	public String getCompnayName() {
		return compnayName;
	}

	public void setCompnayName(String compnayName) {
		this.compnayName = compnayName;
	}

	public String getLogoImageId() {
		return logoImageId;
	}

	public void setLogoImageId(String logoImageId) {
		this.logoImageId = logoImageId;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}
}