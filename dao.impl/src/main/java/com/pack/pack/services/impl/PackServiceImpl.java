package com.pack.pack.services.impl;

import java.util.List;

import com.pack.pack.model.Group;
import com.pack.pack.model.PackItem;
import com.pack.pack.model.User;
import com.pack.pack.services.PackService;
import com.pack.pack.services.exception.PackException;

/**
 * 
 * @author Saurav
 *
 */
public class PackServiceImpl extends BaseService implements PackService {

	@Override
	public PackItem addStory(PackItem original, String story, User creator)
			throws PackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void forwardPackToGroup(PackItem packItem, String parentId,
			List<Group> target, User source) throws PackException {
		// TODO Auto-generated method stub

	}

	@Override
	public void forwardPackToOtherUser(PackItem packItem, String parentId,
			List<User> target, User source) throws PackException {
		// TODO Auto-generated method stub

	}
}