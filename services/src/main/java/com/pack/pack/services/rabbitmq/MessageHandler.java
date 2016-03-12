package com.pack.pack.services.rabbitmq;

import java.io.IOException;

import com.pack.pack.event.MsgEvent;
import com.pack.pack.event.MsgEventType;
import com.pack.pack.message.FwdPack;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.EventManager;
import com.pack.pack.util.JSONUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * 
 * @author Saurav
 *
 */
public class MessageHandler extends DefaultConsumer {

	private String userId;
	
	public MessageHandler(Channel channel, String userId) {
		super(channel);
		this.userId = userId;
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope,
			BasicProperties properties, byte[] body) throws IOException {
		super.handleDelivery(consumerTag, envelope, properties, body);
		String message = new String(body);
		FwdPack fwdPack;
		try {
			fwdPack = JSONUtil.deserialize(message, FwdPack.class);
		} catch (PackPackException e1) {
			throw new RuntimeException(e1);
		}
		String replyTo = properties != null ? properties.getReplyTo() : null;
		String originEntityId = null;
		MsgEventType evtType = MsgEventType.BROADCAST;
		if(replyTo != null) {
			if(replyTo.startsWith(Constants.REPLY_TO_TOPIC_PREFIX)) {
				evtType = MsgEventType.TOPIC;
				originEntityId = replyTo.substring(Constants.REPLY_TO_TOPIC_PREFIX.length());
			}
			else if(replyTo.startsWith(Constants.REPLY_TO_USER_PREFIX)) {
				evtType = MsgEventType.USER;
				originEntityId = replyTo.substring(Constants.REPLY_TO_USER_PREFIX.length());
			}
		}
		MsgEvent event = new MsgEventImpl(originEntityId, evtType, fwdPack); 
		try {
			EventManager.INSTANCE.fireEvent(event);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleCancelOk(String consumerTag) {
		super.handleCancelOk(consumerTag);
	}

	private class MsgEventImpl implements MsgEvent {
		
		private String originEntityId;
		
		private MsgEventType eventType;
		
		private FwdPack message;
		
		MsgEventImpl(String originEntityId, MsgEventType eventType, FwdPack message) {
			this.originEntityId = originEntityId;
			this.eventType = eventType;
			this.message = message;
		}

		@Override
		public String getTargetUserId() {
			return userId;
		}

		@Override
		public String getOriginEntityId() {
			return originEntityId;
		}

		@Override
		public MsgEventType getEventType() {
			return eventType;
		}

		@Override
		public FwdPack getMessage() {
			return message;
		}
	}
}