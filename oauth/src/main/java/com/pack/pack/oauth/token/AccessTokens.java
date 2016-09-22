package com.pack.pack.oauth.token;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class AccessTokens {
	
	private List<AccessTokenInfo> tokens;

	public List<AccessTokenInfo> getTokens() {
		if (tokens == null) {
			tokens = new LinkedList<AccessTokenInfo>();
		}
		return tokens;
	}

	public void setTokens(List<AccessTokenInfo> tokens) {
		this.tokens = tokens;
	}
}