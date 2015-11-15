package com.pack.pack.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.ektorp.docref.DocumentReferences;
import org.ektorp.docref.FetchType;
import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class Pack extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3991470211498256682L;

	private String imgUrl;
	
	private String story;
	
	private String creatorId;
	
	@DocumentReferences(fetch=FetchType.LAZY, descendingSortOrder=true, 
			orderBy="timestamp", backReference="packId")
	private List<PackComment> packComments;
	
	private List<String> tags;
	
	private String title;
	
	private Float avgRating;
	
	private Timestamp creationTime;

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
	
	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
}