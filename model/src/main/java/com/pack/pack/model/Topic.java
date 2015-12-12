package com.pack.pack.model;

import java.util.LinkedList;
import java.util.List;

import org.ektorp.support.CouchDbDocument;

/**
 * 
 * @author Saurav
 *
 */
public class Topic extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5516874502198452029L;

	private String ownerId;
	
	private String name;
	
	private String description;
	
	private long followers;
	
	private List<Pack> packs;

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getFollowers() {
		return followers;
	}

	public void setFollowers(long followers) {
		this.followers = followers;
	}

	public List<Pack> getPacks() {
		if(packs == null) {
			packs = new LinkedList<Pack>();
		}
		return packs;
	}

	public void setPacks(List<Pack> packs) {
		this.packs = packs;
	}
}