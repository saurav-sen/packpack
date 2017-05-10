package com.pack.pack;

import java.io.InputStream;

import com.pack.pack.model.Pack;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.PackAttachmentType;
import com.pack.pack.model.web.Pagination;
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
	 * @param id
	 * @param loadComments
	 * @return
	 * @throws PackPackException
	 */
	public JPackAttachment getPackAttachmentById(String id, boolean loadComments)
			throws PackPackException;

	/**
	 * 
	 * @param packId
	 * @param fromUserId
	 * @param receipents
	 * @throws PackPackException
	 */
	/*
	 * public void forwardPack(String packId, String fromUserId,
	 * PackReceipent... receipents) throws PackPackException;
	 */

	/**
	 * 
	 * @param userId
	 * @param topicId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JPack> loadLatestPack(String userId, String topicId,
			String pageLink) throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param topicId
	 * @param packId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JPackAttachment> loadPackAttachments(String userId,
			String topicId, String packId, String pageLink)
			throws PackPackException;

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
	 * @param publish
	 * @return
	 * @throws PackPackException
	 */
	public JPack uploadPack(InputStream file, String fileName, String title,
			String description, String story, String topicId, String userId,
			String mimeType, PackAttachmentType type, boolean publish)
			throws PackPackException;

	/**
	 * 
	 * @param file
	 * @param fileName
	 * @param type
	 * @param packId
	 * @param topicId
	 * @param userId
	 * @param title
	 * @param description
	 * @param isCompressed
	 * @return
	 * @throws PackPackException
	 */
	public JPackAttachment updatePack(InputStream file, String fileName,
			PackAttachmentType type, String packId, String topicId,
			String userId, String title, String description,
			boolean isCompressed) throws PackPackException;

	/**
	 * 
	 * @param type
	 * @param packId
	 * @param topicId
	 * @param userId
	 * @param title
	 * @param description
	 * @param attachmentUrl
	 * @param attachmentThumbnailUrl
	 * @param isCompressed
	 * @return
	 * @throws PackPackException
	 */
	public JPackAttachment updatePackFromExternalLink(PackAttachmentType type,
			String packId, String topicId, String userId, String title,
			String description, String attachmentUrl,
			String attachmentThumbnailUrl, boolean isCompressed)
			throws PackPackException;

	/**
	 * 
	 * @param criteria
	 * @param packId
	 * @param userId
	 * @throws PackPackException
	 */
	/*
	 * public void broadcastPack(BroadcastCriteria criteria, String packId,
	 * String userId) throws PackPackException;
	 */

	/**
	 * 
	 * @param criteria
	 * @param packId
	 * @throws PackPackException
	 */
	/*
	 * public void broadcastSystemPack(BroadcastCriteria criteria, String
	 * packId) throws PackPackException;
	 */

	/**
	 * 
	 * @param pack
	 * @return
	 * @throws PackPackException
	 */
	public JPack createNewPack(Pack pack) throws PackPackException;

	/**
	 * 
	 * @param attachmentId
	 * @param packId
	 * @param topicId
	 * @throws PackPackException
	 */
	public void deleteAttachment(String attachmentId, String packId,
			String topicId) throws PackPackException;

	/**
	 * 
	 * @param attachmentId
	 * @param story
	 * @return -- ID of newly created story
	 * @throws PackPackException
	 */
	public String addStoryToAttachment(String attachmentId, String story)
			throws PackPackException;

	/**
	 * 
	 * @param attachmentId
	 * @param userId
	 * @return
	 * @throws PackPackException
	 */
	public String loadAttachmentStory(String attachmentId, String userId)
			throws PackPackException;
}