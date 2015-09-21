package com.pack.pack.services;

import java.util.List;

import com.pack.pack.model.Group;
import com.pack.pack.model.PackItem;
import com.pack.pack.model.User;
import com.pack.pack.services.exception.PackException;

/**
 * 
 * @author Saurav
 *
 */
public interface PackService extends IService {

	/**
	 * 
	 * @param packItem
	 * @param parentId
	 * @param target
	 * @param source
	 * @throws PackException
	 */
	public void forwardPackToGroup(PackItem packItem, String parentId,
			List<Group> target, User source) throws PackException;

	/**
	 * 
	 * @param packItem
	 * @param parentId
	 * @param target
	 * @param source
	 * @throws PackException
	 */
	public void forwardPackToOtherUser(PackItem packItem, String parentId,
			List<User> target, User source) throws PackException;

	/**
	 * 
	 * @param original
	 * @param story
	 * @param creator
	 * @return
	 * @throws PackException
	 */
	public PackItem addStory(PackItem original, String story, User creator)
			throws PackException;
}