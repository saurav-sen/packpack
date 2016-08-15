package com.pack.pack;

import java.io.InputStream;
import java.util.List;

import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JUser;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public interface IUserService {

	public JUser registerNewUser(String name, String email, String password,
			String city, String country, String dob, InputStream profilePicture,
			String profilePictureFileName) throws PackPackException;
	
	public JStatus editUserFollowedCategories(String userId,
			List<String> categories) throws PackPackException;

	public JUser uploadProfilePicture(String userId,
			InputStream profilePicture, String profilePictureFileName)
			throws PackPackException;
	
	public JUser findUserById(String userId) throws PackPackException;
}