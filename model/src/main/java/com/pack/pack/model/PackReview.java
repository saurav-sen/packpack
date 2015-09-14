package com.pack.pack.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

/**
 * 
 * @author Saurav
 *
 */
@Entity
public class PackReview extends IdentifiableObject {

	@Property("rating")
	private Float rating;
	
	@Property("comment")
	private String comment;
	
	@Property("userId")
	private ObjectId userId;
	
	@Property("packId")
	private ObjectId packId;

	public Float getRating() {
		return rating;
	}

	public void setRating(Float rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public ObjectId getPackId() {
		return packId;
	}

	public void setPackId(ObjectId packId) {
		this.packId = packId;
	}
}