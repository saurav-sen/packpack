package com.pack.pack.services.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.Pack;
import com.pack.pack.model.User;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class MessaingService {
	
	@Autowired
	private MsgConnectionManager connectionManager;
	
	public void postPack(Pack pack, User user) {
		
	}
}