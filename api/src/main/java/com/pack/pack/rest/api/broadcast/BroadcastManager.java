package com.pack.pack.rest.api.broadcast;

import java.util.HashMap;
import java.util.Map;

import org.glassfish.jersey.media.sse.EventOutput;
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
	
	private Map<String, SseBroadcaster> groupBroadcatsers = new HashMap<String, SseBroadcaster>();
	
	private Map<String, SseBroadcaster> userSseMap = new HashMap<String, SseBroadcaster>();
	
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
	
	public SseBroadcaster getSseBroadcaster(String id) {
		return groupBroadcatsers.get(id);
	}
	
	public EventOutput registerTopicBroadcaster(String topicId) {
		SseBroadcaster sseBroadcaster = getSseBroadcaster(topicId);
		if(sseBroadcaster == null) {
			sseBroadcaster = new SseBroadcaster();
			groupBroadcatsers.put(topicId, sseBroadcaster);
		}
		EventOutput eventOutput = new EventOutput();
		sseBroadcaster.add(eventOutput);
		return eventOutput;
	}
	
	public SseBroadcaster getSseBroadCasterForUser(String userId) {
		return userSseMap.get(userId);
	}
	
	public EventOutput registerUserSseBroadcaster(String userId) {
		SseBroadcaster sseBroadcaster = getSseBroadCasterForUser(userId);
		if(sseBroadcaster == null) {
			sseBroadcaster = new SseBroadcaster();
			userSseMap.put(userId, sseBroadcaster);
		}
		EventOutput eventOutput = new EventOutput();
		sseBroadcaster.add(eventOutput);
		return eventOutput;
	}
}