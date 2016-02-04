package com.pack.pack.services.email;

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
public class EmailSender {

	public void forwardPack(FwdPack fwdPack, String email) throws PackPackException {
		
	}
}