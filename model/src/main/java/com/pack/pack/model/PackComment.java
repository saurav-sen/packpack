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
public class PackComment extends IdentifiableObject {

	@Property("from")
	private ObjectId from;
	
	@Property("targetId")
	private ObjectId targetId;
	
	@Property(value="type", concreteClass=TargetType.class)
	private TargetType targetType;

	public ObjectId getFrom() {
		return from;
	}

	public void setFrom(ObjectId from) {
		this.from = from;
	}

	public ObjectId getTargetId() {
		return targetId;
	}

	public void setTargetId(ObjectId targetId) {
		this.targetId = targetId;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}
}