package com.pack.pack;

import java.io.InputStream;

import com.pack.pack.model.web.JUser;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public interface IUserService {

	/**
	 * 
	 * @param name
	 * @param email
	 * @param password
	 * @param city
	 * @param country
	 * @param dob
	 * @param profilePicture
	 * @param profilePictureFileName
	 * @return
	 * @throws PackPackException
	 */
	public JUser registerNewUser(String name, String email, String password,
			String city, String country, String dob,
			InputStream profilePicture, String profilePictureFileName)
			throws PackPackException;
	
	/**
	 * 
	 * @param name
	 * @param email
	 * @param password
	 * @param longitude
	 * @param latitude
	 * @param profilePicture
	 * @param profilePictureFileName
	 * @return
	 * @throws PackPackException
	 */
	public JUser registerNewUser(String name, String email, String password,
			double longitude, double latitude, InputStream profilePicture,
			String profilePictureFileName) throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param profilePicture
	 * @param profilePictureFileName
	 * @return
	 * @throws PackPackException
	 */
	public JUser uploadProfilePicture(String userId,
			InputStream profilePicture, String profilePictureFileName)
			throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws PackPackException
	 */
	public JUser findUserById(String userId) throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param key
	 * @param value
	 * @return
	 * @throws PackPackException
	 */
	public JUser updateUserSettings(String userId, String key, String value)
			throws PackPackException;

	/**
	 * 
	 * @param userName
	 * @return -- Returns true if user with 'userName' already registered with
	 *         SQUILL or false otherwise.
	 * @throws PackPackException
	 */
	public boolean checkIfUserNameExists(String userName)
			throws PackPackException;
	
	/**
	 * 
	 * @param userName
	 * @param passwd
	 * @throws PackPackException
	 */
	public void updateUserPassword(String userName, String passwd)
			throws PackPackException;
}