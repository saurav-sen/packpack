package com.pack.pack.model;

import java.sql.Timestamp;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

/**
 * 
 * @author Saurav
 *
 */
@Entity
public class PackPromotion extends IdentifiableObject {

	@Property("packId")
	private ObjectId packId;
	
	@Property("brandId")
	private ObjectId branndId;
	
	private Timestamp expiry;

	public ObjectId getPackId() {
		return packId;
	}

	public void setPackId(ObjectId packId) {
		this.packId = packId;
	}

	public ObjectId getBranndId() {
		return branndId;
	}

	public void setBranndId(ObjectId branndId) {
		this.branndId = branndId;
	}

	public Timestamp getExpiry() {
		return expiry;
	}

	public void setExpiry(Timestamp expiry) {
		this.expiry = expiry;
	}
}