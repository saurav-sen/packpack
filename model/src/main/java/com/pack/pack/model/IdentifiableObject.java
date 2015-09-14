package com.pack.pack.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Version;

/**
 * 
 * @author Saurav
 *
 */
public abstract class IdentifiableObject {

	@Id
	@Property("id")
	private ObjectId id;
	
	@Version
	@Property("version")
	private Long version;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}