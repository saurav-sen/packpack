package com.pack.pack;

import java.io.InputStream;

import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JUser;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public interface IUserService {

	public JStatus registerNewUser(String name, String email, String password,
			String city, String country, String state, String locality,
			String dob, InputStream profilePicture,
			String profilePictureFileName) throws PackPackException;

	public JUser uploadProfilePicture(String userId,
			InputStream profilePicture, String profilePictureFileName)
			throws PackPackException;
	
	public JUser findUserById(String userId) throws PackPackException;
}