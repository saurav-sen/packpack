package com.pack.pack.services.sms;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.message.FwdPack;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class SMSSender {

	public void forwardPack(FwdPack fwdPack, String phoneNo) throws PackPackException {
		
	}
}