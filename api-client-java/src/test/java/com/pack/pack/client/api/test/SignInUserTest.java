package com.pack.pack.client.api.test;


/**
 * 
 * @author Saurav
 *
 */
public class SignInUserTest {


	public void signIn() {
		try {
			SignInUtil.signIn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}