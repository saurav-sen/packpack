package com.pack.pack.model;

import java.sql.Timestamp;
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
public class PackItem extends IdentifiableObject {

	@Property("parentId")
	private ObjectId parentId;
	
	@Property("imgUrl")
	private String imgUrl;
	
	@Property("story")
	private String story;
	
	@Property("creator")
	private ObjectId creator;
	
	private List<PackComment> packComments;
	
	private List<String> tags;
	
	@Property("title")
	private String title;
	
	@Property("avgRating")
	private Float avgRating;
	
	@Property("creationTime")
	private Timestamp creationTime;

	public ObjectId getParentId() {
		return parentId;
	}

	public void setParentId(ObjectId parentId) {
		this.parentId = parentId;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public ObjectId getCreator() {
		return creator;
	}

	public void setCreator(ObjectId creator) {
		this.creator = creator;
	}

	public List<PackComment> getPackComments() {
		if(packComments == null) {
			packComments = new ArrayList<PackComment>(20);
		}
		return packComments;
	}

	public void setPackComments(List<PackComment> packComments) {
		this.packComments = packComments;
	}

	public List<String> getTags() {
		if(tags == null) {
			tags = new ArrayList<String>();
		}
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Float getAvgRating() {
		return avgRating;
	}

	public void setAvgRating(Float avgRating) {
		this.avgRating = avgRating;
	}

	public Timestamp getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Timestamp creationTime) {
		this.creationTime = creationTime;
	}
}