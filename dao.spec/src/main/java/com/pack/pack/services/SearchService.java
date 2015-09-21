package com.pack.pack.services;

import java.util.List;

import com.pack.pack.model.Group;
import com.pack.pack.services.exception.PackException;

/**
 * 
 * @author Saurav
 *
 */
public interface SearchService extends IService {

	/**
	 * 
	 * @param namePattern
	 * @param membersName
	 * @return
	 * @throws PackException
	 */
	public List<Group> searchGroups(String namePattern, List<String> membersName)
			throws PackException;
}