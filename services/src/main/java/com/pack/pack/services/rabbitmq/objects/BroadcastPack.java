package com.pack.pack.services.rabbitmq.objects;

import com.pack.pack.message.FwdPack;

/**
 * 
 * @author Saurav
 *
 */
public class BroadcastPack {

	private FwdPack fwdPack;
	
	private BroadcastCriteria criteria;

	public FwdPack getFwdPack() {
		return fwdPack;
	}

	public void setFwdPack(FwdPack fwdPack) {
		this.fwdPack = fwdPack;
	}

	public BroadcastCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(BroadcastCriteria criteria) {
		this.criteria = criteria;
	}
}