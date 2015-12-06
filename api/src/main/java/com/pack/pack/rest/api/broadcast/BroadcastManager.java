package com.pack.pack.rest.api.broadcast;

import java.util.HashMap;
import java.util.Map;

import org.glassfish.jersey.media.sse.SseBroadcaster;

import com.pack.pack.services.rabbitmq.objects.BroadcastCriteria;

/**
 * 
 * @author Saurav
 *
 */
public class BroadcastManager {

	public static final BroadcastManager INSTANCE = new BroadcastManager();
	
	private Map<BroadcastCriteria, SseBroadcaster> sseBroadcasters = new HashMap<BroadcastCriteria, SseBroadcaster>();
	
	private BroadcastManager() {
		sseBroadcasters = new HashMap<BroadcastCriteria, SseBroadcaster>();
	}
	
	public SseBroadcaster getSseBroadcaster(BroadcastCriteria criteria) {
		SseBroadcaster sseBroadcaster = sseBroadcasters.get(criteria);
		if(sseBroadcaster == null) {
			sseBroadcaster = new SseBroadcaster();
			sseBroadcasters.put(criteria, sseBroadcaster);
		}
		return sseBroadcaster;
	}
}