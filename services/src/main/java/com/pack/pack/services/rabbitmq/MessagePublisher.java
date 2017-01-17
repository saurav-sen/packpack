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
public class MessagePublisher {
	
	/*@Autowired
	private MsgConnectionManager connectionManager;
	
	private static Logger logger = LoggerFactory.getLogger(MessagePublisher.class);
	
	public void forwardPack(FwdPack fwdPack, User user) throws PackPackException {
		try {
			MsgConnection connection = connectionManager.openConnection();
			Channel channel = connection.getChannel();
			String msg_queue = user.getId() + "_" + user.getUsername();
			channel.queueDeclare(msg_queue, false, false, false, null);
			String message = JSONUtil.serialize(fwdPack);
			BasicProperties props = new BasicProperties(null, null, null, 0, 0,
					null, Constants.REPLY_TO_USER_PREFIX + user.getId(), null, null, null, null,
					user.getUsername(), null, null);
			channel.basicPublish("", msg_queue, props, message.getBytes());
		} catch (IOException e) {
			new PackPackException("TODO", e.getMessage(), e);
		} catch (TimeoutException e) {
			new PackPackException("TODO", e.getMessage(), e);
		}
	}
	
	public void notifyPackModify(FwdPack fwdPack, Topic topic, User fromUser) throws PackPackException {
		MsgConnection connection = null;
		try {
			connection = connectionManager.openConnection();
			Channel channel = connection.getChannel();
			String exchange_name = "topic_" + topic.getId() + "_" + topic.getName();
			logger.debug("RabbitMQ exchange name: " + exchange_name);
			channel.exchangeDeclare(exchange_name, "fanout");
			String message = JSONUtil.serialize(fwdPack);
			logger.debug("message to RabbitMQ: " + message);
			BasicProperties props = new BasicProperties(null, null, null, 0, 0,
					null, Constants.REPLY_TO_TOPIC_PREFIX + topic.getId(),
					null, null, null, null, null, null, null);
			channel.basicPublish(exchange_name, "", null, message.getBytes());
		} catch (IOException e) {
			new PackPackException("TODO", e.getMessage(), e);
		} catch (TimeoutException e) {
			new PackPackException("TODO", e.getMessage(), e);
		} finally {
			try {
				connectionManager.closeConnection();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void broadcast(BroadcastPack broadcastPack) throws PackPackException {
		FwdPack fwdPack = broadcastPack.getFwdPack();
		BroadcastCriteria criteria = broadcastPack.getCriteria();
		try {
			MsgConnection connection = connectionManager.openConnection();
			Channel channel = connection.getChannel();
			String exchange_name = criteria.getCity() + "_"
					+ criteria.getState() + "_" + criteria.getCountry();
			channel.exchangeDeclare(exchange_name, "fanout");
			String message = JSONUtil.serialize(fwdPack);
			channel.basicPublish(exchange_name, "", null, message.getBytes());
		} catch (IOException e) {
			throw new PackPackException("", e.getMessage(), e);
		} catch (TimeoutException e) {
			throw new PackPackException("", e.getMessage(), e);
		}
	}*/
}