package com.pack.pack.event;

import com.pack.pack.message.FwdPack;
import com.pack.pack.model.User;

/**
 * 
 * @author Saurav
 *
 */
public interface MsgEvent {
	
	/**
	 * 
	 * @return
	 */
	public User getTargetUser();
	
	/**
	 * 
	 * @return
	 */
	public MsgEventTargetType getTargetType();
	
	/**
	 * 
	 * @return
	 */
	public FwdPack getMessage();
}