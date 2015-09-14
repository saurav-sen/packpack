package com.pack.pack.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

/**
 * 
 * @author Saurav
 *
 */
@Entity
public class Subscription extends IdentifiableObject {

	@Property("userId")
	private ObjectId userId;
	
	private List<String> tags;

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public List<String> getTags() {
		if(tags == null) {
			tags = new ArrayList<String>(10);
		}
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}