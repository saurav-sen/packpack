package com.pack.pack.services.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.User;
import com.pack.pack.services.exception.PackPackException;
import com.rabbitmq.client.Channel;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class MessageSubscriber {
	
	@Autowired
	private MsgConnectionManager connectionManager;

	public void subscribeToChannel(User user) throws PackPackException {
		try {
			MsgConnection connection = connectionManager.openConnection();
			Channel channel = connection.getChannel();
			String msg_queue = user.getId() + "_" + user.getUsername();
			channel.queueDeclare(msg_queue, false, false, false, null);
			channel.basicConsume(msg_queue, false, new MessageHandler(channel, user.getId()));
		} catch (IOException e) {
			throw new PackPackException("", e.getMessage(), e);
		} catch (TimeoutException e) {
			throw new PackPackException("", e.getMessage(), e);
		}
	}
	
	/*public void subscribeToGroup(Group group, User user) throws PackPackException {
		try {
			MsgConnection connection = connectionManager.openConnection();
			Channel channel = connection.getChannel();
			String exchange_name = group.getId() + "_" + group.getName();
			channel.exchangeDeclare(exchange_name, "fanout");
			String msg_queue = channel.queueDeclare().getQueue();
			channel.queueBind(msg_queue, exchange_name, "");
			channel.basicConsume(msg_queue, false, new MessageHandler(channel, user.getId()));
		} catch (IOException e) {
			throw new PackPackException("", e.getMessage(), e);
		} catch (TimeoutException e) {
			throw new PackPackException("", e.getMessage(), e);
		}
	}*/
	
	//public void sub
}