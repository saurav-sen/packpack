package com.pack.pack.services.impl;

import java.util.List;

import com.pack.pack.model.Group;
import com.pack.pack.model.User;
import com.pack.pack.services.GroupService;
import com.pack.pack.services.exception.PackException;

/**
 * 
 * @author Saurav
 *
 */
public class GroupServiceImpl extends BaseService implements GroupService {

	@Override
	public Group createGroup(List<User> members, String name) throws PackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group joinGroup(Group g1, Group g2, String name)
			throws PackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unsubscribeFromGroup(Group g, User user)
			throws PackException {
		// TODO Auto-generated method stub

	}
}