package com.pack.pack.oauth.token;

/**
 * 
 * @author Saurav
 *
 */
public interface ITokenStateChangeListener {

	public void stateChanged(Token token, TokenState state);
}