package com.pack.pack.client.api.test;

import java.io.File;

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

	private static final String NAME = "Saurav Sen";
	//private static final String LOCALITY = "Sanath Nagar";
	private static final String CITY = "Hyderabad";
	//private static final String STATE = "Telangana";
	//private static final String COUNTRY = "India";
	private static final String DOB = "27/12/1981";
	private static final String PROFILE_PICTURE = "D:/Saurav/myphoto.jpg";

	public void signUp() {
		try {
			API api = APIBuilder
					.create()
					.setAction(COMMAND.SIGN_UP)
					.addApiParam(APIConstants.User.Register.NAME, NAME)
					.addApiParam(APIConstants.User.Register.EMAIL, SignInUtil.USERNAME)
					.addApiParam(APIConstants.User.Register.PASSWORD, SignInUtil.PASSWORD)
					//.addApiParam(APIConstants.User.Register.LOCALITY, LOCALITY)
					.addApiParam(APIConstants.User.Register.CITY, CITY)
					//.addApiParam(APIConstants.User.Register.STATE, STATE)
					//.addApiParam(APIConstants.User.Register.COUNTRY, COUNTRY)
					.addApiParam(APIConstants.User.Register.DOB, DOB)
					//.addApiParam(APIConstants.User.Register.PROFILE_PICTURE, null)
					/*.addApiParam(APIConstants.User.Register.PROFILE_PICTURE,
							new File(PROFILE_PICTURE))*/.build();
			api.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new SignUpUserTest().signUp();
	}
}