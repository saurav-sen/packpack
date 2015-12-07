package com.pack.pack.rest.api.oauth.token;

/**
 * 
 * @author Saurav
 *
 */
public interface ITokenStateChangeListener {

	public void stateChanged(Token token, TokenState state);
}