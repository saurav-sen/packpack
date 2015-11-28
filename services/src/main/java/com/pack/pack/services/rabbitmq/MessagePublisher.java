package com.pack.pack.services.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.User;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.rabbitmq.objects.FwdPack;
import com.pack.pack.util.JSONUtil;
import com.rabbitmq.client.Channel;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class MessagePublisher {
	
	@Autowired
	private MsgConnectionManager connectionManager;
	
	public void forwardPack(FwdPack fwdPack, User user) throws PackPackException {
		try {
			MsgConnection connection = connectionManager.openConnection();
			Channel channel = connection.getChannel();
			String msg_queue = user.getId() + "_" + user.getUsername();
			channel.queueDeclare(msg_queue, false, false, false, null);
			String message = JSONUtil.serialize(fwdPack);
			channel.basicPublish("", msg_queue, null, message.getBytes());
		} catch (IOException e) {
			throw new PackPackException("", e.getMessage(), e);
		} catch (TimeoutException e) {
			throw new PackPackException("", e.getMessage(), e);
		}
	}
}