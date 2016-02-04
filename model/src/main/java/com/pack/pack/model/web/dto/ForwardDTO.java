package com.pack.pack.model.web.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class ForwardDTO {

	private String fromUserId;
	
	private List<PackReceipent> receipents;
	
	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public List<PackReceipent> getReceipents() {
		if(receipents == null) {
			receipents = new ArrayList<PackReceipent>(5);
		}
		return receipents;
	}

	public void setReceipents(List<PackReceipent> receipents) {
		this.receipents = receipents;
	}
}