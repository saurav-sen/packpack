package com.pack.pack.services;

import java.util.List;

import com.pack.pack.model.Group;
import com.pack.pack.model.User;
import com.pack.pack.services.exception.PackException;

/**
 * 
 * @author Saurav
 *
 */
public interface GroupService extends IService {

	/**
	 * 
	 * @param members
	 * @param name
	 * @return
	 * @throws PackException
	 */
	public Group createGroup(List<User> members, String name)
			throws PackException;

	/**
	 * 
	 * @param g1
	 * @param g2
	 * @param name
	 * @return
	 * @throws PackException
	 */
	public Group joinGroup(Group g1, Group g2, String name)
			throws PackException;

	/**
	 * 
	 * @param g
	 * @param user
	 * @throws PackException
	 */
	public void unsubscribeFromGroup(Group g, User user) throws PackException;
}