package com.pack.pack;

import java.io.InputStream;
import java.util.List;

import com.pack.pack.model.PackAttachmentType;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JPack;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public interface IPackService {

	/**
	 * 
	 * @param id
	 * @return
	 * @throws PackPackException
	 */
	public JPack getPackById(String id) throws PackPackException;

	/**
	 * 
	 * @param packId
	 * @param fromUserId
	 * @param userIds
	 * @throws PackPackException
	 */
	public void forwardPack(String packId, String fromUserId, String... userIds)
			throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param pageNo
	 * @return
	 * @throws PackPackException
	 */
	public List<JPack> loadLatestPack(String userId, int pageNo)
			throws PackPackException;

	/**
	 * 
	 * @param comment
	 * @return
	 * @throws PackPackException
	 */
	public JComment addComment(JComment comment) throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param packId
	 * @throws PackPackException
	 */
	public void addLike(String userId, String packId) throws PackPackException;

	/**
	 * 
	 * @param file
	 * @param fileName
	 * @param title
	 * @param description
	 * @param story
	 * @param topicId
	 * @param userId
	 * @param mimeType
	 * @param type
	 * @return
	 * @throws PackPackException
	 */
	public JPack uploadPack(InputStream file, String fileName, String title,
			String description, String story, String topicId, String userId,
			String mimeType, PackAttachmentType type) throws PackPackException;

	/**
	 * 
	 * @param file
	 * @param fileName
	 * @param type
	 * @param packId
	 * @return
	 * @throws PackPackException
	 */
	public JPack updatePack(InputStream file, String fileName,
			PackAttachmentType type, String packId) throws PackPackException;
}