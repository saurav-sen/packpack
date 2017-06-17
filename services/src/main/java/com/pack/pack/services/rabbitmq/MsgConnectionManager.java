package com.pack.pack.services.rabbitmq;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class MsgConnectionManager {
	
	/*public static void main(String[] args) throws Exception {
		MsgConnectionManager m = new MsgConnectionManager();
		m.openConnection();
		m.closeConnection();
		System.out.println("DONE");
	}*/
	
	private ConnectionFactory connectionFactory;
	
	private MsgConnection msgConnection;
	
	private ThreadLocal<MsgConnection> context = new ThreadLocal<MsgConnection>();
	
	public MsgConnectionManager() {
		connectionFactory = new ConnectionFactory();
		//connectionFactory.setHost("localhost");
	}

	public MsgConnection openConnection() throws IOException, TimeoutException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		connectionFactory.setUri("amqp://bbjoaswu:k8hcZHx9zv60vwKu3rLXjzKjH63lyc1s@sidewinder.rmq.cloudamqp.com/bbjoaswu");//SystemPropertyUtil.getCLoudAMQP_Uri());
		//MsgConnection msgConnection = context.get();
		if(msgConnection == null) {
			msgConnection = new MsgConnection();
			Connection connection = connectionFactory.newConnection();
			msgConnection.setConnection(connection);
			context.set(msgConnection);
		}
		return msgConnection;
	}
	
	public synchronized void closeConnection() throws IOException, TimeoutException {
		MsgConnection msgConnection = context.get();
		if(msgConnection != null) {
			msgConnection.close();
			context.remove();
			msgConnection = null;
		}
	}
}