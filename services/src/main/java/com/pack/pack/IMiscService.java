package com.pack.pack;

import com.pack.pack.model.web.JComment;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public interface IMiscService {

	public static enum EntityType {
		PACK, PACK_ATTACHMENT, COMMENT, DISCUSSION
	}

	/**
	 * 
	 * @param userId
	 * @param entityId
	 * @param type
	 * @throws PackPackException
	 */
	public void addLike(String userId, String entityId, EntityType type)
			throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param entityId
	 * @param type
	 * @param comment
	 * @throws PackPackException
	 */
	public void addComment(String userId, String entityId, EntityType type,
			JComment comment) throws PackPackException;
}