package com.pack.pack.oauth.token;

import java.security.Principal;

/**
 * 
 * @author Saurav
 *
 */
public class SimplePrinciple implements Principal {
	
	private String name;
	
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
