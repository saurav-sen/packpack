package com.pack.pack;

import java.util.List;

import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public interface IMiscService {

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

	/**
	 * 
	 * @param userId
	 * @param entityId
	 * @param entityType
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JDiscussion> loadDiscussions(String userId,
			String entityId, String entityType, String pageLink)
			throws PackPackException;

	/**
	 * 
	 * @param discussionId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JDiscussion> loadReplies(String discussionId,
			String pageLink) throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param entityId
	 * @param type
	 * @return
	 * @throws PackPackException
	 */
	public List<JComment> loadComments(String userId, String entityId,
			EntityType type) throws PackPackException;

	/**
	 * 
	 * @param discussion
	 * @return
	 * @throws PackPackException
	 */
	public JDiscussion startDiscussion(JDiscussion discussion)
			throws PackPackException;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws PackPackException
	 */
	public JDiscussion getDiscussionBasedOnId(String id)
			throws PackPackException;
}