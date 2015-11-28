package com.pack.pack.services.rabbitmq;

import java.io.IOException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;


/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class MessageHandler extends DefaultConsumer {

	public MessageHandler(Channel channel) {
		super(channel);
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope,
			BasicProperties properties, byte[] body) throws IOException {
		super.handleDelivery(consumerTag, envelope, properties, body);
	}
	
	@Override
	public void handleCancelOk(String consumerTag) {
		super.handleCancelOk(consumerTag);
	}
}