package com.pack.pack.client.api.test;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;

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
					.addApiParam(APIConstants.User.Register.PASSWORD, TestDataSet.getInstance().getUserPassword(session.getSeqNo()))
					.addApiParam(APIConstants.User.Register.LOCALITY, TestDataSet.getInstance().getUserLocality(session.getSeqNo()))
					.addApiParam(APIConstants.User.Register.CITY, TestDataSet.getInstance().getUserCity(session.getSeqNo()))
					.addApiParam(APIConstants.User.Register.COUNTRY, TestDataSet.getInstance().getUserCountry(session.getSeqNo()))
					.addApiParam(APIConstants.User.Register.DOB, TestDataSet.getInstance().getUserDOB(session.getSeqNo()))
					.addApiParam(APIConstants.User.Register.VERIFIER, TestDataSet.getInstance().getSpecialSignUpVerifierCode())
					.build();
			api.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*public static void main(String[] args) {
		new SignUpUserTest().signUp();
	}*/
}