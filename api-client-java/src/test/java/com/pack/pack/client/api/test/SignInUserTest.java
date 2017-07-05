package com.pack.pack.client.api.test;


/**
 * 
 * @author Saurav
 *
 */
public class SignInUserTest {


	public String signIn(TestSession session) {
		try {
			return SignInUtil.signIn(session);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}