package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JUser;

/**
 * 
 * @author Saurav
 *
 */
public class SignUpUserTest {

	public void signUp(TestSession session) {
		try {
			API api = APIBuilder
					.create(session.getBaseUrl())
					.setAction(COMMAND.SIGN_UP)
					.addApiParam(APIConstants.User.Register.NAME, TestDataSet.getInstance().getUserFullName(session.getSeqNo()))
					.addApiParam(APIConstants.User.Register.EMAIL, TestDataSet.getInstance().getUserEmail(session.getSeqNo()))
					/*.addApiParam(APIConstants.User.Register.PASSWORD, TestDataSet.getInstance().getUserPassword(session.getSeqNo()))*/
					.addApiParam(APIConstants.User.Register.LONGITUDE, TestDataSet.getInstance().getUserLongitude(session.getSeqNo()))
					.addApiParam(APIConstants.User.Register.LATITUDE, TestDataSet.getInstance().getUserLatitude(session.getSeqNo()))
					.addApiParam(APIConstants.User.Register.VERIFIER, TestDataSet.getInstance().getSpecialSignUpVerifierCode())
					.build();
			JUser user = (JUser) api.execute();
			System.out.println(user.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*public static void main(String[] args) {
		new SignUpUserTest().signUp();
	}*/
}