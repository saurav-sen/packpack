package com.pack.pack.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

/**
 * 
 * @author Saurav
 *
 */
@Entity
public class Brand extends IdentifiableObject {

	@Property("companyName")
	private String companyName;
	
	@Property("logoURL")
	private String logoURL;
	
	@Property("aboutText")
	private String aboutText;
	
	@Property("promoterName")
	private String promoterName;
	
	@Property("promoterEmail")
	private String promoterEmail;
	
	@Property("promoterPhone")
	private String promoterPhone;
	
	@Property(value="promoterID", concreteClass=IdentificationCard.class)
	private IdentificationCard promoterID;
	
	@Property("ownerName")
	private String ownerName;
	
	@Property("ownerEmail")
	private String ownerEmail;
	
	@Property("ownerPhone")
	private String ownerPhone;
	
	@Property(value="ownerID", concreteClass=IdentificationCard.class)
	private IdentificationCard ownerID;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public String getAboutText() {
		return aboutText;
	}

	public void setAboutText(String aboutText) {
		this.aboutText = aboutText;
	}

	public String getPromoterName() {
		return promoterName;
	}

	public void setPromoterName(String promoterName) {
		this.promoterName = promoterName;
	}

	public String getPromoterEmail() {
		return promoterEmail;
	}

	public void setPromoterEmail(String promoterEmail) {
		this.promoterEmail = promoterEmail;
	}

	public String getPromoterPhone() {
		return promoterPhone;
	}

	public void setPromoterPhone(String promoterPhone) {
		this.promoterPhone = promoterPhone;
	}

	public IdentificationCard getPromoterID() {
		return promoterID;
	}

	public void setPromoterID(IdentificationCard promoterID) {
		this.promoterID = promoterID;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public String getOwnerPhone() {
		return ownerPhone;
	}

	public void setOwnerPhone(String ownerPhone) {
		this.ownerPhone = ownerPhone;
	}

	public IdentificationCard getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(IdentificationCard ownerID) {
		this.ownerID = ownerID;
	}
}