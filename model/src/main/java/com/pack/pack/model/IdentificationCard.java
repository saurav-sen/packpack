package com.pack.pack.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

/**
 * 
 * @author Saurav
 *
 */
@Entity
public class IdentificationCard extends IdentifiableObject {

	@Property("idNumber")
	private String idNumber;
	
	@Property("nameOnCard")
	private String nameOnCard;
	
	@Property("cardType")
	private String cardType;

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getNameOnCard() {
		return nameOnCard;
	}

	public void setNameOnCard(String nameOnCard) {
		this.nameOnCard = nameOnCard;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
}