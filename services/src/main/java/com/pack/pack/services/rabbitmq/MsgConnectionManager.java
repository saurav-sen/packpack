package com.pack.pack.services.rabbitmq;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class MsgConnectionManager {
	
	/*private ConnectionFactory connectionFactory;
	
	private ThreadLocal<MsgConnection> context = new ThreadLocal<MsgConnection>();
	
	public MsgConnectionManager() {
		connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("localhost");
	}

	public MsgConnection openConnection() throws IOException, TimeoutException {
		MsgConnection msgConnection = context.get();
		if(msgConnection == null) {
			msgConnection = new MsgConnection();
			Connection connection = connectionFactory.newConnection();
			msgConnection.setConnection(connection);
			context.set(msgConnection);
		}
		return msgConnection;
	}
	
	public void closeConnection() throws IOException, TimeoutException {
		MsgConnection msgConnection = context.get();
		if(msgConnection != null) {
			msgConnection.close();
			context.remove();
		}
	}*/
}