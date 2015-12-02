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
	public String getTargetUserId();
	
	/**
	 * 
	 * @return
	 */
	public String getOriginEntityId();
	
	/**
	 * 
	 * @return
	 */
	public MsgEventType getEventType();
	
	/**
	 * 
	 * @return
	 */
	public FwdPack getMessage();
}