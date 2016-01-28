package com.pack.pack.message.listeners;

import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;

import com.pack.pack.event.IEventListener;
import com.pack.pack.event.MsgEvent;
import com.pack.pack.event.MsgEventType;
import com.pack.pack.message.FwdPack;
import com.pack.pack.rest.api.broadcast.BroadcastManager;

/**
 * 
 * @author Saurav
 *
 */
public class EventListener implements IEventListener {

	@Override
	public void handleEvent(MsgEvent event) {
		MsgEventType msgEventType = event.getEventType();
		FwdPack fwdPack = event.getMessage();
		SseBroadcaster broadCaster = null;
		switch(msgEventType) {
		case USER:
			String userId = event.getOriginEntityId();
			broadCaster = BroadcastManager.INSTANCE.getSseBroadCasterForUser(userId);
			break;
		case TOPIC:
			String topicId = event.getOriginEntityId();
			broadCaster = BroadcastManager.INSTANCE.getSseBroadcaster(topicId);
			break;
		case BROADCAST:
			break;
		}
		if(broadCaster != null) {
			OutboundEvent outboundEvent = new OutboundEvent.Builder()
					.name("pack").mediaType(MediaType.APPLICATION_JSON_TYPE)
					.data(FwdPack.class, fwdPack).build();
			broadCaster.broadcast(outboundEvent);
		}
	}
}