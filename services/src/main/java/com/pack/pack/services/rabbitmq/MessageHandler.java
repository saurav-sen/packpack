package com.pack.pack.services.rabbitmq;

import java.io.IOException;

import com.pack.pack.event.MsgEvent;
import com.pack.pack.model.User;
import com.pack.pack.services.registry.EventManager;
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

	private User user;
	
	public MessageHandler(Channel channel, User user) {
		super(channel);
		this.user = user;
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope,
			BasicProperties properties, byte[] body) throws IOException {
		super.handleDelivery(consumerTag, envelope, properties, body);
		MsgEvent event = null; //TODO
		try {
			EventManager.INSTANCE.fireEvent(event);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleCancelOk(String consumerTag) {
		super.handleCancelOk(consumerTag);
	}

	public User getUser() {
		return user;
	}
}