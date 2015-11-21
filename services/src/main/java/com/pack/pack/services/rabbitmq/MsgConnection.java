package com.pack.pack.services.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 
 * @author Saurav
 *
 */
public class MsgConnection {

	private Connection connection;
	
	private Channel channel;

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Channel getChannel() throws IOException {
		if(channel == null) {
			channel = connection.createChannel();
		}
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public void close() throws IOException, TimeoutException {
		if(channel != null && channel.isOpen()) {
			channel.close();
			channel = null;
		}
		if(connection != null && connection.isOpen()) {
			connection.close();
			connection = null;
		}
	}
}