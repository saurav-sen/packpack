package com.pack.pack.client.api.test;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;

public class ResetPasswordTest {
	
	public void testIssueVerifier() throws Exception {
		API api = APIBuilder
				.create(BASE_URL)
				.setAction(COMMAND.ISSUE_PASSWD_RESET_LINK)
				.addApiParam(APIConstants.User.USERNAME,
						"sourabhnits@gmail.com").build();
		api.execute();
	}

	public void testReset() throws Exception {
		API api = APIBuilder
				.create(BASE_URL)
				.setAction(COMMAND.RESET_USER_PASSWD)
				.addApiParam(APIConstants.User.USERNAME,
						"sourabhnits@gmail.com")
				.addApiParam(APIConstants.User.PasswordReset.VERIFIER_CODE,
						"1834124918")
				.addApiParam(APIConstants.User.PasswordReset.NEW_PASSWORD,
						"p@ssword").build();
		api.execute();
	}
	
	public static void main(String[] args) throws Exception {
		//new ResetPasswordTest().testIssueVerifier();
		//new ResetPasswordTest().testReset();
	}
}
