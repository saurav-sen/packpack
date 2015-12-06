package com.pack.pack.rest.api.broadcast;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

import com.pack.pack.services.rabbitmq.objects.BroadcastCriteria;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("/broadcast")
public class BroadcastChannel {

	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	@Consumes(MediaType.APPLICATION_JSON)
	public EventOutput registerToChannel(BroadcastCriteria channel) {
		SseBroadcaster sseBroadCaster = BroadcastManager.INSTANCE
				.getSseBroadcaster(channel);
		EventOutput eventOutput = new EventOutput();
		sseBroadCaster.add(eventOutput);
		return eventOutput;
	}
}