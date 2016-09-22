package com.pack.pack.oauth.token;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Saurav
 *
 */
public class TokenInfo {

	private String token;
	private String secret;
	private String consumerKey;
	private String callbackUrl;
	private SimplePrinciple principal;
	private List<String> roles;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getConsumerKey() {
		return consumerKey;
	}
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	public String getCallbackUrl() {
		return callbackUrl;
	}
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	public SimplePrinciple getPrincipal() {
		return principal;
	}
	public void setPrincipal(SimplePrinciple principal) {
		this.principal = principal;
	}
	public List<String> getRoles() {
		if(roles == null) {
			roles = new ArrayList<String>();
		}
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
